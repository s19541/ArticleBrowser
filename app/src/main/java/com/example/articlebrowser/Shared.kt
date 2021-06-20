package com.example.articlebrowser

import com.example.articlebrowser.model.Article

object Shared {
    var articleList = mutableListOf<Article>()
    var favouritesClicked = false
    var reloadHelper = true
}