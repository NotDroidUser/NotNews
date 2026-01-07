package com.notdroid.notnews

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.notdroid.notnews.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.core.content.edit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
  private val nav by lazy { findNavController(R.id.fragment_host) }
  
  @Inject
  lateinit var preferences: SharedPreferences
  private val config by lazy { AppBarConfiguration(navGraph = nav.graph, binding.drawerView) }
  
  private val onDownloadOptionChanged =
    SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
      if (!sharedPreferences.getBoolean(getString(R.string.webview_preferences_key), false)) {
        this.runOnUiThread {
          sharedPreferences.edit(commit = true) {
            putBoolean(getString(R.string.lite_web_preferences_key), false).putBoolean(getString(R.string.autodl_preferences_key), false)
          }
        }
      }
      
      if (!sharedPreferences.getBoolean(getString(R.string.lite_web_preferences_key), false)) {
        this.runOnUiThread {
          sharedPreferences.edit(commit = true) {
            putBoolean(getString(R.string.autodl_preferences_key), false)
          }
        }
      }
      
      if (key == getString(R.string.autodl_preferences_key)) {
        this.runOnUiThread {
          binding.navigationView.menu.findItem(R.id.downloadedNewsFragment)?.isVisible =
            (sharedPreferences.getBoolean(getString(R.string.autodl_preferences_key), false))
        }
      }
    }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    NavigationUI.setupWithNavController(binding.navigationView, nav)
    NavigationUI.setupActionBarWithNavController(this, nav, config)
    binding.navigationView.getHeaderView(0)?.findViewById<TextView>(R.id.version)?.text = buildString {
      append("v")
      append(packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).versionName)
    }
    this.runOnUiThread {
      binding.navigationView.menu.findItem(R.id.downloadedNewsFragment)?.isVisible =
        (preferences.getBoolean(getString(R.string.autodl_preferences_key), false))
    }
  }

  override fun onResume() {
    preferences.registerOnSharedPreferenceChangeListener(onDownloadOptionChanged)
    super.onResume()
  }

  override fun onPause() {
    preferences.unregisterOnSharedPreferenceChangeListener(onDownloadOptionChanged)
    super.onPause()
  }

  override fun onSupportNavigateUp(): Boolean {
    return NavigationUI.navigateUp(nav,config)
  }
}