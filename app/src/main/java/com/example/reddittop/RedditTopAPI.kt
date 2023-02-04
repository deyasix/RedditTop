package com.example.reddittop

import com.example.reddittop.model.top.Top
import retrofit2.Response
import retrofit2.http.GET

interface RedditTopAPI {
    @GET("/top.json")
    suspend fun getTop() : Response<Top>
}