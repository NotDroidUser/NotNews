package com.notdroid.notnews.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.newsapi.NewsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: NewsApiRepository):ViewModel() {
  private val filter: MutableLiveData<String> = MutableLiveData("")
  private val news = repository.news.asLiveData()

  val theVoid = repository.theVoid.map { it==0L }.asLiveData()

  val filteredNews= MediatorLiveData<List<NewsApiLocalSave>>().apply{
    addSource(news){ list->
      this.postValue(list.filter {element->
        val actualFilter=filter.value
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
    addSource(filter){filter->
      val actualList = news.value?: listOf()
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
      filter.postValue(s)
    }
  }

  fun loadNewData(apiKey: String="",onUI:()->Unit) {
    if (apiKey != "") {
      viewModelScope.launch (Dispatchers.IO){
        try {
          repository.getNewsFromApi(apiKey = apiKey,)

        }catch (_:Exception){

        }
      }
    }
    viewModelScope.launch {
      delay(300)
      onUI()
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