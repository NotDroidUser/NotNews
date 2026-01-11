package com.notdroid.notnews.composables

import android.content.SharedPreferences
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notdroid.notnews.R
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.openInBrowser
import com.notdroid.notnews.vm.NewsViewModel

@Composable
fun NotNewsPage(viewModel: NewsViewModel,onNavigate:(NewsApiLocalSave)->Unit,modifier: Modifier = Modifier) {
  val pull= rememberPullToRefreshState()
  val preferences: SharedPreferences= viewModel.sharedPreferences //TODO for now, migrate to dataStore
  val apiKeyPK=stringResource(R.string.api_key_preferences_key)
  val queryKeyPK=stringResource(R.string.query_preferences_key)
  val webviewPK=stringResource(R.string.webview_preferences_key)
  val litePK=stringResource(R.string.lite_web_preferences_key)
  
  val isRefreshing= remember{ mutableStateOf(false) }
  val context= LocalContext.current
  val news=viewModel.filteredNewsState.collectAsStateWithLifecycle()
  val theVoid=viewModel.theVoidState.collectAsStateWithLifecycle()
  PullToRefreshBox(isRefreshing.value, onRefresh = {
    viewModel.loadNewData(preferences.getString(apiKeyPK,"")?:"",preferences.getString(queryKeyPK
      ,"")?:"") { isRefreshing.value=false }
  },state=pull) {
    if (theVoid.value){
      TheVoid()
    }else{
      NotNewsList(news.value) { item: NewsApiLocalSave ->
//        if ( preferences.getBoolean(webviewPK,false) && preferences.getBoolean(litePK,false)) {
//           //future
//        }else
        if (preferences.getBoolean(webviewPK,false)){
          onNavigate(item)
        } else {
          openInBrowser(context, item.url)
        }
      }
    }
  }
}