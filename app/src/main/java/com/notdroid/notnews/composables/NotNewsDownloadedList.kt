package com.notdroid.notnews.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.notdroid.notnews.db.entities.NewsApiLocalSave

@Composable
fun NotNewsDownloadedList(newsList: List<NewsApiLocalSave>, onClick:(NewsApiLocalSave)->Unit, modifier: Modifier= Modifier){
  LazyColumn() {
    items(newsList.size){key->
      NotNewCard(newsList[key],onClick, isOffline = true)
    }
  }
}

@Preview
@Composable
private fun PreviewNotNewsDownloadList() {
  NotNewsDownloadedList(PreviewUtils.generateMockApi(10), {})
}