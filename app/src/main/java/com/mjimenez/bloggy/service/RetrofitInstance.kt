package com.mjimenez.bloggy.service

import com.google.gson.GsonBuilder
import com.mjimenez.bloggy.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val gson = GsonBuilder()
        .setLenient()
        .create()
    val api: Api by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Api::class.java)
    }
}
