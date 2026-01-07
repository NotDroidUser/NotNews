package com.notdroid.notnews.newsapi

import androidx.lifecycle.viewModelScope
import com.notdroid.notnews.db.dao.NewsApiDao
import com.notdroid.notnews.db.dao.OfflineDao
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.db.entities.OfflineArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dankito.readability4j.Article
import java.io.File
import java.util.Date

class OfflineNewsRepository(private val offlineDao: OfflineDao, private val newsApiDao: NewsApiDao) {

  val offlineArticles=offlineDao.getOfflineLoadedArticles()

  fun getOfflineArticleByURL(url: String)=offlineDao.getOfflineArticleByURL(url)

//  future
//  fun createIfDontExist(article: Article, articleUrl: String, articleImageUrl:String){
//    newsApiDao.insertNews(NewsApiLocalSave(0, "","", title = article.title?:articleUrl,article.articleContent?.wholeText()?.slice(0..100)?:"",articleUrl,articleImageUrl,Date(),false,true))
//  }
  
  fun addOfflineArticle(path:String, url: String,title:String)=offlineDao.insertOfflineArticle(
    OfflineArticle(offlineHtmlFile = path, url = url, title = title)
  )
  
  fun updateOfflineArticle(path:String, url: String,title:String,id:Long) {
    val offline =getOfflineArticleByURL(url)?.offlineHtmlFile
    if(offline!=path && offline!=null){
      val file = File(offline)
      if (file.exists()) {
        file.delete()
      }
    }
    offlineDao.updateOfflineArticle(
      OfflineArticle(offlineHtmlFile = path, url = url, title = title, id = id)
    )
  }
  

}