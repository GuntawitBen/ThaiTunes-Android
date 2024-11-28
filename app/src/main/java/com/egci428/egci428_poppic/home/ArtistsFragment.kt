package com.egci428.egci428_poppic.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.egci428.egci428_poppic.R
import com.egci428.egci428_poppic.adapter.ArtistAdapter
import com.egci428.egci428_poppic.adapter.SongAdapter
import com.egci428.egci428_poppic.api.RetrofitClient
import com.egci428.egci428_poppic.models.Artist
import com.egci428.egci428_poppic.models.Song
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var artistAdapter: ArtistAdapter
    private val artistList = mutableListOf<Artist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artists, container, false)

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Initialize the Adapter
        artistAdapter = ArtistAdapter(artistList)
        recyclerView.adapter = artistAdapter

        // Fetch artists
        randomArtists()

        return view
    }

    private fun randomArtists() {
        val apiService = RetrofitClient.getInstance().getAPI()
        apiService.randomArtists().enqueue(object : Callback<List<Artist>> {
            override fun onResponse(call: Call<List<Artist>>, response: Response<List<Artist>>) {
                if (response.isSuccessful && response.body() != null) {
                    val fetchedArtists = response.body()!!
                    artistList.clear()
                    artistList.addAll(fetchedArtists)
                    artistAdapter.notifyDataSetChanged()
                } else {
                    // Handle unsuccessful response
                    Log.e("ArtistsFragment", "Failed to fetch artists: ${response.message()}")
                    Toast.makeText(requireContext(), "Failed to fetch artists", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Artist>>, t: Throwable) {
                // Log the error and show a message to the user
                Log.e("ArtistsFragment", "Error fetching artists", t)
                Toast.makeText(requireContext(), "Error fetching artists", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
