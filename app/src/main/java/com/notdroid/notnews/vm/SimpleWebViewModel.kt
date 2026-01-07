package com.notdroid.notnews.vm

import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notdroid.notnews.db.entities.OfflineArticle
import com.notdroid.notnews.makeBase64Image
import com.notdroid.notnews.newsapi.OfflineNewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.io.IOException
import java.io.OutputStream
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class SimpleWebViewModel @Inject constructor(private val repository: OfflineNewsRepository):ViewModel() {

  private val okHttpClient = OkHttpClient()

  val offlineArticle = MutableLiveData<OfflineArticle?>()
  var onOfflineOne= false
  var articleUrl:String=""
  var loadOnWebView=false
  
  fun tryGettingOfflineOne(){
    viewModelScope.launch (Dispatchers.IO){
      offlineArticle.postValue(repository.getOfflineArticleByURL(articleUrl).also { offline->
        onOfflineOne=offline!=null
      })
    }
  }
  
  fun saveArticle(title:String, htmlText:String, out: OutputStream, path:String, onSaveNotification:()->Unit){
    viewModelScope.launch(Dispatchers.IO){
      //for future add history
      val offline=repository.getOfflineArticleByURL(articleUrl)
      val doc = Jsoup.parse(htmlText)
      val elementsToChange = doc.select("a")
      for (element in elementsToChange){
        val href = element.attr("href")
        if (href.isNotBlank()){
          val prefix = articleUrl.split("/")[2]
          try{
            if (!URI(href).isAbsolute && !href.startsWith(prefix)) {
              element.attr("href", URI(articleUrl).resolve(href).toString())
            }
          }catch (_:Exception){
            if (!href.startsWith(prefix)){
              element.attr("href", prefix+href)
            }
          }
        }
      }

      doc.body().html().byteInputStream().copyTo(out)

      if (offline!=null){
        repository.updateOfflineArticle(path, articleUrl,title,offline.id)
      }else {
        repository.addOfflineArticle(path, articleUrl,title)
        onOfflineOne=true
      }

      launch (viewModelScope.coroutineContext){
        onSaveNotification()
      }
    }
  }
  
  fun downloadAllImages(content: Element?, progressBar: ProgressBar?, agent:String, soItHasFinished: (String) -> Unit){
    if(content!=null){
      viewModelScope.launch(Dispatchers.IO){
        val allImages = mutableListOf<Node>()
        arrayOf(
          "img",
          "picture",
          "figure",
        ).forEach {
          allImages.addAll(content.getElementsByTag(it))
        }
        for ((index,img) in allImages.withIndex()){
          if(img.attr("src")!=""||img.attr("srcset")!=""||img.attr("poster")!=""){
            var where ="src"
            val url=img.attr("src")
              .ifBlank { where="poster"; img.attr("poster") }
              .ifBlank { where="srcset"; img.attr("srcset").split(' ')[0] }
            try {
              val request = Request.Builder().addHeader(
                "User-Agent",
                agent
              ).url(url).build()
              val response = OkHttpClient().newCall(request).execute()
              if(response.code()==200&&response.body()!=null){
                img.attr(where,makeBase64Image(response.body()!!.bytes(),response.header("content-type")?:"image/png"))
              }
            }catch (_:IOException){
              //well that was awkward so lets keep the base url
            }
          }
          progressBar?.post {
            progressBar.setProgress(30+(index/allImages.size*70),true)
          }
        }
        soItHasFinished(content.outerHtml())
      }
    }
  }
  
  companion object {
    private const val TAG: String="OfflineWebViewModel"
  }
  
}