package com.notdroid.notnews.db.newsApi

/**
 * Data class of the NewsAPI Source List into {@link NewsSourceResponse}
 * a example json response for further reference
 * {"id": "abc-news",
 * "name": "ABC News",
 * "description": "Your trusted source for breaking news, analysis, exclusive interviews, headlines, and videos at ABCNews.com.",
 * "url": "https://abcnews.go.com",
 * "category": "general",
 * "language": "en",
 * "country": "us"}
 * */
data class NewsSource(val id:String?,
                      val name:String?,
                      val description:String?,
                      val url:String?,
                      val category:String?,
                      val language:String?,
                      val country:String?)