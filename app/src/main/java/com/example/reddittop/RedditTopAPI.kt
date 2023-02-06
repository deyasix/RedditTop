package com.example.reddittop

import com.example.reddittop.model.top.Top
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditTopAPI {
    @GET("/top.json")
    suspend fun getTop(@Query("after") after: String = "") : Response<Top>
}