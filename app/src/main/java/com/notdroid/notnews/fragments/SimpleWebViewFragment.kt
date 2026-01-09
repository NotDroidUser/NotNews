package com.notdroid.notnews.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.notdroid.notnews.MainActivity
import com.notdroid.notnews.R
import com.notdroid.notnews.databinding.SimpleWebviewBinding
import com.notdroid.notnews.db.entities.OfflineArticle
import com.notdroid.notnews.getStyle
import com.notdroid.notnews.isDarkMode
import com.notdroid.notnews.openInBrowser
import com.notdroid.notnews.vm.SimpleWebViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dankito.readability4j.Readability4J
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.Sha2Crypt
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject

@AndroidEntryPoint
class SimpleWebViewFragment:Fragment(){
  
  fun backColor():String {
    return "#%06X".format(0xFFFFFF and
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ContextCompat.getColor(requireContext(),if (isDarkMode(requireContext()))
          android.R.color.system_neutral1_1000
        else
          android.R.color.system_neutral1_50)
      } else{
        ContextCompat.getColor(requireContext(),
          if (!isDarkMode(requireContext()))
            android.R.color.background_light
          else
            android.R.color.background_dark
        )
      }
    )
  }
  
  fun textColor ():String{
    return "#%06X".format( 0xFFFFFF and TypedValue().let {
      requireContext().theme.resolveAttribute(
        com.google.android.material.R.attr.colorOnBackground,
        it,
        true
      )
      it.data
    })
  }
  
  fun getColor():Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      ContextCompat.getColor(requireContext(), android.R.color.system_accent1_600)
    } else {
      TypedValue().let {
        requireContext().theme.resolveAttribute(
          com.google.android.material.R.attr.colorPrimary,
          it,
          true
        )
        it.data
      }
    }
  }
  
  fun colorString():String { return "#%06X".format( 0xFFFFFF and getColor()) }
  
  private lateinit var binding:SimpleWebviewBinding
  private val args by navArgs<SimpleWebViewFragmentArgs>()
  
  private val offline:SimpleWebViewModel by viewModels()
  

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding=SimpleWebviewBinding.inflate(inflater,container,false)
    return binding.root
  }

  
  @Inject
  lateinit var preferences: SharedPreferences

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if(offline.articleUrl.isBlank()){
      offline.articleUrl=args.url
    }
    (activity as? MainActivity)?.let { activity ->
    
    
    }
    with(binding){
      swipe.setOnRefreshListener {
        reloadArticle()
        lifecycleScope.launch (Dispatchers.IO){
          delay(1000)
          swipe.isRefreshing=false
        }
      }
      if (getLiteWebView()&&!offline.loadOnWebView) {
        setupLiteWebView()
        offline.tryGettingOfflineOne()
        offline.offlineArticle.observe(viewLifecycleOwner) { offlineArticle ->
          if ((offlineArticle != null) && getAutoDL()
          ) {
            loadArticle(offlineArticle)
          } else
            loadArticle(offline.articleUrl)
        }
      } else {
        setupWebView()
        webview.loadUrl(offline.articleUrl)
      }
    }
  }
  
  private fun reloadArticle() {
    if (getLiteWebView()) {
      setupLiteWebView()
      loadArticle(offline.articleUrl)
    } else {
      setupWebView()
      binding.webview.loadUrl(offline.articleUrl)
    }
  }
  
  private fun setupWebView() {
    with(binding) {
      webview.webViewClient= WebViewClient()
      webview.webChromeClient = object: WebChromeClient(){
        override fun onReceivedTitle(view: WebView?, title: String?) {
          super.onReceivedTitle(view, title)
          activity?.runOnUiThread {
            activity?.setTitle(title?:view?.title?:activity?.title)
          }
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
          super.onProgressChanged(view, newProgress)
          setProgress(newProgress)
          if (newProgress==100){
            swipe.isRefreshing=false
          }
        }
      }
    }
  }
  
  private fun showErrorView(){
    with(binding){
      progressBar.progress = 0
      progressBar.visibility= View.GONE
      webview.visibility=View.GONE
      errorImage.visibility=View.VISIBLE
      errorText.visibility=View.VISIBLE
      reload.visibility=View.VISIBLE
      errorTitle.visibility= View.VISIBLE
    }
  }
  private fun hideErrorView(){
    with(binding){
      webview.visibility=View.VISIBLE
      progressBar.visibility= View.VISIBLE
      errorImage.visibility=View.GONE
      errorText.visibility=View.GONE
      reload.visibility=View.GONE
      someExtra.visibility= View.GONE
      errorTitle.visibility= View.GONE
    }
  }
  
  private fun showError(code: Int) {
    with(binding){
      showErrorView()
      
      errorText.setOnClickListener {}
      var errorTextPlain:String
      var errorTitleText:String
      var errorExtra=""
      when (code) {
        502 -> {
          errorImage.setImageResource(R.drawable.resource_502)
          errorTitleText="Looks like the server connection has an issue"
          errorTextPlain="The page you are looking for, is under renovation, or their servers have no internet, we have send a bot for help them on your behalf with some good vibes for their quick recovery."
        }
        in 500 until 600 -> {
          errorImage.setImageResource(R.drawable.resource_503)
          errorTitleText="Looks like the server had an issue"
          errorTextPlain="The page you are looking for, is under renovation, or its servers are under a curse, a programmer is trying to break that curse that is their servers just now.\n"
        }
        404 -> {
          errorImage.setImageResource(R.drawable.resource_404)
          errorTitleText="Page not found"
          errorTextPlain="The page you are looking for, might have been removed had its name changed or is " +
            "temporarily unavailable, i already looked on the moon just in case it was there."
        }
        403,406 -> {
          errorImage.setImageResource(R.drawable.resource_403)
          errorTitleText="Page has blocked your request"
          errorTextPlain="The page you are looking for, is blocked for your country or the admin had blocked you, i'm going somewhere because in this ip there is nothing to do."
          errorExtra="Maybe a VPN?"
        }
        103->{
          //http code 103 is not supported by okhttp atm
          errorImage.setImageResource(R.drawable.resource_103)
          errorTitleText="Looks like this page has some that i can't decode"
          errorTextPlain="The page you are looking for, is on internet, but is too abstract, Try seeing the page on a simple webview, because it wants me to do something «i don't want to» and then fetch it. (Search for how code 103 works)"
          reload.isInvisible=true
        }
        999 -> {
          //runtime error on the article parsing
          //'the void article'
          errorImage.setImageResource(R.drawable.resource_void)
          errorTitleText="Page looks like don't have a article at all"
          errorTextPlain="The page you are looking for, is blocked with cloudflare or cannot be get by this way, i'm looking into the result it has given, a void like this one, try opening in a browser or try again, maybe you get something different."
        }
        else->{
          //'bad connection'
          errorImage.setImageResource(R.drawable.resource_noconnection)
          errorTitleText="Looks like you don't have any connection"
          errorTextPlain="The page you are looking for, is on internet, and right now you don't have that so “press on” wifi, mobile data, and airplane mode, all you can make to restore it."
          errorExtra="Try with VPN?"
        }
      }
      errorTitle.text=errorTitleText
      errorText.text=errorTextPlain
      if( errorExtra.isNotBlank()){
        someExtra.text=errorExtra
        someExtra.visibility = View.VISIBLE
        someExtra.setOnClickListener {
          openInBrowser(activity, "http://www.reddit.com/r/VPN/wiki/faq")
        }
      } else
        someExtra.visibility = View.GONE
      reload.setOnClickListener{
        loadArticle(url = offline.articleUrl)
      }
    }
  }
  
  private fun showErrorOnWebview(code:Int) {
    with(binding) {
      progressBar.progress = 0
      progressBar.visibility= View.GONE
      when (code) {
        502 -> {
          webview.loadUrl("file:///android_asset/502.html")
        }
        
        in 500 until 600 -> {
          webview.loadUrl("file:///android_asset/5xx.html")
        }
        
        403,406 -> {
          webview.loadUrl("file:///android_asset/403.html")
        }
        
        404 -> {
          webview.loadUrl("file:///android_asset/404.html")
        }
        
        103->{
          webview.loadUrl("file:///android_asset/103.html")
        }
        
        999->{
          webview.loadUrl("file:///android_asset/void.html")
        }
        
        else -> {
          webview.loadUrl("file:///android_asset/noconnection.html")
        }
      }
    }
  }
  
  private fun loadArticle(url: String) {
    with(binding) {
      hideErrorView()
      val request = Request.Builder().addHeader(
        "User-Agent",
        getLiteWebViewAgent()!!
      ).url(url).build()
      request.headers.toMultimap().forEach { header ->
        Log.d("loadArticle", "Header: ${header.key}")
        Log.d("loadArticle", "Value: ${header.value.joinToString()}")
      }
      setProgress(10)
      OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          setProgress(100)
          activity?.runOnUiThread {
            if(getJsOrLib()){
              webview.loadUrl("file:///android_asset/noconnection.html")
            }else{
              showError(0)
            }
          }
        }

        override fun onResponse(call: Call, response: Response) {
          val code = response.code
          if(code!=200){
            activity?.runOnUiThread {
              if(getJsOrLib()){
                showErrorOnWebview(code)
              }else{
                showError(code)
              }
            }
            return
          }
          val html = response.body?.string() ?: ""
          if (getJsOrLib()) {
            val jsonHtml = JSONObject.quote(html)
            activity?.runOnUiThread {
              webview.evaluateJavascript("processArticle($jsonHtml,\'${args.articleImageUrl}\')", null)
            }
          } else {
            val article = Readability4J(offline.articleUrl,html).parse()
            setProgress(30)
            if(article.content!=null) {
              activity?.runOnUiThread {
                (activity as MainActivity?)?.let {
                  it.supportActionBar?.title=article.title
                }
              }
              article.content?.let{
                var child = article.articleContent?.firstElementChild()
                if(child!=null && child.tagName()=="div"){
                    child=child.firstElementChild()
                }
                child?.before("<h1 class=\"reader-title\">" + article.title + "</h1>")
                if(!it.contains(args.articleImageUrl))
                  child?.before("<img src=\"${args.articleImageUrl}\" />")
              }
              if(getAutoDL()){
                progressBar.post{
                  Toast.makeText(progressBar.context, "Downloading images", Toast.LENGTH_SHORT).show()
                }
                offline.downloadAllImages(article.articleContent,progressBar,getLiteWebViewAgent()!!) { savedImagesContent:String ->
                  val (path:String,out:OutputStream)= newFile();
                  offline.saveArticle( title = article.title?:offline.articleUrl, htmlText =  savedImagesContent,out,path, onSaveNotification = {
                    progressBar.post{
                      Toast.makeText(progressBar.context, "Saved", Toast.LENGTH_SHORT).show()
                      setProgress(100)
                    }
                  })
                  activity?.runOnUiThread {
                    loadOnWebView(savedImagesContent)
                  }
                }
              }
              else{
                activity?.runOnUiThread {
                  progressBar.visibility=View.GONE
                  webview.loadDataWithBaseURL(
                    offline.articleUrl,
                    buildpageHTML(article.content!!),
                    "text/html",
                    null,
                    offline.articleUrl
                  )
                }
              }
            } else {
              showError(999)
            }
          }
        }
      })
    }
  }
  
  private fun newFile(): Pair<String, OutputStream> {
    val filesDir = requireContext().filesDir
    var name = Base64.encodeBase64(Sha2Crypt.sha256Crypt(offline.articleUrl.toByteArray()).toByteArray()).toString(charset("utf-8")) + ".html"
    val actualFile = File(filesDir, name)
    if(actualFile.exists()&&!offline.onOfflineOne){
        name= Base64.encodeBase64(Sha2Crypt.sha512Crypt(offline.articleUrl.toByteArray()).toByteArray()).toString(charset("utf-8")) + ".html"
    }
    val out= requireContext().openFileOutput(name, Context.MODE_PRIVATE)
    val path= actualFile.toString()
    return path to out;
  }
  
  
  private fun loadArticle(offlineArticle: OfflineArticle) {
    setProgress(100)
    val data = File(offlineArticle.offlineHtmlFile).readText()
    if (getJsOrLib()) {
        offline.viewModelScope.launch(Dispatchers.IO) {
          offlineArticle
          val jsonHtml = JSONObject.quote(data)
          //interesting thing is you can load data through this on a back thread(that is not good idea) but cannot load a simple url in a non main thread
          launch (Dispatchers.Main) {
            binding.webview.evaluateJavascript("putArticle($jsonHtml)", null)
            (activity as MainActivity?)?.supportActionBar?.title = offlineArticle.title
          }
      }
    }
    else{
      loadOnWebView(data)
      (activity as MainActivity?)?.supportActionBar?.title = offlineArticle.title
    }
  }
  
  fun loadOnWebView(webContent: String){
    var page = buildpageHTML(webContent)
    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
      page=page.replace("#","%23")
    }
    binding.webview.loadData(page,"text/html",null)
  }
  
  private fun setProgress(progress: Int) {
    binding.progressBar.post {
      binding.progressBar.progress = progress
      binding.progressBar.isVisible = progress != 0 && progress != 100
    }
  }
  
  private fun buildpageHTML(webContent: String) = buildString {
      
      append(
        "<!DOCTYPE html >"+
          "<html lang=\"en\">\n" +
          "  <head>\n" +
          "  <meta charset=\"utf-8\">" +
          "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" data-next-head=\"\">"+
          getStyle(colorString(), backColor(),textColor()) +
          "  </head>\n" +
          "  <body>\n")
      append(webContent)
      append("\n  </body>\n</html>")
    }
  
  @SuppressLint("SetJavaScriptEnabled")
  private fun setupLiteWebView() {
    with(binding) {
      webview.webChromeClient= WebChromeClient()
      WebView.setWebContentsDebuggingEnabled(true)
      webview.webViewClient=object : WebViewClient(){
        override fun shouldOverrideUrlLoading(
          view: WebView,
          request: WebResourceRequest
        ): Boolean {
          if(request.url.scheme?.contains("data")==true){
              return super.shouldOverrideUrlLoading(view, request)
          }
          else {
            loadArticle(request.url.toString())
            return true
          }
        }
      }
      if(getJsOrLib()) {
        webview.settings.javaScriptEnabled = true
        webview.addJavascriptInterface(
          object {
            @JavascriptInterface
            fun setProgress(progress: Int) {
              activity?.runOnUiThread {
                this@SimpleWebViewFragment.setProgress(progress)
              }
            }
            
            @JavascriptInterface
            fun receiveProcessedArticle(title: String, html: String) {
              activity?.runOnUiThread {
                (activity as MainActivity?)?.let { activity ->
                  if (title=="999"){
                    showErrorOnWebview(999)
                  } else {
                    activity.supportActionBar?.title = title
                    if (getAutoDL()) {
                      val (path, out) = newFile()
                      offline.downloadAllImages(Jsoup.parse(html), progressBar, getLiteWebViewAgent()!!) { savedImagesContent ->
                        offline.saveArticle(title = title, htmlText = savedImagesContent, out, path, onSaveNotification = {
                          //change this to a notification maybe
                          progressBar.post { Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show() }
                        })
                      }
                    }
                  }
                }
              }
              Log.e(TAG, "receiveProcessedArticle: $html")
            }
            
            @JavascriptInterface
            fun getStyle(): String{
              activity?.runOnUiThread { colorString()+textColor()+backColor() }
              return getStyle(colorString(), backColor(),textColor())
            }
            
            @JavascriptInterface
            fun reload(){
              activity?.runOnUiThread {reloadArticle()}
            }
            
            
          }, "Android")
        webview.loadUrl("file:///android_asset/reader.html")
      }
    }
  }
  
  private fun getAutoDL() =
    preferences.getBoolean(getString(R.string.autodl_preferences_key), false)
  
  private fun getJsOrLib() =
    preferences.getBoolean(getString(R.string.lite_web_js_preferences_key), false)
  
  private fun getLiteWebView() =
    preferences.getBoolean(getString(R.string.lite_web_preferences_key), false)
  
  private fun getLiteWebViewAgent() =
    preferences.getString(getString(R.string.user_agent_lite_browser_pref_key), "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:145.0) Gecko/20100101 Firefox/145.0")
  

  companion object {
    private const val TAG: String="SimpleWebViewFragment"
  }
}