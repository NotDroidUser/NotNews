package com.notdroid.notnews.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import kotlin.random.Random

@Composable
fun NotNewsList(newsList: List<Pair<NewsApiLocalSave,Boolean>>, onClick:(NewsApiLocalSave)->Unit) {
  val listState= rememberLazyListState()
  val itemSize= remember { mutableIntStateOf(newsList.size)}
  LazyColumn(state=listState) {
    items(newsList.size){ key->
      val (news,isOffline) = newsList[key]
      NotNewCard(news,onClick,isOffline)
    }
  }
  
  if(itemSize.intValue!=newsList.size){
    if(newsList.size>itemSize.intValue&&itemSize.intValue!=0){
      listState.requestScrollToItem(0)
    }
    itemSize.intValue=newsList.size
  }
}

@Preview(name = "NotNewsFrontPage", showBackground = false, group = "NotNews")
@Composable
private fun PreviewNotNewsList(){
  NotNewsTheme {
    NotNewsList(PreviewUtils.generateMockApi(10).map { it to (Random.nextInt()%2==0) }, {})
  }
}