package com.egci428.egci428_poppic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val songList = mutableListOf<Song>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        songAdapter = SongAdapter(songList)
        recyclerView.adapter = songAdapter

        // Fetch songs for search
        fetchSongs()

        return view
    }

    private fun fetchSongs() {
        val apiService = RetrofitClient.getInstance().getAPI()
        apiService.allSongs().enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (response.isSuccessful && response.body() != null) {
                    val fetchedSongs = response.body()!!
                    songList.clear()
                    songList.addAll(fetchedSongs)
                    songAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                // Handle API failure (e.g., show a toast or log the error)
                t.printStackTrace()
            }
        })
    }
}
