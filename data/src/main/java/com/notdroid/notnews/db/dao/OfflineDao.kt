package com.notdroid.notnews.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.db.entities.OfflineArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@Dao
interface OfflineDao{
    @Query("Select NewsApiLocalSave.* from OfflineArticle inner join NewsApiLocalSave where OfflineArticle.url==NewsApiLocalSave.url")
    fun getOfflineLoadedArticles(): Flow<List<NewsApiLocalSave>>
  
    @Query("Select offlineHtmlFile from OfflineArticle where url=:url")
    fun isOffline(url:String): String?
  
    @Query("Select * from OfflineArticle where url=:url")
    fun getOfflineArticleByURL(url:String): OfflineArticle?

    @Insert
    fun insertOfflineArticle(value:OfflineArticle):Long

    @Update
    fun updateOfflineArticle(update:OfflineArticle)

    @Query("Delete from offlinearticle where url=:url")
    fun deleteOfflineArticle(url:String)
    
}
