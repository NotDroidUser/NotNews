package com.notdroid.notnews.newsapi

import com.notdroid.notnews.db.newsApi.CategoriesValues
import com.notdroid.notnews.db.newsApi.NewsApiResponse
import com.notdroid.notnews.db.newsApi.NewsSourceResponse
import com.notdroid.notnews.db.newsApi.SearchInValues
import com.notdroid.notnews.db.newsApi.SortByValues
import com.notdroid.notnews.sevenDaysBefore
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.Date

/**
 * Where all the magic of the api is done
 * */
interface NewsApiService{
  /**
   * Calls to the api (needs internet, and that permission must be granted before ask to api)
   * and returns a Call to the every news in internet, to call it later
   * you must at least one of {@link query}, {@link sources}, {@link domains}
   * see also {@linkplain https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes} for the lang param
   * @param apiKey This is your NewsApi Key, if you don't have one you can make one in for test
   * {@linkplain https://newsapi.org/} with a limit of 100 request daily and i think 1000 monthly
   * @param query This is the query about what you want to search in news
   * @param searchIn Use {@link SearchInValues} for this one
   * @param sources The max sources is 20, make sure you separate them by comma ','
   * @param domains The source domains to limit the search (instead of source that is by the api id only) of the make sure you separate them by comma ','
   * @param excludeDomains Make sure you separate them by comma ','
   * @param fromDate The start of the Date range of the news, default to {@link sevenDaysBefore}
   * @param toDate The end of the Date range of the news, default to today (however, the developer api don't have "today", so take that in consideration if you want)
   * @param lang any value from ISO 639, use any in Locale.getISOLanguages() but only one
   * @param sortBy use SortByValues for this one
   * @param pageSize max is 100, by default, 100, if you have bad connection use less but be aware
   * you will be using the call to api anyways
   * @param page This is only for Business and more but there is it
   * @return A object {@link Call<NewsEverything>} you must ensure its valid {@code NewsResult} or {@code NewsError}
   * */
  @GET("everything")
  fun getEverything(@Header("X-Api-Key") apiKey:String,
                    @Query("q") query:String?=null,
                    @Query("searchIn") searchIn: SearchInValues?=null,
                    @Query("sources") sources:String?=null,
                    @Query("domains") domains:String?=null,
                    @Query("excludeDomains") excludeDomains:String?=null,
                    @Query("from") fromDate: Date = sevenDaysBefore,
                    @Query("to") toDate: Date = Date(),
                    @Query("language") lang:String?=null,
                    @Query("sortBy") sortBy: SortByValues?=null,
                    @Query("pageSize") pageSize:Int?=100,
                    @Query("page") page: Int? =1): Call<NewsApiResponse>

  /**
   * Calls to the api (needs internet, and that permission must be granted before ask to api)
   * and returns a Call to the top headlines, to call it later
   * you must at least use one of those
   * {@link query}, {@link sources}, {@link language}, {@link country}, {@link category}
   * see also {@linkplain https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes} for the lang param
   * @param apiKey This is your NewsApi Key, if you don't have one you can make one in for test
   * {@linkplain https://newsapi.org/} with a limit of 100 request daily and i think 1000 monthly
   * @param country Use ISO 3166-1 {@code Locale.getISOCountries()} for this one
   * @param category The source domains to limit the search (instead of source that is by the api id only) of the make sure you separate them by comma ','
   * @param sources The max sources is 20, make sure you separate them by comma ',', don't use with country or category
   * @param lang any value from ISO 639, use any in {@code Locale.getISOLanguages()} but only one (just in case this isn't in the api but works, just a little)
   * @param query This is the query about what you want to search in news
   * @param pageSize max is 100, by default, 100, if you have bad connection use less but be aware
   * you will be using the call to api anyways
   * @param page This is only for Business and more but there is it
   * @return A object {@link Call<NewsEverything>} you must ensure its valid {@code NewsResult} or {@code NewsError}
   * */
  @Deprecated(level = DeprecationLevel.WARNING, message = "Use getEverything with sortBy as SortByValues.popularity\n" +
      "as this api only works for US code")
  @GET("top-headlines")
  fun getTopHeadlines(@Header("X-Api-Key") apiKey:String,
                      @Query("country") country:String?="us",
                      @Query("category") category: CategoriesValues?,
                      @Query("sources") sources:String?,
                      @Query("language") lang:String?,
                      @Query("q") query:String?,
                      @Query("pageSize") pageSize:Int?=100,
                      @Query("page") page: Int? =1): Call<NewsApiResponse>
  /**
   * Calls to the api (needs internet, and that permission must be granted before ask to api)
   * and returns a Call to the sources of the top headlines (sadly not all), to call it later
   * see also {@linkplain https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes} for the lang param
   * @param apiKey This is your NewsApi Key, if you don't have one you can make one in for test
   * {@linkplain https://newsapi.org/} with a limit of 100 request daily and i think 1000 monthly
   * @param category The source domains to limit the search (instead of source that is
   * by the api id only) of the make sure you separate them by comma ','
   * @param lang any value from ISO 639, use any in {@code Locale.getISOLanguages()}
   * but only one (just in case this isn't in the api but works, just a little)
   * @param country Use ISO 3166-1 {@code Locale.getISOCountries()} for this one
   * */
  @Deprecated(level = DeprecationLevel.WARNING, message = "this only works for paid api (recently, at start of the year 2025 it worked) and don't even try to get all sources, only some sites even top news put someones that aren't here")
  @GET("top-headlines/sources")
  fun getSources(@Header("X-Api-Key") apiKey:String?,
                 @Query("category") category: CategoriesValues?=null,
                 @Query("country") country:String?=null,
                 @Query("language") lang:String?=null): Call<NewsSourceResponse>
}