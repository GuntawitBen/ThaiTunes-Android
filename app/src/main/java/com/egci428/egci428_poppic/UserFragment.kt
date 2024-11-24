package com.egci428.egci428_poppic

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.egci428.egci428_poppic.models.Song
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class UserFragment : Fragment() {
    private var songList: MutableList<Song> = mutableListOf()  // Mutable list for Firebase updates
    private lateinit var adapter: Adapter  // Declare the adapter for later initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val image = view.findViewById<ImageView>(R.id.imageView2)
        val btnPlaylist = view.findViewById<Button>(R.id.btnPlaylist)
        val listViewPlaylists = view.findViewById<ListView>(R.id.listViewPlaylists)

        // Initialize the adapter and set it to the ListView
        adapter = Adapter(requireContext(), songList)
        listViewPlaylists.adapter = adapter

        // Set the initial image
        image.setImageResource(R.drawable.ic_launcher_background)

        // Initially hide the ListView
        listViewPlaylists.visibility = View.GONE

        // Toggle visibility of the ListView when the button is clicked
        btnPlaylist.setOnClickListener {
            listViewPlaylists.visibility =
                if (listViewPlaylists.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // Fetch song data from Firebase
        fetchSongsFromFirebase()

        return view
    }

    private fun fetchSongsFromFirebase() {
        val db = FirebaseFirestore.getInstance()  // Get Firestore instance
        db.collection("songs")  // Replace "songs" with your Firestore collection name
            .get()
            .addOnSuccessListener { documents ->
                songList.clear()  // Clear the list before adding new data
                for (document in documents) {
                    val song = document.toObject(Song::class.java)
                    songList.add(song)
                }
                adapter.notifyDataSetChanged()  // Notify adapter that the data has changed
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Failed to fetch songs: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error fetching songs", exception)
            }
    }

    // Adapter class as before
    class Adapter(var context: Context, private var songList: List<Song>) : BaseAdapter() {
        override fun getCount(): Int = songList.size
        override fun getItem(position: Int): Any = songList[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.song_item, null)

            val song = songList[position]
            val songText = view.findViewById<TextView>(R.id.songTitle)
            val artistText = view.findViewById<TextView>(R.id.songArtist)
            val image = view.findViewById<ImageView>(R.id.songImage)

            songText.text = song.songName
            artistText.text = song.artistName
            Picasso.get().load(song.artWorkURL).into(image)

            return view
        }
    }
}
