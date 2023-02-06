package com.example.reddittop

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.reddittop.databinding.ListItemBinding
import com.example.reddittop.model.Post
import com.squareup.picasso.Picasso
import java.io.*


class PostAdapter(private val context: Context, private val data: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    inner class PostViewHolder(postLayoutBinding: ListItemBinding) :
        RecyclerView.ViewHolder(postLayoutBinding.root) {
        private val binding = postLayoutBinding

        fun bind(post: Post) {
            binding.author.text = post.author
            binding.comment.text = post.comments
            binding.date.text = post.date
            Picasso.get().load(post.thumbnail).into(binding.thumbnail)
            binding.thumbnail.setOnClickListener {
                it.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(post.fullThumbnail ?: post.thumbnail)
                    )
                )
            }
            binding.downloadButton.setOnClickListener {
                val bitmap = getImage(binding.thumbnail)
                if (bitmap != null) saveToStorage(bitmap)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    fun addData(listItems: List<Post>) {
        val size = data.size
        data.addAll(listItems)
        val sizeNew = data.size
        notifyItemRangeChanged(size, sizeNew)
    }

    fun getImage(view: ImageView): Bitmap? {
        var image: Bitmap? = null
        try {
            image = Bitmap.createBitmap(
                view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(image)
            view.draw(canvas)
        } catch (ex: Exception) {
            ex.localizedMessage?.let { Log.e("Error", it) }
        }
        return image
    }

    fun saveToStorage(bitmap: Bitmap) {
        val imageName = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let {
                    resolver.openOutputStream(it)
                }
            }
        } else {
            val imagesDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDirectory, imageName)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(context, "Image was saved to gallery", Toast.LENGTH_LONG).show()
        }
    }

}