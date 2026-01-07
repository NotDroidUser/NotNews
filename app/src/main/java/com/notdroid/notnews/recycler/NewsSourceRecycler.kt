package com.notdroid.notnews.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.notdroid.notnews.R
import com.notdroid.notnews.db.newsApi.CategoriesValues
import com.notdroid.notnews.databinding.NotsourceCardViewModelBinding
import com.notdroid.notnews.db.entities.NewsSourceLocalSave
import com.notdroid.notnews.getFlagEmoji
import com.notdroid.notnews.newsSourceDiffer
import java.util.Locale

class NewsSourceRecycler: ListAdapter<NewsSourceLocalSave, NewsSourceRecycler.NewsSourceHolder>(newsSourceDiffer) {

  class NewsSourceHolder (private val binding: NotsourceCardViewModelBinding):
    RecyclerView.ViewHolder(binding.root){
    fun bind(item: NewsSourceLocalSave){
      with(binding){
        sourceName.text=item.name
        sourceDescription.text=item.description
        sourceUrl.text= item.url
        Glide.with(root)
          .load("https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=${item.url}&size=32")
          .diskCacheStrategy(DiskCacheStrategy.DATA)
          .into(image)
        @SuppressLint("SetTextI18n")
        sourceLang.text = Locale(item.language?:"en").getDisplayScript(Locale.getDefault()) + " " +
            getFlagEmoji(item.country?:"US")
        sourceCategory.text = getCategory(item.category,root.context)
      }
    }

    private fun getCategory(category:String?, context: Context) = if (category!=null) when (CategoriesValues.valueOf(category)){
        CategoriesValues.business ->context.getText(R.string.business_text)
        CategoriesValues.entertainment ->context.getText(R.string.entertainment_text)
        CategoriesValues.general ->context.getText(R.string.general_text)
        CategoriesValues.health ->context.getText(R.string.health_text)
        CategoriesValues.science ->context.getText(R.string.science_text)
        CategoriesValues.sports ->context.getText(R.string.sports_text)
        CategoriesValues.technology ->context.getText(R.string.technology_text)
      } else ""
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    NewsSourceHolder(
      NotsourceCardViewModelBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )

  override fun onBindViewHolder(holder: NewsSourceHolder, position: Int) {
    holder.bind(getItem(position))
  }
}