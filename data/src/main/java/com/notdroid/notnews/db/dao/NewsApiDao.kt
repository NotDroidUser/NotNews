package com.notdroid.notnews.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.db.entities.NewsSourceLocalSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Dao
interface NewsApiDao{

  @Query("select * from NewsApiLocalSave order by publishedAt Desc")
  fun getRawNews(): List<NewsApiLocalSave>

  @Query("select * from NewsApiLocalSave where hidden==:onlyDisabled order by publishedAt Desc ")
  fun getNews(onlyDisabled:Boolean=false): Flow<List<NewsApiLocalSave>>

  @Query("select NewsApiLocalSave.*, OfflineArticle.url as nullable " +
      "from NewsApiLocalSave left join OfflineArticle on OfflineArticle.url==NewsApiLocalSave.url " +
      "where nullable==NULL order by publishedAt Desc")
  fun getNotOfflineNews(): Flow<List<NewsApiLocalSave>>

  @Query("select Count(id) from NewsApiLocalSave ")
  fun getHowManyNews(): Flow<Long>
  
  @Insert
  fun insertNews(news: NewsApiLocalSave):Long

}