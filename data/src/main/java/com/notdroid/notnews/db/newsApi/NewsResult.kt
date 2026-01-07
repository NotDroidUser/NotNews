package com.notdroid.notnews.db.newsApi

/**
 * Data class to make {@link NewsApiResponse} into a non null Result for ease work if it is one
 * */
data class NewsResult(val status:String,
                      val totalResults:Int,
                      val articles:ArrayList<NewsItem>){
  constructor(everything: NewsApiResponse):this(everything.status,everything.totalResults?:0,everything.articles?: arrayListOf())
}