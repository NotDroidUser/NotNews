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
import androidx.navigation.fragment.findNavController
import com.notdroid.notnews.R
import com.notdroid.notnews.composables.NotNewsDownloadedPage
import com.notdroid.notnews.composables.NotNewsTheme
import com.notdroid.notnews.databinding.FullComposeBinding
import com.notdroid.notnews.vm.OfflineNewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DownloadedNewsFragment:Fragment() {

    @Inject
    lateinit var preferences: SharedPreferences
    private val offlineNewsViewModel: OfflineNewsViewModel by viewModels()
    private lateinit var binding: FullComposeBinding

    @Volatile
    private lateinit var searchView: SearchView

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View {
      binding=FullComposeBinding.inflate(inflater,container,false)
//      binding= FragmentMainBinding.inflate(inflater,container,false)
      return binding.root
    }

    val onSearchBackCallback=object: OnBackPressedCallback(false){
      override fun handleOnBackPressed() {
        if (::searchView.isInitialized){
          if (!searchView.isIconified || searchView.query!="") {
            searchView.setQuery("",false)
            searchView.isIconified=true
            offlineNewsViewModel.setFilter("")
            isEnabled=false
          }
        }else
          isEnabled=false
      }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      /*with(binding){
        binding.swipe.setOnRefreshListener {
          swipe.isRefreshing=false
        }
        binding.newsList.layoutManager= LinearLayoutManager(requireContext())
        binding.newsList.adapter= NewsRecyclerAdapter(object: NewsController {
          override fun onTouchItem(item: NewsApiLocalSave) {
            if (preferences.getBoolean(getString(R.string.webview_preferences_key),false)) {
              findNavController().navigate(DownloadedNewsFragmentDirections.toOfflineWebViewFragment(url = item.url, articleImageUrl = item.urlToImage))
            } else {
              Intent().apply{
                addCategory(Intent.CATEGORY_BROWSABLE)
                setAction(Intent.ACTION_VIEW)
                setData((item.url).toUri());
              }.also{
                startActivity(it)
              }
            }
          }

          override fun isAvailableOffline(url: String,isOffline: View){
            isOffline.visibility=View.VISIBLE
          }
        })
        offlineNewsViewModel.filteredNews.observe(viewLifecycleOwner){ list->
          (binding.newsList.adapter as NewsRecyclerAdapter).submitList(list)
        }
        offlineNewsViewModel.emptyResults.observe(viewLifecycleOwner){empty->
          if (empty) {
            thevoid.visibility = View.VISIBLE
            voidText.visibility = View.VISIBLE
            newsList.visibility = View.GONE
          }else{
            thevoid.visibility= View.GONE
            voidText.visibility= View.GONE
            newsList.visibility= View.VISIBLE
          }
        }
      }*/
      binding.root.setContent {
        NotNewsTheme {
          NotNewsDownloadedPage(offlineNewsViewModel,{item->
            findNavController().navigate(DownloadedNewsFragmentDirections.toOfflineWebViewFragment(url = item.url, articleImageUrl = item.urlToImage))
          })
        }
      }
      requireActivity().apply {
        addMenuProvider(object: MenuProvider {
          override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.news_search_menu, menu)
            menu.findItem(R.id.search_bar)?.let {
              searchView = it.actionView as SearchView
              searchView.queryHint="Search...."
              searchView.setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                  query?.let{
                    onSearchBackCallback.isEnabled=searchView.isIconified.not()||searchView.query!=""
                    offlineNewsViewModel.setFilter(query)
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

}