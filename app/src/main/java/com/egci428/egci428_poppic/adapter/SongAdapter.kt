package com.egci428.egci428_poppic.adapter

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.egci428.egci428_poppic.NowPlayingFragment
import com.egci428.egci428_poppic.R
import com.egci428.egci428_poppic.models.Song
import com.squareup.picasso.Picasso

class SongAdapter(private val songList: MutableList<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.bind(song)
    }

    override fun getItemCount(): Int = songList.size

    fun updateList(filteredSongs: List<Song>) {
        songList.clear()
        songList.addAll(filteredSongs)
        notifyDataSetChanged()
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songImage: ImageView = itemView.findViewById(R.id.songImage)
        private val songTitle: TextView = itemView.findViewById(R.id.songTitle)
        private val songArtist: TextView = itemView.findViewById(R.id.songArtist)
        private val playPauseButton: ImageButton = itemView.findViewById(R.id.playPauseButton)

        fun bind(song: Song) {
            songTitle.text = song.songName
            songArtist.text = song.artistName

            // Check if the image URL is valid before loading it
            val imageUrl = song.artWorkURL
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Placeholder during loading
                    .error(R.drawable.ic_launcher_background) // Fallback if loading fails
                    .into(songImage)
            } else {
                // If the URL is empty, use a default image
                Picasso.get()
                    .load(R.drawable.ic_launcher_background) // Fallback image
                    .into(songImage)
            }

            // Attach listeners
            itemView.setOnClickListener { navigateToNowPlaying(song) }
            playPauseButton.setOnClickListener { navigateToNowPlaying(song) }
        }


        private fun navigateToNowPlaying(song: Song) {
            val nowPlayingFragment = NowPlayingFragment.newInstance(song.songName)
            val fragmentTransaction = (itemView.context as AppCompatActivity)
                .supportFragmentManager
                .beginTransaction()
            fragmentTransaction.replace(R.id.nowPlayingFragment, nowPlayingFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
}
