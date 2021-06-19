package com.example.articlebrowser.model

import android.icu.text.CaseMap

data class Article(
    val id: String,
    val title: String,
    val description: String,
    val image: String,
    val link: String,
    var isRead: Boolean,
    var isFavourite: Boolean
)
