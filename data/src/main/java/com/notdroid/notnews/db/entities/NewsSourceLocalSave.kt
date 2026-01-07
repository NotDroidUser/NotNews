package com.notdroid.notnews.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.notdroid.notnews.db.newsApi.NewsSource

@Entity
data class NewsSourceLocalSave(@PrimaryKey(autoGenerate = true)
                      var id: Long = 0,
                      val sourceId:String,
                      val name:String,
                      val description:String,
                      val url:String,
                      val category:String,
                      val language:String,
                      val country:String){

  constructor(newsSource: NewsSource):this(
    sourceId = newsSource.id?:"",
    name = newsSource.name?:"",
    description = newsSource.description?:"",
    url = newsSource.url?:"",
    category = newsSource.category?:"",
    language = newsSource.language?:"",
    country = newsSource.country?:""
  )

  fun toItem()= NewsSource(
    sourceId,
    name,
    description,
    url,
    category,
    language,
    country
  )

}

