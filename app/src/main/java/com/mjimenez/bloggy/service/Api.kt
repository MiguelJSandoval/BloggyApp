package com.mjimenez.bloggy.service

import com.mjimenez.bloggy.service.dto.SavingPost
import com.mjimenez.bloggy.service.model.*
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @GET("api/posts")
    suspend fun getPosts(
        @Query("query") query: String?,
        @Query("page") page: Int,
        @Query("rows") rows: Int,
    ): Response<List<Post>>

    @POST("api/posts")
    suspend fun savePost(
        @Body post: SavingPost
    )
}
