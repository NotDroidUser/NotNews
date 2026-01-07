package com.notdroid.notnews.fragments

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.notdroid.notnews.NotNewsApp
import com.notdroid.notnews.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PreferencesFragment:PreferenceFragmentCompat() {
  
  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences,rootKey)
    //on change of one refresh
    //do with findPreference<>()
  }
}