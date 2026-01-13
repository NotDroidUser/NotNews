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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.notdroid.notnews.R

val lightNotNewsTheme= lightColorScheme(
  primary = Color(0xFF2196F3),
  secondary = Color(0xFF03A9F4)
)

val darkNotNewsApp= darkColorScheme(
  primary = Color(0xFF0D47A1),
  secondary = Color(0xFF1976D2)
)

val alegreya=FontFamily(fonts = listOf(
  Font(R.font.alegreya_regular),
  Font(R.font.alegreya_black),
  Font(R.font.alegreya_black_italic),
  Font(R.font.alegreya_bold),
  Font(R.font.alegreya_bold_italic),
  Font(R.font.alegreya_extra_bold),
  Font(R.font.alegreya_extra_bold_italic),
  Font(R.font.alegreya_italic),
  Font(R.font.alegreya_medium),
  Font(R.font.alegreya_medium_italic),
  Font(R.font.alegreya_sc_black),
  Font(R.font.alegreya_sc_black_italic),
  Font(R.font.alegreya_sc_bold),
  Font(R.font.alegreya_sc_bold_italic),
  Font(R.font.alegreya_sc_extra_bold),
  Font(R.font.alegreya_sc_extra_bold_italic),
  Font(R.font.alegreya_sc_italic),
  Font(R.font.alegreya_sc_medium),
  Font(R.font.alegreya_sc_medium_italic),
  Font(R.font.alegreya_sc_regular),
))



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