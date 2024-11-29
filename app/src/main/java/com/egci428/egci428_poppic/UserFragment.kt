package com.egci428.egci428_poppic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.egci428.egci428_poppic.adapter.SongAdapter
import com.egci428.egci428_poppic.api.API
import com.egci428.egci428_poppic.api.RetrofitClient
import com.egci428.egci428_poppic.models.Song
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var songsAdapter: SongAdapter
    private val favoriteSongs = mutableListOf<Song>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val logOutButton = view.findViewById<Button>(R.id.logoutButton)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        logOutButton.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            requireActivity().finish()
        }

        fetchFavoriteSongs()

        return view
    }

    private fun fetchFavoriteSongs() {
        val userId = "12345"
        val apiService = RetrofitClient.getInstance().getAPI()
        val call = apiService.favoriteSongs(userId)

        call.enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (response.isSuccessful) {
                    val songs = response.body() ?: emptyList()
                    favoriteSongs.clear()
                    favoriteSongs.addAll(songs)

                    if (!::songsAdapter.isInitialized) {
                        songsAdapter = SongAdapter(favoriteSongs)
                        recyclerView.adapter = songsAdapter
                    } else {
                        songsAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("UserFragment", "Failed to fetch favorite songs: ${response.message()}")
                    Toast.makeText(requireContext(), "Failed to fetch favorite songs.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                Log.e("UserFragment", "Error fetching favorite songs: ${t.message}")
                Toast.makeText(requireContext(), "Failed to load songs. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

