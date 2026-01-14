package com.notdroid.notnews.composables

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.notdroid.notnews.R
import com.notdroid.notnews.openInBrowser


@Composable
fun AboutPage(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val scroll = rememberScrollState()
  var license by remember { mutableStateOf(false) }
  Scaffold(floatingActionButton = {
    FloatingActionButton(content = {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        AnimatedContent(license, transitionSpec = {
          (scaleIn(initialScale = 0.1f, animationSpec = tween(200, delayMillis = 160)) + fadeIn(animationSpec = tween(100, delayMillis = 80)))
            .togetherWith(scaleOut(targetScale = 0.1f, animationSpec = tween(200)) + fadeOut(animationSpec = tween(100)))
        }) { state ->
          when (state) {
            true -> {
              Icon(painter = painterResource(R.drawable.baseline_art_track_24), "A picture and text")
            }
            false -> {
              Icon(painter = painterResource(R.drawable.baseline_subject_24), "Text Lines")
            }
            
          }
        }
        Text("Licenses")
      }
    }, onClick = {
      license = !license
    })
  }) { paddingValues ->
    AnimatedContent(license, transitionSpec = {
      val fadeSpec = tween<Float>(100, delayMillis = 80)
      val expandSpec = tween<IntSize>(200, delayMillis = 160)
      val shrinkSpec = tween<IntSize>(200)
      if (initialState) {
        (expandHorizontally(expandFrom = Alignment.CenterHorizontally, animationSpec = expandSpec) + fadeIn(animationSpec = fadeSpec))
          .togetherWith(shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally, animationSpec = shrinkSpec) + fadeOut(animationSpec = tween(100)))
      } else {
        (expandHorizontally(expandFrom = Alignment.CenterHorizontally, animationSpec = expandSpec) + fadeIn(animationSpec = fadeSpec))
          .togetherWith(shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally, animationSpec = shrinkSpec) + fadeOut(animationSpec = tween(100)))
      }
    }) { state ->
      when (state) {
        true -> Column(modifier.padding(paddingValues), verticalArrangement = Arrangement.SpaceBetween) {
          val libraries by produceLibraries(R.raw.aboutlibraries)
          LibrariesContainer(libraries, Modifier.fillMaxSize())
        }
        
        false ->
          Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()
            .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), onClick = { openInBrowser(context, "https://github.com/NotDroidUser") }) {
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(8.dp)) {
                Column(verticalArrangement = Arrangement.SpaceAround, modifier = Modifier
                  .padding(8.dp)
                  .fillMaxHeight()
                  .weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                  Row(verticalAlignment = Alignment.Bottom){
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge.copy(fontFeatureSettings = "smcp"), fontFamily = alegreya, fontWeight = FontWeight.Medium)
                    
                    Text (stringResource(R.string.app_version))
                  }
                  Text("A news app, without ads, made with ❤\uFE0F.")
                  Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)) {
                    Button(onClick = { openInBrowser(context, "https://github.com/NotDroidUser/NotNews/?tab=MIT-1-ov-file#") }) {
                      Text("License -> ")
                    }
                    Button(onClick = { openInBrowser(context, "https://github.com/NotDroidUser/NotNews/releases") }) {
                      Text("Site -> ")
                    }
                  }
                }
                
              }
            }
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), onClick = { openInBrowser(context, "https://github.com/NotDroidUser") }) {
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(8.dp)) {
                Column(verticalArrangement = Arrangement.SpaceAround, modifier = Modifier
                  .padding(8.dp)
                  .fillMaxHeight()
                  .weight(1f), horizontalAlignment = Alignment.End) {
                  Text("This app is brought to you by:")
                  Text("NotDroidUser", style = LocalTextStyle.current.copy(fontFeatureSettings = "smcp"), fontWeight = FontWeight.Medium)
                }
                Column(verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxHeight()) {
                  //animate this one
                  Image(painter = painterResource(R.mipmap.developer), "A green pixelated android", modifier = Modifier.size(((24 + 16) * 2).dp))
                }
              }
            }
            LicenseCard(context, "This app uses NewsApi. This app is not affiliated with, sponsored by, or endorsed by NewsAPI. All trademarks and brand names are the property of their respective owners.", "https://undraw.co/license", "https://newsapi.org/", image = R.mipmap.nalogo)
            LicenseCard(context, "Most of the assets are courtesy of unDraw team, huge thanks to them!", "https://newsapi.org/pricing", "https://undraw.co/", image = R.drawable.undraw)
            LicenseCard(context, "App Icon by Twitter Twemoji via SVGRepo", "https://github.com/twitter/twemoji?ref=svgrepo.com", "https://www.svgrepo.com/", image = R.drawable.ic_launcher_foreground)
            LicenseCard(context, "OldNews icon on the NavBar by Phosphor via SVGRepo", "https://github.com/phosphor-icons/phosphor-icons", "https://www.svgrepo.com/", image = R.drawable.twotone_newspaper_clipping)
            Spacer(Modifier.size(0.dp, 100.dp))
            
          }
      }
    }
  }
}

@Composable
private fun LicenseCard(context: Context, about: String, licenseLink: String, siteLink: String, @DrawableRes image: Int) {
  Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .height(IntrinsicSize.Min)) {
      Column(modifier = Modifier
        .padding(8.dp)
        .weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(about)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
          Button(onClick = { openInBrowser(context, licenseLink) }) {
            Text("License ->")
          }
          Button(onClick = { openInBrowser(context, siteLink) }) {
            Text("Site ->")
          }
        }
        
      }
      if (image != 0) {
        Column(verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxHeight()) {
          Image(painter = painterResource(image), null, modifier = Modifier.size(((24 + 16) * 2).dp))
        }
      }
    }
  }
}

@Preview(showSystemUi = true, wallpaper = Wallpapers.NONE, showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun PreviewAboutPage() {
  NotNewsTheme {
    AboutPage()
  }
}