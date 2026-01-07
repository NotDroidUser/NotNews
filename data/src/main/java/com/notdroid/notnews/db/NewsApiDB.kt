package com.notdroid.notnews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.notdroid.notnews.db.dao.NewsApiDao
import com.notdroid.notnews.db.dao.OfflineDao
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.db.entities.NewsSourceLocalSave
import com.notdroid.notnews.db.entities.OfflineArticle
import androidx.room.TypeConverter
import androidx.sqlite.execSQL
import java.util.Date


@TypeConverters(NewsApiDB.DateConverter::class)
@Database(entities = [NewsSourceLocalSave::class, NewsApiLocalSave::class,OfflineArticle::class],version= NewsApiDB.DB_VERSION)
abstract class NewsApiDB:RoomDatabase() {
  abstract fun getNewsApiDao(): NewsApiDao
  abstract fun getOfflineDao(): OfflineDao

  class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
      return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
      return date.time
    }
  }

  companion object{
    @Volatile
    private lateinit var INSTANCE: NewsApiDB
    const val DB_VERSION=1
    private const val DB_NAME="notnews.db"

    fun getInstance(context: Context): NewsApiDB {
      if (!Companion::INSTANCE.isInitialized){
        INSTANCE =Room.databaseBuilder(context, NewsApiDB::class.java, DB_NAME).fallbackToDestructiveMigration(true).build()
      }
      return INSTANCE
    }
  }

}