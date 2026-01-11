package com.notdroid.notnews.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.remember
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.notdroid.notnews.R
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.stringify


@Composable
fun NotNewCard(item:NewsApiLocalSave, onClick:(NewsApiLocalSave)->Unit, isOffline: Boolean, modifier: Modifier=Modifier) {
  val imageUrl=remember{item.urlToImage}
  Card(CombinedModifier(modifier, Modifier
    .padding(16.dp)
    .combinedClickable(
      onClick = { onClick(item) },
      onLongClick = {
        //future
      },
      hapticFeedbackEnabled = true
    ))) {
    if (imageUrl.isNotEmpty()){
      AsyncImage(model =item.urlToImage, contentDescription = "", contentScale = ContentScale.FillBounds, modifier = Modifier
        .fillMaxWidth()
        .height(200.dp))
    }
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(16.dp)) {
      Text(item.title, fontFamily = FontFamily.SansSerif , fontWeight = FontWeight.W400,style= MaterialTheme.typography.titleLarge, modifier =  Modifier.padding(bottom = 16.dp))
      Text(item.description, Modifier.padding(bottom = 16.dp))
      Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(
          if (item.sourceName.isNotBlank()&&item.author.isNotBlank()){
            "${item.sourceName} by\n${item.author}"
          }else{
            item.sourceName.takeIf { it.isNotBlank()}?:item.author
          }
        ,)
        Row() {
          Text(stringify(item.publishedAt, LocalContext.current), softWrap = false)
          if (isOffline) {
            Image(painterResource(R.drawable.ic_arrow_downward_24), contentDescription ="An arrow pointing to bottom, this is an indicator of the article is downloaded")
          }
        }
      }
    }
  }
}


@Preview(showBackground = false, wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE, device = "spec:width=1000px,height=1000px,dpi=320", name = "NotNewCardPreview", group = "news")
@Composable
private fun NotNewCardPreview(){
  NotNewsTheme {
    NotNewCard(PreviewUtils.generateMockApi(1)[0], {},true)
  }
}

