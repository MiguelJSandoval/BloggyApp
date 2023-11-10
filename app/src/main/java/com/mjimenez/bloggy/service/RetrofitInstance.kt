package com.mjimenez.bloggy.service

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val gson = GsonBuilder()
        .setLenient()
        .create()
    val api: Api by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.6:8080/") // For dev environment
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Api::class.java)
    }
}
