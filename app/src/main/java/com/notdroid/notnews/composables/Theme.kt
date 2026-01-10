package com.notdroid.notnews.composables

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val lightNotNewsTheme= lightColorScheme(
  primary = Color(0xFF2196F3),
  secondary = Color(0xFF03A9F4)
)

val darkNotNewsApp= darkColorScheme(
  primary = Color(0xFF0D47A1),
  secondary = Color(0xFF1976D2)
)



@Composable
fun NotNewsTheme(content: @Composable ()->Unit){
  val theme= if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
    if (isSystemInDarkTheme()) {
      dynamicDarkColorScheme(LocalContext.current)
    } else {
      dynamicLightColorScheme(LocalContext.current)
    }
  }else{
    if (isSystemInDarkTheme()) {
      darkNotNewsApp
    } else {
      lightNotNewsTheme
    }
  }
  MaterialTheme(theme, typography = Typography(), ){
    content()
  }
}