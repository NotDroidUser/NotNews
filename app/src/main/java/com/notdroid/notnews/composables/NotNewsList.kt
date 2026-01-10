package com.notdroid.notnews.composables

import android.view.View
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.recycler.NewsController

@Composable
fun NotNewsList(news: List<Pair<NewsApiLocalSave,Boolean>>, controller: NewsController, listState: LazyListState) {
  LazyColumn(state=listState) {
    items(news.size,){ key->
      val (news,isOffline)=  news[key]
      NotNewCard(news,controller,isOffline)
    }
  }
}

@Preview(name = "NotNewsFrontPage", showBackground = false, group = "NotNews")
@Composable
private fun PreviewNotNewsList(){
  NotNewsTheme {
    NotNewsList(PreviewUtils.generateMockApi(10).map { it to true }, controller = object :NewsController{
      override fun isAvailableOffline(url: String, isOffline: View) {
      }
      override fun onTouchItem(item: NewsApiLocalSave) {
      }
    },rememberLazyListState())
  }
}