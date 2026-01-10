package com.notdroid.notnews.composables

import android.content.SharedPreferences
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notdroid.notnews.R
import com.notdroid.notnews.recycler.NewsController
import com.notdroid.notnews.vm.NewsViewModel

@Composable
fun NotNewsPage(viewModel: NewsViewModel,controller: NewsController,modifier: Modifier = Modifier) {
  val pull= rememberPullToRefreshState()
  val preferences: SharedPreferences= viewModel.sharedPreferences //TODO for now, migrate to dataStore
  val apiKeyPK=stringResource(R.string.api_key_preferences_key)
  val isRefreshing= remember{ mutableStateOf(false) }
  val news=viewModel.filteredNewsState.collectAsStateWithLifecycle()
  val listState= rememberLazyListState()
  PullToRefreshBox(isRefreshing.value, onRefresh = {
    viewModel.loadNewData(preferences.getString(apiKeyPK,"")?:"") { isRefreshing.value=false }
  }) {
    NotNewsList(news.value, controller =controller, listState)
  }
}