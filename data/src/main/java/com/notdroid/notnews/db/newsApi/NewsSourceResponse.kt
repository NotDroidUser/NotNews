package com.notdroid.notnews.db.newsApi

/**
 * Data class for retrieve all NewsAPI Sources, this should be called 1 time maybe in a month
 * */
data class NewsSourceResponse(val status:String,
                              val sources:ArrayList<NewsSource>?,
                              //inCaseOfError
                              val code:String?,
                              val message: String?)