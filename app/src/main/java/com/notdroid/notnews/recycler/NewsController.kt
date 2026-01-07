package com.notdroid.notnews.recycler

import android.view.View
import com.notdroid.notnews.db.entities.NewsApiLocalSave

interface NewsController{
  fun isAvailableOffline(url:String,isOffline:View)
  fun onTouchItem(item: NewsApiLocalSave)
//  future
//  fun onDelete(item: NewsApiLocalSave)
//  fun onDownloadSource(item: NewsApiLocalSave)
}