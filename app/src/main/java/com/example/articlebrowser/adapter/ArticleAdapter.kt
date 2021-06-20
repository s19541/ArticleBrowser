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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class ArticleItem(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root){
    private lateinit var auth: FirebaseAuth
        fun bind(article: Article){
            auth = FirebaseAuth.getInstance()
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
                favouriteButton.setOnClickListener {
                    if(auth.currentUser != null) {
                        article.isFavourite = !article.isFavourite
                        if (article.isFavourite) {
                            favouriteButton.setImageResource(android.R.drawable.btn_star_big_on)
                            FirebaseDatabase.getInstance()
                                .getReference(auth.currentUser?.uid.toString() + "/isFavourite/" + article.id)
                                .setValue(true)
                        } else {
                            favouriteButton.setImageResource(android.R.drawable.btn_star_big_off)
                            FirebaseDatabase.getInstance()
                                .getReference(auth.currentUser?.uid.toString() + "/isFavourite/" + article.id)
                                .setValue(false)
                        }
                    }
                }
                card.setOnClickListener{
                    if(auth.currentUser != null) {
                        FirebaseDatabase.getInstance()
                            .getReference(auth.currentUser?.uid.toString() + "/isRead/" + article.id)
                            .setValue(true)
                    }
                    CustomTabsIntent.Builder()
                        .build()
                        .launchUrl(binding.root.context, Uri.parse(article.link))
                }
                if(article.isFavourite)
                    favouriteButton.setImageResource(android.R.drawable.btn_star_big_on)
                if(article.isRead)
                    card.setBackgroundColor(Color.GRAY)
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