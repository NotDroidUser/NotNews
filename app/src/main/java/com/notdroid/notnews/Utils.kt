package com.notdroid.notnews

import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.db.entities.NewsSourceLocalSave
import org.apache.commons.codec.binary.Base64
import java.nio.charset.Charset
import java.util.Calendar
import java.util.Date

//A week i think is ok for a "new something" maybe even a month
val sevenDaysBefore by lazy {
  Date().apply {
    time -= 24 * 60 * 60 * 1000 * 7
  }
}


fun openInBrowser(activity: FragmentActivity?, url:String) {
  Intent.createChooser(
    Intent().apply {
      addCategory(Intent.CATEGORY_BROWSABLE)
      setAction(Intent.ACTION_VIEW)
      setData(url.toUri());
    }.also {
      activity?.startActivity(it)
    }, "Open with ...."
  )
}


fun stringify(date:Date,context: Context):String{
  val now=Calendar.getInstance()
  val newsDate=Calendar.getInstance().apply { timeInMillis=date.time }

  val year= now.get(Calendar.YEAR) - newsDate.get(Calendar.YEAR)
  val month = now.get(Calendar.MONTH) - newsDate.get(Calendar.MONTH)
  val days = now.get(Calendar.DAY_OF_YEAR)- newsDate.get(Calendar.DAY_OF_YEAR)
  val hours = now.get(Calendar.HOUR) - newsDate.get(Calendar.HOUR)
  val minutes = now.get(Calendar.MINUTE) - newsDate.get(Calendar.MINUTE)

  if (year>0 && month>=12) return "$year " + context.getText( if (year==1) R.string.year_text else R.string.years_text )

  if (month>0 && days>=30) return "$month " + context.getText(if (month==1) R.string.month_text else R.string.months_text )

  if (days>0 )return "$days " + context.getText( if (days==1) R.string.day_text else R.string.days_text)

  if (hours>0) return "$hours " + context.getText(if (hours==1) R.string.hour_text else R.string.hours_text)

  if (minutes>0) return "$minutes " + context.getText(R.string.minutes_text)

  return date.time.toString()
}


val newsApiItemDiffer = object : DiffUtil.ItemCallback<NewsApiLocalSave>(){
  override fun areItemsTheSame(oldItem: NewsApiLocalSave, newItem: NewsApiLocalSave) = newItem.url==oldItem.url
  override fun areContentsTheSame(oldItem: NewsApiLocalSave, newItem: NewsApiLocalSave) = oldItem==newItem
}

val newsSourceDiffer = object : DiffUtil.ItemCallback<NewsSourceLocalSave>(){
  override fun areItemsTheSame(oldItem: NewsSourceLocalSave, newItem: NewsSourceLocalSave) = newItem.url==oldItem.url
  override fun areContentsTheSame(oldItem: NewsSourceLocalSave, newItem: NewsSourceLocalSave) = oldItem==newItem
}


/**
 * Convert ASCII value of char to regional emoji symbol (like 🇧🇷 🇺🇸 🇦🇷 🇨🇺)
**/
fun getFlagEmoji(countryCode: String): String {
  return countryCode.uppercase()
    .map { char ->
      val base = 0x1F1E6 // Base code point for regional indicator symbols
      val offset = char.code - 'A'.code
      String(Character.toChars(base + offset))
    }
    .joinToString("")
}

//Why not the android one? bc Android 26 min api
fun makeBase64Image(inputImage:ByteArray, mime:String):String{
  val encodeBase64 = Base64.encodeBase64(inputImage)
  return "data:$mime;base64,${encodeBase64.toString(Charset.forName("utf8")) }"
}

fun getStyle(colorString: String,backgroundColor: String,textColor: String):String {
  return """<style>
body {
    margin: 0;
    padding: 16px;
    background-color: ${backgroundColor};
    font-family: 'Roboto', 'Georgia', serif;
    line-height: 1.6;
    color: ${textColor}};
}

.reader-container {
    padding: 20px;
    background-color: white;
}

.reader-title {
    font-size: 2em;
    font-weight: 700;
    color: ${colorString};
    margin-bottom: 20px;
    line-height: 1.2;
}

p {
    font-size: 1.1em;
    margin: 0 0 20px 0;
}

h1,
h2,
h3,
h4,
h5,
h6 {
    color: ${colorString};
    font-weight: 600;
    margin: 20px 0 10px 0;
}

img {
    max-width: 100%;
    height: auto;
    display: block;
    margin: 20px auto;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

a {
    color: ${colorString};
    text-decoration: none;
    border-bottom: 1px solid ${colorString}80;
}

a:hover {
    border-bottom: 2px solid ${colorString};
}

blockquote {
    border-left: 4px solid ${colorString};
    padding-left: 20px;
    margin: 20px 0;
    color: ${textColor}aa;
    font-style: italic;
}

code {
    background-color: ${backgroundColor};
    padding: 2px 6px;
    border-radius: 4px;
    font-family: 'Courier New', monospace;
}

pre {
    background-color: $colorString;
    padding: 15px;
    border-radius: 8px;
    overflow-x: auto;
}

ul li {
    list-style: none;
}
</style>
"""
}

fun isDarkMode(context: Context)= (context.resources.configuration.uiMode and UI_MODE_NIGHT_MASK)==UI_MODE_NIGHT_YES