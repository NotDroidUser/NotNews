package com.notdroid.notnews.db.newsApi

/* *
 * This is the common api response for News, {@link NewsApiService.getEverything} and
 * {@link NewsApiService.getTopHeadlines} uses this to retrieve a success or an error
 * from the NewsAPI site
 * */
data class NewsApiResponse(val status:String,
                          //InNormalCase
                           val totalResults:Int?,
                           val articles:ArrayList<NewsItem>?,
                          //inCaseOfError
                           val code:String?,
                           val message: String?)