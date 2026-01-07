package com.notdroid.notnews.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.notdroid.notnews.db.newsApi.NewsItem
import com.notdroid.notnews.db.newsApi.NewsSource
import java.util.Date

@Entity
data class NewsApiLocalSave(@PrimaryKey(autoGenerate = true)
                            var id: Long = 0,
                            val sourceName:String,
                            var author:String,
                            var title:String,
                            var description:String,
                            var url:String,
                            var urlToImage:String,
                            var publishedAt: Date,
                            @ColumnInfo(name = "hidden")
                            var isHidden:Boolean=false,
                            var isRead:Boolean=false
){

  constructor(newsItem: NewsItem):this(
    sourceName = (newsItem.source?.name?:""),
    author = newsItem.author?:"",
    title = newsItem.title,
    description = newsItem.description?:"",
    url = newsItem.url,
    urlToImage = newsItem.urlToImage?:"",
    publishedAt = newsItem.publishedAt,
    isHidden = false,
    isRead = false
  )

  fun toItem()= NewsItem(
    NewsSource(null,name=sourceName,null,null,null,null,null),
    author,
    title,
    description,
    url,
    urlToImage,
    publishedAt
  )


}