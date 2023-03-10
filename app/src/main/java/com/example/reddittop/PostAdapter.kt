package com.example.reddittop

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.reddittop.databinding.ListItemBinding
import com.example.reddittop.model.Post
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class PostAdapter(private val context: Context, private val data: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    inner class PostViewHolder(postLayoutBinding: ListItemBinding) :
        RecyclerView.ViewHolder(postLayoutBinding.root) {
        private val binding = postLayoutBinding

        fun bind(post: Post) {
            Picasso.get().load(post.thumbnail).into(binding.thumbnail)
            binding.author.text = post.author
            binding.comment.text = post.comments
            binding.date.text = post.date
            binding.thumbnail.setOnClickListener {
                it.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(post.fullThumbnail ?: post.thumbnail)
                    )
                )
            }
            binding.downloadButton.setOnClickListener {
                if (binding.thumbnail.drawable == null) {
                    Toast.makeText(context, "Image was not found. Post has no image or image is uploading now", Toast.LENGTH_LONG).show()
                }
                else {
                    val address =
                        if (post.fullThumbnail != null && post.fullThumbnail.startsWith("https://i.redd.it")) post.fullThumbnail else post.thumbnail
                    val bitmap = getImage(address)
                    if (bitmap != null) saveToStorage(bitmap)
                    else {
                        Log.e("Error", address)
                    }
                }
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

    fun getImage(address: String): Bitmap? {
        var result: Bitmap? = null
        val thread = Thread() {
            try {
                result = Picasso.get().load(address).get()
            } catch (ex: Exception) {
                Log.e(ex.localizedMessage, address)
            }
        }
        thread.start()
        thread.join()
        return result
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

    fun getData() = data

}