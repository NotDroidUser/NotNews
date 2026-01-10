package com.notdroid.notnews.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.notdroid.notnews.R
import com.notdroid.notnews.composables.NotNewsPage
import com.notdroid.notnews.composables.NotNewsTheme
import com.notdroid.notnews.composables.TheVoid
import com.notdroid.notnews.databinding.FullComposeBinding
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.openInBrowser
import com.notdroid.notnews.recycler.NewsController
import com.notdroid.notnews.vm.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainNewsFragment:Fragment() {

  @Inject
  lateinit var preferences:SharedPreferences
  private val newsViewModel: NewsViewModel by viewModels()
  private lateinit var binding: FullComposeBinding

  @Volatile
  private lateinit var searchView: SearchView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding=FullComposeBinding.inflate(inflater,container,false)
    return binding.root
  }

  val onSearchBackCallback=object:OnBackPressedCallback(false){
    override fun handleOnBackPressed() {
      if (::searchView.isInitialized){
        if (searchView.isIconified.not()||searchView.query!="") {
          searchView.setQuery("",false)
          searchView.isIconified=true
          newsViewModel.setFilter("")
          isEnabled=false
        }
      }else
        isEnabled=false
    }
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//    with(binding){
//      swipe.setOnRefreshListener {
//        if (::preferences.isInitialized){
//          newsViewModel.loadNewData(preferences.getString(getString(R.string.api_key_preferences_key),"")!!){
//            swipe.isRefreshing=false
//          }
//        }
//      }
      val controller = object : NewsController {
        
        override fun onTouchItem(item: NewsApiLocalSave) {
          if (preferences.getBoolean(getString(R.string.webview_preferences_key), false)) {
            findNavController().navigate(MainNewsFragmentDirections.toSimpleWebViewFragment(url = item.url, articleImageUrl = item.urlToImage))
          } else {
            preferences.getBoolean(getString(R.string.lite_web_js_preferences_key), false)
            openInBrowser(activity, item.url)
          }
        }
        
        override fun isAvailableOffline(url: String, isOffline: View) {
          newsViewModel.isAvailableOffline(url) { offline ->
            activity?.runOnUiThread {
              isOffline.visibility=if (offline&&getAutoDL()) View.VISIBLE else View.GONE
              //this one is no longer needed
            }
          }
        }
      }
      binding.root.setContent {
        NotNewsTheme {
          val theVoid=newsViewModel.theVoidState.collectAsStateWithLifecycle(lifecycle)
          if (theVoid.value){
            TheVoid()
          }else{
            NotNewsPage(newsViewModel, controller)
          }
        }
      }
//      composableList.setContent {
//        NotNewsTheme {
//          val news by newsViewModel.filteredNewsState
//            .collectAsStateWithLifecycle(lifecycle)
//          val listState= rememberLazyListState()
//          NotNewsList(news,controller,listState)
//        }
//      }
//      binding.newsList.layoutManager=LinearLayoutManager(requireContext())

//      binding.newsList.adapter= NewsRecyclerAdapter(controller)
//      newsViewModel.filteredNews.observe(viewLifecycleOwner){ list->
//        (newsList.adapter as NewsRecyclerAdapter).apply {
//          val oldSize=this.currentList.size
//          submitList(list)
//          if (preferences.getBoolean(getString(R.string.move_up_key),true)){
//            if (list.size > oldSize) {
//              binding.newsList.scrollToPosition(0)
//            }
//          }
//        }
//      }
//      newsViewModel.theVoid.observe(viewLifecycleOwner){isVoid->
//        if (isVoid){
//          if (::preferences.isInitialized) {
//            if (!::searchView.isInitialized) {
//              newsViewModel.loadFirstTimeData(preferences.getString(getString(R.string.api_key_preferences_key),"")?:"")
//            }
//          }
//        }
//      }
//      newsViewModel.emptyResults.observe(viewLifecycleOwner){empty->
//        if (empty) {
//          thevoid.visibility = View.VISIBLE
//          voidText.visibility = View.VISIBLE
//          composableList.visibility = View.GONE
//        }else{
//          thevoid.visibility=View.GONE
//          voidText.visibility=View.GONE
//          composableList.visibility=View.VISIBLE
//        }
//      }
//    }
    requireActivity().apply {
      addMenuProvider(object:MenuProvider{

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
          menuInflater.inflate(R.menu.news_search_menu, menu)
          menu.findItem(R.id.search_bar)?.let {
            searchView = it.actionView as SearchView
            searchView.queryHint="Search...."
            searchView.setOnQueryTextListener(object : OnQueryTextListener{
              override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                  onSearchBackCallback.isEnabled=searchView.isIconified.not()||searchView.query!=""
                  newsViewModel.setFilter(query)
                }
                return true
              }

              override fun onQueryTextChange(newText: String?): Boolean {
                return onQueryTextSubmit(newText)
              }
            })
            searchView.isIconified=true
          }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
          return when (menuItem.itemId){
            R.id.search_bar->{
              true
            }
            else-> false
          }
        }
      },viewLifecycleOwner)
      onBackPressedDispatcher.addCallback (onSearchBackCallback)
    }
    
    super.onViewCreated(view, savedInstanceState)
  }
  
  private fun getAutoDL() =
    preferences.getBoolean(getString(R.string.autodl_preferences_key), false)
  
}