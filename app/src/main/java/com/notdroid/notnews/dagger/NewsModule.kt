package com.notdroid.notnews.dagger

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.util.ISO8601Utils
import com.notdroid.notnews.db.NewsApiDB
import com.notdroid.notnews.db.newsApi.CategoriesValues
import com.notdroid.notnews.db.newsApi.SearchInValues
import com.notdroid.notnews.db.newsApi.SortByValues
import com.notdroid.notnews.newsapi.NewsApiRepository
import com.notdroid.notnews.newsapi.NewsApiService
import com.notdroid.notnews.newsapi.OfflineNewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.Date
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NewsModule {
  
  companion object{
    const val BASE_URL = "https://newsapi.org/v2/"
  }

  @Provides
  @Singleton
  fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
      .addConverterFactory(GeneralNewsFactory)
      .build()
  }

  @Provides
  @Singleton
  fun provideNewsApiService(retrofit: Retrofit): NewsApiService {
    return retrofit.create(NewsApiService::class.java)
  }


  @Provides
  @Singleton
  fun provideNewsApiDB(@ApplicationContext context: Context): NewsApiDB {
    return NewsApiDB.getInstance(context.applicationContext)
  }

  @Provides
  @Singleton
  fun provideOfflineWebRepository(newsApiDB: NewsApiDB): OfflineNewsRepository {
    return OfflineNewsRepository(newsApiDB.getOfflineDao(),newsApiDB.getNewsApiDao())
  }

  @Provides
  @Singleton
  fun provideNewsApiRepository(newsApiDB: NewsApiDB, newsApiService: NewsApiService): NewsApiRepository {
    return NewsApiRepository(newsApiDB.getNewsApiDao(),newsApiDB.getOfflineDao(), newsApiService)
  }

  @Provides
  @Singleton
  fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
  }


}

/**
 * This factory makes the converter of the enum classes {@link SearchInValues} {@link SortByValues}
 * {@link CategoriesValues} into strings
 * */
object GeneralNewsFactory: Converter.Factory(){
  class SortByConverter(): Converter<SortByValues, String> {
    override fun convert(sort: SortByValues): String {
      return sort.name
    }
  }

  class SearchInConverter(): Converter<SearchInValues, String> {
    override fun convert(search: SearchInValues): String {
      return search.name
    }
  }

  class CategoriesConverter(): Converter<CategoriesValues, String> {
    override fun convert(category: CategoriesValues): String {
      return category.name
    }
  }
  class DateConverter(): Converter<Date, String> {
    override fun convert(date: Date): String {
      return ISO8601Utils.format(date)
    }
  }

  override fun stringConverter(
    type: Type,
    annotations: Array<out Annotation>,
    retrofit: Retrofit
  ): Converter<*, String>? {
    return when (type) {
      SearchInValues::class.java -> SearchInConverter()
      SortByValues::class.java -> SortByConverter()
      CategoriesValues::class.java -> CategoriesConverter()
      Date::class.java->DateConverter()
      else -> super.stringConverter(type, annotations, retrofit)
    }
  }
}