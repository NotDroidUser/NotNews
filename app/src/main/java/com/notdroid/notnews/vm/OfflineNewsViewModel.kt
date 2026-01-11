package com.notdroid.notnews.vm

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.newsapi.OfflineNewsRepository
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
class OfflineNewsViewModel @Inject constructor(repository: OfflineNewsRepository):ViewModel() {
  private val filter = MutableStateFlow("")
  private val news = repository.offlineArticles
  
  val theVoidState=repository.offlineArticles.map { it.isEmpty() }.stateIn(viewModelScope+Dispatchers.IO, SharingStarted.WhileSubscribed(),true)
  val emptyResults=repository.offlineArticles.map { it.isEmpty() }.asLiveData()

  val filteredNewsState= combine(news,filter){ news, filter->
    news.filter {element->
      filter.isEmpty()||(
        element.url.contains(filter)||
        element.description.contains(filter)||
        element.title.contains(filter)||
        element.sourceName.contains(filter))
    }
  }.stateIn(viewModelScope+Dispatchers.IO, SharingStarted.WhileSubscribed(), listOf())

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

}