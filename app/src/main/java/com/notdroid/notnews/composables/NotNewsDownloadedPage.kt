package com.notdroid.notnews.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.vm.OfflineNewsViewModel

@Composable
fun NotNewsDownloadedPage(viewModel: OfflineNewsViewModel, onNavigate:(NewsApiLocalSave)->Unit, modifier: Modifier = Modifier) {
  val offlineNews by viewModel.filteredNewsState.collectAsStateWithLifecycle()
  val theVoid by viewModel.theVoidState.collectAsStateWithLifecycle()
  if(theVoid){
      TheVoid()
  }else {
    NotNewsDownloadedList(offlineNews, { item ->
      onNavigate(item)
    })
  }
  
}