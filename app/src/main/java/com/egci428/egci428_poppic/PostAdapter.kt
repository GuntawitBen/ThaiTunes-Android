package com.egci428.egci428_poppic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Post(
    val title: String,
    val content: String,
    val author: String,
    val timestamp: String
)

class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // Define the ViewHolder to hold and bind views for each item
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.postTitle)
        val content: TextView = itemView.findViewById(R.id.postContent)
        val author: TextView = itemView.findViewById(R.id.postAuthor)
        val timestamp: TextView = itemView.findViewById(R.id.postTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        // Inflate the item layout and create ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // Bind data to the views for each post item
        val post = postList[position]
        holder.title.text = post.title
        holder.content.text = post.content
        holder.author.text = "By ${post.author}"
        holder.timestamp.text = post.timestamp
    }

    override fun getItemCount(): Int {
        // Return the total number of items
        return postList.size
    }
}
