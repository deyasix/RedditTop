package com.example.reddittop

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reddittop.model.Post
import kotlinx.coroutines.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private lateinit var topList: RecyclerView
    private var after: String = ""
    var state: Parcelable? = null

    companion object {
        var list: MutableList<Post> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        topList = findViewById(R.id.topList)
        getTop()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        state = savedInstanceState.getParcelable("ListState")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putParcelable(
            "ListState",
            (topList.layoutManager as LinearLayoutManager).onSaveInstanceState()
        )
        super.onSaveInstanceState(savedInstanceState)
    }

    private fun getTop() {
        topList.adapter = PostAdapter(this@MainActivity, mutableListOf())
        topList.layoutManager = LinearLayoutManager(this@MainActivity)
        getData()
        topList.addOnScrollListener(ScrollListener())
    }

    fun getData() {
        val retrofit = Client.getInstance()
        val api = retrofit.create(RedditTopAPI::class.java)
        val top = mutableListOf<Post>()
        lifecycleScope.launch {
            try {
                val response = api.getTop(after)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body !== null) {
                        body.data.children.forEach {
                            top.add(
                                Post(
                                    "Author: ${it.data.author}",
                                    ("${
                                        ((System.currentTimeMillis() - it.data.created * 1000) / 3_600_000).roundToInt()
                                    } hours ago"),
                                    "${it.data.num_comments} comments",
                                    it.data.thumbnail,
                                    it.data.url_overridden_by_dest
                                )
                            )
                        }
                        if (state != null && after == "") {
                            (topList.adapter as PostAdapter).addData(list)
                            (topList.layoutManager as LinearLayoutManager).onRestoreInstanceState(
                                state
                            )
                        } else {
                            (topList.adapter as PostAdapter).addData(top)
                        }
                        list = (topList.adapter as PostAdapter).getData()
                        after = body.data.after
                    }

                }
            } catch (ex: Exception) {
                ex.localizedMessage?.let { Log.e("Error", it) }
            }
        }
    }

    inner class ScrollListener : RecyclerView.OnScrollListener() {
        private var previousTotal = 0
        private var loading = true
        private var visibleThreshold = 5
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
                val visibleItemCount = (topList.layoutManager as LinearLayoutManager).childCount
                val totalItemCount = (topList.layoutManager as LinearLayoutManager).itemCount
                val firstVisibleItem =
                    (topList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    getData()
                    loading = true
                }
            }
        }
    }
}