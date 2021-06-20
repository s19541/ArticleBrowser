package com.example.articlebrowser

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.articlebrowser.adapter.ArticleAdapter
import com.example.articlebrowser.databinding.ActivityMainBinding
import com.example.articlebrowser.model.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.prof.rssparser.Parser
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    val articleAdapter by lazy {ArticleAdapter()}
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
    fun fetchRssData() : ArrayList<Article> {
        val parser = Parser.Builder()
                .context(this)
                .charset(Charset.forName(StandardCharsets.UTF_8.name()))
                .cacheExpirationMillis(24L * 60L * 60L * 100L)
                .build()

        val articleList = arrayListOf<Article>();

        runBlocking {
            launch {
                val channel = parser.getChannel(url = "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci.htm")
                Log.d("MyChannel", channel.toString())

                for (art in channel.articles) {
                    val id = art.guid.toString().substringAfter(',').substringAfter(',').substringBefore(',')
                    val article = Article(id, art.title!!, art.author!!, art.image!!, art.link!!, false, false)
                    articleList.add(article)
                }
            }
        }
        return articleList
    }
    private fun setupArticleList(articleList : List<Article>){
        articleAdapter.articles = articleList
        binding.articleList.apply{
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onResume() {
        super.onResume()
        if(Shared.reloadHelper)
            recreate()
        Shared.reloadHelper = !Shared.reloadHelper
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser !=null ){
            binding.loggedAs.text = "logged as ${auth.currentUser?.email}"
            binding.signInButton.text = "Change user"
        }
        Shared.articleList = fetchRssData()
        updateArticlesStatuses()
    }
    fun updateArticlesStatuses(){
        for((index, article) in Shared.articleList.withIndex()){
            FirebaseDatabase.getInstance()
                    .getReference(auth.currentUser?.uid.toString() + "/isRead/" + article.id)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val isRead = snapshot.getValue(Boolean::class.java)
                            if(isRead != null)
                                article.isRead = isRead
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            FirebaseDatabase.getInstance()
                    .getReference(auth.currentUser?.uid.toString() + "/isFavourite/" + article.id)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val isFavourite = snapshot.getValue(Boolean::class.java)
                            if (isFavourite != null) {
                                article.isFavourite = isFavourite
                            }
                            if (index == Shared.articleList.size - 1) {
                                if (Shared.favouritesClicked) {
                                    binding.favouritesButton.setImageResource(android.R.drawable.btn_star_big_on)
                                    setupArticleList(Shared.articleList.filter { it.isFavourite })
                                } else {
                                    binding.favouritesButton.setImageResource(android.R.drawable.btn_star_big_off)
                                    println(Shared.articleList)
                                    setupArticleList(Shared.articleList)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
        }

    }
    fun login(view: View){
        startActivity(Intent(binding.root.context, LoginActivity::class.java))
    }
    fun showFavourites(view: View){
        Shared.favouritesClicked = !Shared.favouritesClicked
        recreate()
    }
}