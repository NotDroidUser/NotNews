package com.notdroid.notnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.notdroid.notnews.databinding.FragmentAboutBinding
import com.notdroid.notnews.openInBrowser

class AboutFragment: Fragment() {
  lateinit var binding: FragmentAboutBinding
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding= FragmentAboutBinding.inflate(layoutInflater,container,false)
    return binding.root
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.devtext.setOnClickListener {
      openInBrowser(activity,"https://github.com/NotDroidUser")
    }
    binding.dev.setOnClickListener {
      openInBrowser(activity,"https://github.com/NotDroidUser")
    }
    binding.newsapi.setOnClickListener {
      openInBrowser(activity,"https://newsapi.org/")
    }
    binding.newsapiText.setOnClickListener {
      openInBrowser(activity,"https://newsapi.org/")
    }
    binding.undraw.setOnClickListener {
      openInBrowser(activity,"https://undraw.co/")
    }
    binding.undrawText.setOnClickListener {
      openInBrowser(activity,"https://undraw.co/")
    }
  }
}