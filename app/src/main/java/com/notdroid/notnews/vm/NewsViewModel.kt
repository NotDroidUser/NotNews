package com.notdroid.notnews.vm

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.newsapi.NewsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(val sharedPreferences: SharedPreferences, private val repository: NewsApiRepository):ViewModel() {
  private val filter: MutableStateFlow<String> = MutableStateFlow<String>("")
  private val news = repository.news
  private val offlineNewsCount =  repository.offlineCount
  
  val theVoidState = repository.theVoid.map { it==0L }.stateIn(viewModelScope+Dispatchers.IO, started = SharingStarted.WhileSubscribed(),true)
  val theVoid = repository.theVoid.map { it==0L }.asLiveData()

  val filteredNewsState= combine(news,filter,offlineNewsCount){newsV1,filterV2,_->
    newsV1.filter { element->
      filterV2.isEmpty()||(element.url.contains(filterV2)||
        element.description.contains(filterV2)||
        element.title.contains(filterV2)||
        element.sourceName.contains(filterV2))
    }.map { it to isAvailableOffline(it.url) }
  }.stateIn(viewModelScope.plus(Dispatchers.IO), SharingStarted.WhileSubscribed(),listOf())
  
  val filteredNews= MediatorLiveData<List<NewsApiLocalSave>>().apply{
    addSource(news.asLiveData()){ list->
      this.postValue(list.filter {element->
        val actualFilter=filter.asLiveData().value
        if(actualFilter.isNullOrEmpty()){
          true
        }else {
          element.url.contains(actualFilter)||
          element.description.contains(actualFilter)||
          element.title.contains(actualFilter)||
          element.sourceName.contains(actualFilter)
        }
      })
    }
    addSource(filter.asLiveData()){filter->
      val actualList = news.asLiveData().value?: listOf()
      this.postValue(actualList.filter {element->
        if(filter.isEmpty()){
          true
        }else {
          element.url.contains(filter)||
          element.description.contains(filter)||
          element.title.contains(filter)||
          element.sourceName.contains(filter)
        }
      })
    }
  }

  fun isAvailableOffline(url: String,onUI: (Boolean) -> Unit){
    viewModelScope.launch (Dispatchers.IO){
      val isOffline=repository.doesUrlHaveOfflineArticle(url)
      viewModelScope.launch (Dispatchers.Main){
        onUI(isOffline)
      }
    }
  }

  fun isAvailableOffline(url: String) = repository.doesUrlHaveOfflineArticle(url)
  
  val emptyResults=MediatorLiveData(false).apply {
    addSource(theVoid){
      viewModelScope.launch {
        this@apply.postValue(it||filteredNews.value.isNullOrEmpty())
      }
    }
    addSource(filteredNews){
      viewModelScope.launch {
        this@apply.postValue(it.isNullOrEmpty() || theVoid.value?:false)
      }
    }
  }

  private lateinit var debounceJob: Job

  fun setFilter(s:String){
    if (::debounceJob.isInitialized)
      if (debounceJob.isActive){
        debounceJob.cancel(CancellationException("just more to go"))
      }
    debounceJob=viewModelScope.launch {
      delay(300)
      filter.value=s
    }
  }

  fun loadNewData(apiKey: String="",query:String="",onUI:()->Unit) {
    if (apiKey != "") {
      viewModelScope.launch (Dispatchers.IO){
        try {
          repository.getNewsFromApi(apiKey,query)
        }catch (_:Exception){ }
        with(Dispatchers.Main) {
          delay(300)
          onUI()
        }
      }
    }
    
  }

  fun loadFirstTimeData(apiKey: String="") {
    if (apiKey != "") {
      viewModelScope.launch (Dispatchers.IO){
        try {
          repository.getNewsFromApi(apiKey = apiKey)
        }
        catch (_:Exception){

        }
      }
    }
  }

}