package com.egci428.egci428_poppic

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference


import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private val db = Firebase.firestore
    private lateinit var postAdapter: PostAdapter
    private val postList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter and set it to the RecyclerView
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        // Load posts from Firebase
        loadPostsFromFirebase()

        // Floating Action Button
        val fab = view.findViewById<FloatingActionButton>(R.id.floating_action_button)
        fab.setOnClickListener {
            val intent = Intent(requireContext(), NewPostActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadPostsFromFirebase()
    }

    private fun loadPostsFromFirebase() {
        // Reference the Firestore collection where your posts are stored
        db.collection("Posts")
            .get()
            .addOnSuccessListener { result ->
                // Clear the current list before adding new posts
                postList.clear()

                // Iterate through each document in the collection
                for (document in result) {
                    // Create a Post object from the Firestore document
                    val content = document.getString("content") ?: ""
                    val mediaUrl = document.getString("mediaUrl") ?: ""
                    val timestamp = document.getString("timestamp") ?: ""
                    val userId = document.getString("userId") ?: ""

                    // Create a new Post instance and add it to the postList
                    val post = Post(content, mediaUrl, timestamp, userId)
                    postList.add(post)
                }

                // Notify the adapter that the data has changed
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}



