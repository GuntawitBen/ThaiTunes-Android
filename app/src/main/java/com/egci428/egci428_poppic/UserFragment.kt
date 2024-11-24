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
import android.widget.Toast
import com.egci428.egci428_poppic.api.API
import com.egci428.egci428_poppic.models.Song
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UserFragment : Fragment() {
    protected var songlists: List<Song> = listOf()
    lateinit var listViewPlaylists:ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // Inflate the fragment layout
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        // Get references to the UI components
        val image = view.findViewById<ImageView>(R.id.imageView2)
        val btnPlaylist = view.findViewById<Button>(R.id.btnPlaylist)
         listViewPlaylists = view.findViewById(R.id.listViewPlaylists)
        fetchSongs()
        // Set up the adapter
        val adapter = Adapter(requireContext(), 0, songlists)
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

    private fun fetchSongs() {
        // Retrofit API call to fetch song data
        val apiService = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(API::class.java)

        apiService.allSongs().enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (response.isSuccessful) {
                    songlists = response.body() ?: emptyList()  // Update the song list
                    // Notify the adapter that data has changed
                    (listViewPlaylists.adapter as Adapter).notifyDataSetChanged()
                } else {
                    Toast.makeText(activity, "Failed to fetch songs", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class Adapter(var Songdata: Context, resource: Int, var objects: List<Song>)
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

            songtext.text = Song.songName
            artisttext.text = Song.artistName
            Picasso.get().load(Song.artWorkURL).into(image)


            Log.d("UserFragment", "Song: $Song") // Log the song data to ensure it's working

            return view
        }
    }
}
