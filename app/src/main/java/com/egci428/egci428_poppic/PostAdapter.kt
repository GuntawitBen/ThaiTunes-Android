package com.egci428.egci428_poppic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class Post(
    val content: String = "",
    val mediaUrl: String = "",
    val timestamp: String = "",
    val userId: String = ""
)


class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.contentTV)
        val user: TextView = itemView.findViewById(R.id.usernameTV)
        val timestamp: TextView = itemView.findViewById(R.id.timeStampTV)
        val mediaImage: ImageView = itemView.findViewById(R.id.postIV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        // Inflate the item layout and create ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.content.text = post.content
        holder.user.text = post.userId
        holder.timestamp.text = post.timestamp

        // Check if mediaUrl is available and load the image
        if (post.mediaUrl.isNotEmpty()) {
            holder.mediaImage.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(post.mediaUrl)
                .into(holder.mediaImage)
        } else {
            holder.mediaImage.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int {
        // Return the total number of items
        return postList.size
    }
}

