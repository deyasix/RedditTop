package com.example.reddittop

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.reddittop.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(private val context: Context, private val data: List<Post>): BaseAdapter() {
    override fun getCount(): Int  = data.size

    override fun getItem(position: Int): Post = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val post = getItem(position)
        val inflater = LayoutInflater.from(parent.context)

        if (convertView == null) {
            inflater.inflate(R.layout.list_item, parent, false)
        }

        val view = inflater.inflate(R.layout.list_item, parent, false)
        val authorTextView = view.findViewById(R.id.author) as TextView
        val dateTextView = view.findViewById(R.id.date) as TextView
        val commentTextView = view.findViewById(R.id.comment) as TextView
        val thumbnailImageView = view.findViewById(R.id.thumbnail) as ImageView

        authorTextView.text = post.author
        dateTextView.text = post.date
        commentTextView.text = post.comments
        Picasso.get().load(post.thumbnail).into(thumbnailImageView)

        thumbnailImageView.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.fullThumbnail)))
        }

        return view
    }
}