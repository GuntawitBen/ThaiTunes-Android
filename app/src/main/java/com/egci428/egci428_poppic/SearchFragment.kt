package com.egci428.egci428_poppic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.egci428.egci428_poppic.adapter.SongAdapter
import com.egci428.egci428_poppic.api.API
import com.egci428.egci428_poppic.api.RetrofitClient
import com.egci428.egci428_poppic.models.Song
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private lateinit var searchView: SearchView
    private val originalSongList = mutableListOf<Song>() // To retain all songs
    private val displayedSongList = mutableListOf<Song>() // For the filtered list

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        songAdapter = SongAdapter(displayedSongList)
        recyclerView.adapter = songAdapter

        searchView = view.findViewById(R.id.searchView)

        fetchSongs()

        // Set up SearchView listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterSongs(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterSongs(it) }
                return true
            }
        })

        return view
    }

    private fun fetchSongs() {
        val apiService = RetrofitClient.getInstance().getAPI()
        apiService.allSongs().enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (response.isSuccessful && response.body() != null) {
                    val fetchedSongs = response.body()!!

                    // Clear both lists and populate with fetched songs
                    originalSongList.clear()
                    displayedSongList.clear()
                    originalSongList.addAll(fetchedSongs)
                    displayedSongList.addAll(fetchedSongs)

                    // Notify adapter of changes
                    songAdapter.notifyDataSetChanged()
                } else {
                    // Show placeholder or handle error
                    Toast.makeText(requireContext(), "Failed to load songs.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                // Handle API failure
                Toast.makeText(requireContext(), "Error fetching songs: ${t.message}", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })
    }

    private fun filterSongs(query: String) {
        // Filter the original list based on the query
        val filteredSongs = originalSongList.filter { song ->
            song.songName.contains(query, ignoreCase = true) ||
                    song.artistName.contains(query, ignoreCase = true)
        }

        // Update displayed list and notify adapter
        displayedSongList.clear()
        displayedSongList.addAll(filteredSongs)
        songAdapter.notifyDataSetChanged()
    }
}

