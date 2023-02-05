package com.example.reddittop

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reddittop.databinding.ListItemBinding
import com.example.reddittop.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(private val context: Context, private val data: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    class PostViewHolder(postLayoutBinding: ListItemBinding) :
        RecyclerView.ViewHolder(postLayoutBinding.root) {
        private val binding = postLayoutBinding

        fun bind(post: Post) {
            binding.author.text = post.author
            binding.comment.text = post.comments
            binding.date.text = post.date
            Picasso.get().load(post.thumbnail).into(binding.thumbnail)
            binding.thumbnail.setOnClickListener {
                it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.fullThumbnail)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount(): Int = data.size
}