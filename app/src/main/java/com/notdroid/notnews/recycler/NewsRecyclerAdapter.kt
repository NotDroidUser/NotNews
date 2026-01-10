package com.notdroid.notnews.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.notdroid.notnews.composables.NotNewCard
import com.notdroid.notnews.composables.NotNewsTheme
import com.notdroid.notnews.databinding.NotnewCardViewModelBinding
import com.notdroid.notnews.db.entities.NewsApiLocalSave
import com.notdroid.notnews.newsApiItemDiffer

class NewsRecyclerAdapter(private val controller: NewsController):
  ListAdapter<NewsApiLocalSave, NewsRecyclerAdapter.NewsHolder>(newsApiItemDiffer) {

  class NewsHolder (private val binding: NotnewCardViewModelBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(item: NewsApiLocalSave, controller: NewsController){
      binding.compose.setContent {
        NotNewsTheme {
          NotNewCard(item,controller,isOffline = true) // because it's used only on downloads now,
        // anyways this will not be used anymore
        }
      }
      
//      with(binding){
//
//        articleTitle.text=item.title
//        articleDescription.text=item.description
//        dateOfNew.text= stringify(item.publishedAt, root.context)
//        if (item.urlToImage!="") {
//          image.visibility = View.VISIBLE
//          Glide.with(root)
//            .load(item.urlToImage)
//            .diskCacheStrategy(DiskCacheStrategy.DATA)
//            .into(image)
//        } else{
//          image.visibility=View.GONE
//        }
//        controller.isAvailableOffline(item.url,isDownloaded)
//        source.text = buildString {
//          append(item.sourceName)
//          if (item.author!="") {
//            append(" ")
//            append(root.context.getText(R.string.by_text))
//            append(" ")
//            append(item.author)
//          }
//        }
//        root.setOnClickListener {
//          controller.onTouchItem(item)
//        }
//
//      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
    NewsHolder(
      NotnewCardViewModelBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )

  override fun onBindViewHolder(holder: NewsHolder, position: Int) {
    holder.bind(getItem(position),controller)
  }
}