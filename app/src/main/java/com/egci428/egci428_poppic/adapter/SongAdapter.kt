package com.egci428.egci428_poppic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.egci428.egci428_poppic.R
import com.egci428.egci428_poppic.models.Song
import com.squareup.picasso.Picasso

class SongAdapter(private val songList: List<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.songTitle.text = song.songName
        holder.songArtist.text = song.artistName
        Picasso.get().load(song.artWorkURL).into(holder.songImage)

        holder.itemView.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Play/Pause clicked for ${song.songName}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Play/Pause button action (example logic)
        holder.playPauseButton.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Play/Pause clicked for ${song.songName}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = songList.size

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songImage: ImageView = itemView.findViewById(R.id.songImage)
        val songTitle: TextView = itemView.findViewById(R.id.songName)
        val songArtist: TextView = itemView.findViewById(R.id.songArtist)
        val playPauseButton: ImageButton = itemView.findViewById(R.id.playPauseButton)
    }
}

