package com.notdroid.notnews.composables

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.notdroid.notnews.R
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.recycler.NewsController
import com.notdroid.notnews.stringify
import com.notdroid.notnews.vm.NewsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.GregorianCalendar


@Composable
fun NotNewCard(item:NewsApiLocalSave,controller:NewsController) {
  val imageUrl=remember{item.urlToImage}
  val newsViewModel: NewsViewModel = hiltViewModel()
  val isOffline = remember {mutableStateOf(  false )}
  LaunchedEffect(item) {
    withContext(Dispatchers.IO){
      newsViewModel.isAvailableOffline(item.url) { off ->
        isOffline.value = off
      }
    }
  }
  Card(onClick = {controller.onTouchItem(item)}, Modifier.padding(16.dp)) {
    if (imageUrl.isNotEmpty()){
      AsyncImage(model =item.urlToImage, contentDescription = "", contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxWidth().height(200.dp))
    }
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(16.dp)) {
      Text(item.title,style= MaterialTheme.typography.titleLarge, modifier =  Modifier.padding(bottom = 16.dp))
      Text(item.description, Modifier.padding(bottom = 16.dp))
      Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(
          if (item.sourceName.isNotBlank()&&item.author.isNotBlank()){
            "${item.sourceName} by\n ${item.author}"
          }else{
            item.sourceName.takeIf { it.isNotBlank()}?:item.author
          }
        ,)
        Row() {
          Text(stringify(item.publishedAt, LocalContext.current), softWrap = false)
          if (isOffline.value) {
            Image(painterResource(R.drawable.ic_arrow_downward_24), contentDescription = null)
          }
        }
      }
    }
  }
}


@Preview(showBackground = false, wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE, device = "spec:width=1000px,height=1000px,dpi=320", name = "NotNewCardPreview", group = "news")
@Composable
fun NotNewCardPreview(){
  NotNewsTheme {
    NotNewCard(NewsApiLocalSave(
      0,
      "VentureBeat",
      "Dean Takahashi",
      "Ansys says simulations will close the gap with reality and make the world more sustainable",
      "You may not have heard of Ansys, but it's in the process of being acquired by chip design tool firm Synopsys for \$35 billion.",
      "https://venturebeat.com/games/ansys-says-simulations-will-close-the-gap-with-reality-and-make-the-world-more-sustainable/",
      "https://media.zenfs.com/en/thewrap.com/58791b5d6507adf4c4e88f9fcf8357b0",
      GregorianCalendar.getInstance().apply {
        roll(Calendar.DAY_OF_YEAR,-1)
      }.time,
      false,
      false),
      object :NewsController{
      override fun isAvailableOffline(url: String, isOffline: View) {
      
      }
      
      override fun onTouchItem(item: NewsApiLocalSave) {
  
      }
    })
  }
}

