package com.egci428.egci428_poppic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.egci428.egci428_poppic.R
import com.egci428.egci428_poppic.models.Artist
import com.squareup.picasso.Picasso

class ArtistAdapter (private val artistList: List<Artist>) :
    RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.artist_item, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        // Get the artist at the current position
        val artist = artistList[position]

        // Bind artist data to the views
        holder.artistName.text = artist.artistName
        Picasso.get().load(artist.artWorkURL).into(holder.artistImage)
    }

    override fun getItemCount(): Int = artistList.size

    class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artistImage: ImageView = itemView.findViewById(R.id.artistImage)
        val artistName: TextView = itemView.findViewById(R.id.artistTV)
    }
}
