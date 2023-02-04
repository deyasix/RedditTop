package com.example.reddittop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.reddittop.model.Post
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var topList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        topList = findViewById(R.id.topList)
        getTop()
    }

    private fun getTop() {
        val retrofit = Client.getInstance()
        val api = retrofit.create(RedditTopAPI::class.java)
        lifecycleScope.launchWhenCreated {
            try {
                val response = api.getTop()
                if (response.isSuccessful) {
                    val body = response.body()
                    val top = mutableListOf<Post>()
                    body?.data?.children?.forEach {
                        top.add(
                            Post(
                                "Author: ${it.data.author}",
                                ("${
                                    ((System.currentTimeMillis() - it.data.created * 1000) / 3_600_000).roundToInt()
                                } hours ago"),
                                "${it.data.num_comments} comments",
                                it.data.thumbnail
                            )
                        )
                    }
                    topList.adapter = PostAdapter(top)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        response.errorBody().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (ex: Exception) {
                ex.localizedMessage?.let { Log.e("Error", it) }
            }
        }
    }
}