package com.egci428.egci428_poppic

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.egci428.egci428_poppic.models.Song

class UserFragment : Fragment() {
    private var songList: MutableList<Song> = mutableListOf()  // Mutable list for Firebase updates
    private lateinit var adapter: Adapter  // Declare the adapter for later initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val image = view.findViewById<ImageView>(R.id.imageView2)
        val btnPlaylist = view.findViewById<Button>(R.id.btnPlaylist)
        val listViewPlaylists = view.findViewById<ListView>(R.id.listViewPlaylists)

        // Initialize the adapter and set it to the ListView
        //adapter = Adapter(requireContext(), songList)
        //listViewPlaylists.adapter = adapter

        // Set the initial image
        image.setImageResource(R.drawable.ic_launcher_background)

        // Initially hide the ListView
        listViewPlaylists.visibility = View.GONE

        // Toggle visibility of the ListView when the button is clicked
        btnPlaylist.setOnClickListener {
            listViewPlaylists.visibility =
                if (listViewPlaylists.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        return view
    }




}
