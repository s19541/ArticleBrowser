package com.example.articlebrowser.adapter

import android.R.attr.src
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.example.articlebrowser.databinding.ItemArticleBinding
import com.example.articlebrowser.model.Article
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class ArticleItem(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(article: Article){
            binding.apply{
                title.text = article.title
                description.text = article.description
                try {
                    thread {
                        try {
                            val url = URL(article.image)
                            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                            connection.doInput = true
                            connection.connect()
                            val input: InputStream = connection.inputStream
                            val bitmap = BitmapFactory.decodeStream(input)
                            icon.setImageBitmap(bitmap)
                        }catch(exception: Exception){}
                    }.start()
                }catch (exception: Exception){
                    exception.printStackTrace()
                }
                card.setOnClickListener{
                    CustomTabsIntent.Builder()
                        .build()
                        .launchUrl(binding.root.context, Uri.parse(article.link))
                }
                if(article.isRead)
                    card.setBackgroundColor(Color.GRAY)
                if(article.isFavourite)
                    card.setBackgroundColor(Color.YELLOW)
            }
        }
}

    class ArticleAdapter() : RecyclerView.Adapter<ArticleItem>() {
        var articles: List<Article> = emptyList()
            set(value){
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleItem {
            val binding = ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ArticleItem(binding)
        }

        override fun getItemCount(): Int = articles.size

        override fun onBindViewHolder(holder: ArticleItem, position: Int) {
            holder.bind(articles[position])
        }
}