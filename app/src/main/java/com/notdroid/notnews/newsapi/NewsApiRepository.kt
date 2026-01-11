package com.notdroid.notnews.newsapi

import android.util.Log
import com.google.gson.GsonBuilder
import com.notdroid.notnews.db.dao.NewsApiDao
import com.notdroid.notnews.db.dao.OfflineDao
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.db.newsApi.Errors
import com.notdroid.notnews.db.newsApi.NewsError
import retrofit2.HttpException
import retrofit2.awaitResponse
import java.io.File
import java.util.Date


/**
 * This repository has offline data and also retrieves them from the api online, normally you don't consume this one outside the Dagger impl, so see there how to use it
 * */
class NewsApiRepository(
  private val newsDao: NewsApiDao,
  private val offlineDao: OfflineDao,
  private val service: NewsApiService
)  {

  fun doesUrlHaveOfflineArticle(url:String): Boolean {
    val offlineHtml = offlineDao.isOffline(url)
    return offlineHtml !=null && File(offlineHtml).exists().also {
      if (!it){
        offlineDao.deleteOfflineArticle(url)
      }
    }
  }

  val news=newsDao.getNews()
  val theVoid=newsDao.getHowManyNews()
//  val notOfflineNews=newsDao.getNotOfflineNews()
  val offlineCount=newsDao.getOfflineNewsCount()
  /**
   * ``Maybe you're alone with your app and no more api to consume``
   * */
  class VoidException(message:String):RuntimeException(message)
  
  class NewsApiException(private val newsError: NewsError):RuntimeException(){
    fun getApiError(): Errors {
      val enumError = Errors.isEumError(newsError.message)
      return enumError ?: throw UnknownError()
    }
  }
  
  /**
   * Calls the api and add only the new news to the DB, it will update automatically in their GUI thread
   * @param apiKey The api key of the NewsApi
   * @param query, The query for the api, by default "news", you can change this one in the settings on the app
   * @throws VoidException if the api has an 'stroke' (means that the api maybe no longer exist/not responding anything)
   * @throws NewsApiException meaning that api has throw you an error, that is documented, hopefully
   * */
  @Throws(HttpException::class, NewsApiException::class,RuntimeException::class)
  suspend fun getNewsFromApi(apiKey:String,query: String="news"){
    val response=service.getEverything(apiKey, query = query).awaitResponse()
    if (response.code()!=200) {
      throw HttpException(response)
    }
    val body = response.body()
    if(body !=null) {
      val newsError= NewsError(body)
      if (newsError.message.contains(NewsError.Companion.DEFAULT_ERROR)
        && newsError.code.contains(NewsError.DEFAULT_CODE)
        && !newsError.status.contains(Regex("(404)|(200)")))
      {
        val articles=body.articles
        if (articles != null) {
          val list=newsDao.getRawNews()
          articles.removeIf { newOne ->
            list.any { it.url==newOne.url }
          }
          Log.d("NewsApiRepository", "Articles in DB: ${newsDao.getRawNews().size}", )
          Log.d("NewsApiRepository", "Articles to be added: ${articles.size}", )
          articles.map { news ->
            try {
              newsDao.insertNews(NewsApiLocalSave(news))
            }catch (e:Exception){
              Log.e("NewsApiRepository", "An error has occurred: ",  e )
            }
          }
        }else{
          throw VoidException("Looks Like the API had an stroke so stay calm and look into the void")
        }
      } else {
        throw NewsApiException(newsError)
      }
    }
  }
}