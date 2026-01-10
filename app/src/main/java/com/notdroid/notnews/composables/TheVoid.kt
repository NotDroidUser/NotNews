package com.notdroid.notnews.composables

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.notdroid.notnews.R

@Composable
fun TheVoid(){
  Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier=Modifier.fillMaxHeight(1f)) {
    Image(painter = painterResource(R.drawable.resource_void),"A person seeing into the void", contentScale = ContentScale.Fit)
    Spacer(Modifier.size(16.dp))
    Text("Noting to see here, just the void.",style = MaterialTheme.typography.titleLarge) //todo bug the theme don't render the text as white on dark mode if no scaffold
  }
}

@Preview(wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE, name = "The Void", group = "NotNews", showSystemUi = false, showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun PreviewTheVoid(){
  NotNewsTheme {
    TheVoid()
  }
}