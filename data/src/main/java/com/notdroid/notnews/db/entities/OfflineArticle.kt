package com.notdroid.notnews.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OfflineArticle(@PrimaryKey(autoGenerate = true)
                          var id: Long = 0,
                          val offlineHtmlFile: String,
                          val url:String,
                          val title: String)