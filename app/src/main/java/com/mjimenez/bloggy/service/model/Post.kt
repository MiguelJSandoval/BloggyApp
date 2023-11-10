package com.mjimenez.bloggy.service.model

data class Post(
    val postId: String,
    val title: String,
    val author: String,
    val date: String,
    val content: String,
)
