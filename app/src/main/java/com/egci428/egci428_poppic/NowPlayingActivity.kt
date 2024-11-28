package com.egci428.egci428_poppic

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import com.egci428.egci428_poppic.models.PlaylistRequest
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import okhttp3.ResponseBody
import retrofit2.converter.gson.GsonConverterFactory
import com.egci428.egci428_poppic.models.Song



class NowPlayingFragment : Fragment(), SensorEventListener {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playPauseButton: ImageButton
    private lateinit var songProgress: SeekBar
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView
    private lateinit var songImage: ImageView
    private var isPlaying = false
    private var playingSong: String = "Please Please Please"
    private lateinit var songName: String

    private val handler = Handler()
    private val songUrl = "http://10.0.2.2:3000/play/$playingSong"
    private val apiService: API

    private var sensorManager: SensorManager? = null
    private var lastUpdate: Long = 0
    private var favoritesList: MutableList<Song> = mutableListOf() // Store favorite songs

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(API::class.java)
    }

    companion object {
        private const val ARG_SONG_NAME = "songName"

        fun newInstance(songName: String): NowPlayingFragment {
            val fragment = NowPlayingFragment()
            val args = Bundle().apply {
                putString(ARG_SONG_NAME, songName)
            }
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)

        // Initialize views
        playPauseButton = view.findViewById(R.id.playPauseButton)
        songProgress = view.findViewById(R.id.songProgress)
        currentTimeText = view.findViewById(R.id.currentTime)
        totalTimeText = view.findViewById(R.id.totalTime)
        songTitle = view.findViewById(R.id.songName)
        songArtist = view.findViewById(R.id.songArtist)
        songImage = view.findViewById(R.id.songImage)

        arguments?.getString(ARG_SONG_NAME)?.let {
            playingSong = it
        }

        playPauseButton.setImageResource(android.R.drawable.ic_media_play)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lastUpdate = System.currentTimeMillis()

        fetchSongMetadata(playingSong)

        initMediaPlayer()

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

    private fun initMediaPlayer() {
        val dynamicSongUrl = "http://10.0.2.2:3000/play/$playingSong"

        // Create a new MediaPlayer instance and set the song
        mediaPlayer = MediaPlayer().apply {
            setDataSource(dynamicSongUrl)
            prepareAsync()
            setOnPreparedListener {
                totalTimeText.text = formatTime(it.duration)
                songProgress.max = it.duration
                startUpdatingProgress()
            }
        }
    }

    private fun fetchSongMetadata(songName: String) {
        val call = apiService.getSongInfo(songName)
        call.enqueue(object : Callback<Song> {
            override fun onResponse(call: Call<Song>, response: Response<Song>) {
                if (response.isSuccessful) {
                    val song = response.body()
                    song?.let {
                        songTitle.text = it.songName
                        songArtist.text = it.artistName

                        Picasso.get().load(it.artWorkURL).into(songImage)
                    }
                } else {
                    Toast.makeText(activity, "Failed to fetch song metadata", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Song>, t: Throwable) {
                // Check if the fragment is attached and context is not null
                if (isAdded && context != null) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                } else {
                    // Log the error if the fragment is detached or context is unavailable
                    Log.e("NowPlayingFragment", "Error fetching song metadata: ${t.message}")
                }
            }
        })
    }

    private fun startUpdatingProgress() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    activity?.runOnUiThread {
                        songProgress.progress = currentPosition
                        currentTimeText.text = formatTime(currentPosition)
                    }
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }

    private fun formatTime(timeInMillis: Int): String {
        val minutes = timeInMillis / 1000 / 60
        val seconds = timeInMillis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Sensor Event Handling
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            handleShake(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun handleShake(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)
        val currentTime = System.currentTimeMillis()

        if (acceleration > 2) {
            if (currentTime - lastUpdate > 200) {
                lastUpdate = currentTime
                addToFavorites() // Add current song to favorites
            }
        }
    }

    private fun addToFavorites() {
        // Get the song details
        val currentSong = com.egci428.egci428_poppic.models.Song().apply {
            songName = songTitle.text.toString() // Assuming `songTitle` is a TextView with the song name
            artistName = songArtist.text.toString() // Assuming `songArtist` is a TextView with the artist name
            artWorkURL = "" // Initialize as empty, will update below
        }

        // If artwork URL is available, use it; otherwise, fall back to the default URL
        val artworkUrl = if (currentSong.artWorkURL.isNotBlank()) {
            currentSong.artWorkURL
        } else {
            "https://example.com/default_artwork.jpg" // Default artwork URL
        }

        // Prepare the API call to add this song to the favorites playlist
        val userId = "12345" // Replace with the actual user ID (e.g., from a login session or shared preferences)

        // Log for debugging
        Log.d("API_CALL", "UserId: $userId, SongName: ${currentSong.songName}, ArtistName: ${currentSong.artistName}, ArtworkURL: $artworkUrl")

        val playlistRequest = PlaylistRequest( userId, currentSong.songName,   1,  artworkUrl)

        val call = apiService.addToPlaylist(playlistRequest)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "Song added to favorites!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Failed to add song to favorites!", Toast.LENGTH_SHORT).show()
                    Log.d("API_CALL", "Error Response: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("NowPlayingFragment", "Error adding to favorites: ${t.message}")
            }
        })

    }


    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

}
