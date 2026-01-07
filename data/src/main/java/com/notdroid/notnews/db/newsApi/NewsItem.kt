package com.notdroid.notnews.db.newsApi

import java.util.Date

/**
 * Data class of the NewsAPI News List into {@link NewsApiResponse}
 * */

data class NewsItem(val source: NewsSource?,
                    val author:String?,
                    val title:String,
                    val description:String?,
                    val url:String,
                    val urlToImage:String?,
                    val publishedAt: Date
)