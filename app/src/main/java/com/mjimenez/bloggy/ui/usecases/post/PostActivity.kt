package com.mjimenez.bloggy.ui.usecases.post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.mjimenez.bloggy.databinding.ActivityPostBinding
import com.mjimenez.bloggy.service.model.Post

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var post: Post

    companion object {
        const val POST = "POST_TO_SHOW"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val gson = Gson()
        val postJson = intent.getStringExtra(POST)
        post = gson.fromJson(postJson, Post::class.java)

        supportActionBar?.title = post.title
        binding.title.visibility = View.GONE

        binding.author.text = post.author
        binding.date.text = post.date
        binding.content.text = post.content
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}