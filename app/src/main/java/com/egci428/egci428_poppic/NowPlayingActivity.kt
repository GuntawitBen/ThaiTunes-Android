package com.egci428.egci428_poppic

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView

import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.egci428.egci428_poppic.api.API
import com.egci428.egci428_poppic.models.Song
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NowPlayingFragment : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playPauseButton: ImageButton
    private lateinit var songProgress: SeekBar
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView
    private lateinit var songImage: ImageView
    private var isPlaying = false

    private val handler = Handler()
    private val songUrl = "http://10.0.2.2:3000/play/Espresso"
    private val apiService: API

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(API::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)

        // Initialize views
        playPauseButton = view.findViewById(R.id.playPauseButton)
        songProgress = view.findViewById(R.id.songProgress)
        currentTimeText = view.findViewById(R.id.currentTime)
        totalTimeText = view.findViewById(R.id.totalTime)
        songTitle = view.findViewById(R.id.songTitle)
        songArtist = view.findViewById(R.id.songArtist)
        songImage = view.findViewById(R.id.songImage)

        playPauseButton.setImageResource(android.R.drawable.ic_media_play)

        // Fetch song metadata
        fetchSongMetadata("Espresso")

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer().apply {
            setDataSource(songUrl)
            prepareAsync()
            setOnPreparedListener {
                totalTimeText.text = formatTime(it.duration)
                songProgress.max = it.duration
                startUpdatingProgress()
            }

            setOnErrorListener { mp, what, extra ->
                // Handle errors here
                Toast.makeText(activity, "Error playing song", Toast.LENGTH_SHORT).show()
                false
            }

            setOnCompletionListener {
                playPauseButton.setImageResource(android.R.drawable.ic_media_play)
                this@NowPlayingFragment.isPlaying = false
            }
        }

        // PLAY PAUSE BUTTON LISTENER
        playPauseButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                playPauseButton.setImageResource(android.R.drawable.ic_media_play)
                handler.removeCallbacksAndMessages(null)
            } else {
                mediaPlayer.start()
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
                startUpdatingProgress()
            }
            isPlaying = !isPlaying
        }

        // SEEKBAR CHANGE LISTENER
        songProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return view
    }

    private fun fetchSongMetadata(songName: String) {
        val call = apiService.getSongInfo(songName)
        call.enqueue(object : Callback<Song> {
            override fun onResponse(call: Call<Song>, response: Response<Song>) {
                if (response.isSuccessful) {
                    val song = response.body()
                    song?.let {
                        // Update UI with song metadata
                        songTitle.text = it.songName
                        songArtist.text = it.artistName

                        Picasso.get()
                            .load(it.artWorkURL)
                            .into(songImage, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    Log.d("Picasso", "Image Loaded Successfully")
                                }

                                override fun onError(e: Exception?) {
                                    Log.e("Picasso", "Error loading image: ${e?.message}")
                                }
                            })

                    }
                } else {
                    Toast.makeText(activity, "Failed to fetch song metadata", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Song>, t: Throwable) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("NowPlayingFragment", "Fragment is not attached: ${t.message}")
                }
            }
        })
    }

    private fun startUpdatingProgress() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition

                    // Update the SeekBar and duration text on the main thread
                    activity?.runOnUiThread {
                        songProgress.progress = currentPosition
                        currentTimeText.text = formatTime(currentPosition)
                    }

                    // Update every second
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)  // Initial delay of 1 second
    }

    private fun formatTime(timeInMillis: Int): String {
        val minutes = timeInMillis / 1000 / 60
        val seconds = timeInMillis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.release()
    }
}
