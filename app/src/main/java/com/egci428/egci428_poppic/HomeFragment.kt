package com.egci428.egci428_poppic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize the RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Sample data to display in the RecyclerView
        val samplePosts = listOf(
            Post("Post 1", "Content of post 1", "Author 1", "10:00 AM"),
            Post("Post 2", "Content of post 2", "Author 2", "10:30 AM"),
            Post("Post 3", "Content of post 3", "Author 3", "11:00 AM")
        )

        // Initialize the adapter with sample data
        val postAdapter = PostAdapter(samplePosts)
        recyclerView.adapter = postAdapter

        return view
    }
}

