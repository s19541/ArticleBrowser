package com.example.articlebrowser

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.articlebrowser.adapter.ArticleAdapter
import com.example.articlebrowser.databinding.ActivityMainBinding
import com.example.articlebrowser.model.Article
import com.google.firebase.auth.FirebaseAuth
import com.prof.rssparser.Parser
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


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
                     articleList.add(Article(art.guid!!, art.title!!, art.author!!, art.image!!, art.link!!, false, false)) }
                }
            }
        return articleList
        }
    private fun setupArticleList(){
        articleAdapter.articles = Shared.articleList
        binding.dishList.apply{
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser !=null ){
            binding.loggedAs.text = "logged as ${auth.currentUser?.email}"
            binding.signInButton.text = "Change user"
        }
        Shared.articleList = fetchRssData()
        setupArticleList()
    }
    fun login(view: View){
        startActivity(Intent(binding.root.context, LoginActivity::class.java))
    }
}