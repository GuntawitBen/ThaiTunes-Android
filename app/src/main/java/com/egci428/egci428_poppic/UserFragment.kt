package com.egci428.egci428_poppic

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.egci428.egci428_poppic.api.API
import com.egci428.egci428_poppic.models.Playlist
import com.egci428.egci428_poppic.models.Song
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserFragment : Fragment() {
    private var playlistList: MutableList<Playlist> = mutableListOf()  // List for multiple playlists
    private lateinit var adapter: PlaylistAdapter  // Adapter to bind playlists to ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val listViewPlaylists = view.findViewById<ListView>(R.id.listViewPlaylists)
        val btnPlaylist = view.findViewById<Button>(R.id.btnPlaylist)

        // Initialize the adapter
        adapter = PlaylistAdapter(requireContext(), playlistList)
        listViewPlaylists.adapter = adapter

        // Initially hide the ListView
        listViewPlaylists.visibility = View.GONE

        // Toggle ListView visibility when the button is clicked
        btnPlaylist.setOnClickListener {
            listViewPlaylists.visibility =
                if (listViewPlaylists.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // Load playlists from the API
        loadPlaylists()

        return view
    }


    private fun loadPlaylists() {
        val apiService = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(API::class.java) // Replace with your actual API service class

        val userId = "user123" // Replace with the actual userId
        val call = apiService.getFavorites(userId)
        call.enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (response.isSuccessful) {
                    val favoriteSongs = response.body() ?: emptyList()

                    // Map the favorite songs to a Playlist
                    val playlist = Playlist.mapToPlaylist(
                        favoriteSongs,
                        "My Favorites"
                    )

                    // Clear the existing list and add the new playlist
                    playlistList.clear()
                    playlistList.add(playlist)
                    adapter.notifyDataSetChanged() // Refresh the ListView
                } else {
                    Toast.makeText(requireContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    // Adapter for the ListView
    class PlaylistAdapter(var context: Context, private var playlistList: MutableList<Playlist>) : BaseAdapter() {
        override fun getCount(): Int = playlistList.size
        override fun getItem(position: Int): Any = playlistList[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = convertView ?: inflater.inflate(R.layout.fav_playlist, null)

            val playlist = playlistList[position]
            val playlistNameText = view.findViewById<TextView>(R.id.fav_playlist_title)
            val songCountText = view.findViewById<TextView>(R.id.songcount)
            val image = view.findViewById<ImageView>(R.id.fav_playlist_image)

            playlistNameText.text = playlist.playlistName
            songCountText.text = "${playlist.songCount} songs"
            Picasso.get().load(playlist.artWorkURL).into(image)

            return view
        }
    }
}
