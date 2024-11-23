package com.egci428.egci428_poppic

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
//import

class UserFragment : Fragment() {
    protected var objects: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize your data
        objects = arrayListOf("Song1", "Song2", "Song3")

        // Inflate the fragment layout
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        // Get references to the UI components
        val image = view.findViewById<ImageView>(R.id.imageView2)
        val btnPlaylist = view.findViewById<Button>(R.id.btnPlaylist)
        val listViewPlaylists = view.findViewById<ListView>(R.id.listViewPlaylists)

        // Set up the adapter
        val adapter = Adapter(requireContext(), 0, objects)
        listViewPlaylists.adapter = adapter

        // Set the initial image
        image.setImageResource(R.drawable.ic_launcher_background)

        // Initially hide the ListView
        listViewPlaylists.visibility = View.GONE

        // Set up the button to make the ListView visible
        btnPlaylist.setOnClickListener {
            Log.d("UserFragment", "Playlist button clicked")
            // Toggle visibility of the ListView
            if (listViewPlaylists.visibility == View.GONE) {
                listViewPlaylists.visibility = View.VISIBLE
            } else {
                listViewPlaylists.visibility = View.GONE
            }
        }

        return view
    }

    class Adapter(var Songdata: Context, resource: Int, var objects: ArrayList<String>)
        : BaseAdapter() {

        override fun getCount(): Int {
            return objects.size
        }

        override fun getItem(position: Int): Any {
            return objects[position]
        }

        override fun getItemId(Position: Int): Long {
            return Position.toLong()
        }

        override fun getView(Position: Int, convertView: View?, Parent: ViewGroup): View {
            val Song = objects[Position]
            val inflater = Songdata.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.playlist_listview, null)

            val songtext = view.findViewById<TextView>(R.id.Song)
            val artisttext = view.findViewById<TextView>(R.id.artistName)
            val image = view.findViewById<ImageView>(R.id.imageView3)
            songtext.text = Song
            artisttext.text = Song
            image.setImageResource(R.drawable.baseline_auto_awesome_motion_24)

            Log.d("UserFragment", "Song: $Song") // Log the song data to ensure it's working

            return view
        }
    }
}
