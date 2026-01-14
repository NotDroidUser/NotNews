package com.notdroid.notnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.notdroid.notnews.composables.AboutPage
import com.notdroid.notnews.composables.NotNewsTheme
import com.notdroid.notnews.databinding.FullComposeBinding

class AboutFragment: Fragment() {
  private lateinit var binding: FullComposeBinding
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding= FullComposeBinding.inflate(layoutInflater,container,false)
    return binding.root
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.root.setContent {
      NotNewsTheme {
        AboutPage()
      }
    }
//    binding.devtext.setOnClickListener {
//      openInBrowser(activity,"https://github.com/NotDroidUser")
//    }
//    binding.dev.setOnClickListener {
//      openInBrowser(activity,"https://github.com/NotDroidUser")
//    }
//    binding.newsapi.setOnClickListener {
//      openInBrowser(activity,"https://newsapi.org/")
//    }
//    binding.newsapiText.setOnClickListener {
//      openInBrowser(activity,"https://newsapi.org/")
//    }
//    binding.undraw.setOnClickListener {
//      openInBrowser(activity,"https://undraw.co/")
//    }
//    binding.undrawText.setOnClickListener {
//      openInBrowser(activity,"https://undraw.co/")
//    }
  }
}