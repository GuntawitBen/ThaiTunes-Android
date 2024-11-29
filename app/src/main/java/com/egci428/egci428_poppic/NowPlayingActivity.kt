package com.egci428.egci428_poppic

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.egci428.egci428_poppic.api.API
import com.egci428.egci428_poppic.models.PlaylistRequest
import com.egci428.egci428_poppic.models.Song
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private var playingSong: String? = null
    private lateinit var songName: String

    private val handler = Handler()
    private val apiService: API

    private var sensorManager: SensorManager? = null
    private var lastUpdate: Long = 0
    private var isProcessingShake: Boolean = false
    private var favoritesList: MutableList<Song> = mutableListOf()

    private var imageURL: String = ""
    private var art: String = ""

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(API::class.java)
    }

    companion object {
        private const val ARG_SONG_NAME = "songName"

        fun newInstance(songName: String?): NowPlayingFragment {
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)

        sensorManager = activity?.getSystemService(SensorManager::class.java)


        // Initialize views
        playPauseButton = view.findViewById(R.id.playPauseButton)
        songProgress = view.findViewById(R.id.songProgress)
        currentTimeText = view.findViewById(R.id.currentTime)
        totalTimeText = view.findViewById(R.id.totalTime)
        songTitle = view.findViewById(R.id.songTitle)
        songArtist = view.findViewById(R.id.songArtist)
        songImage = view.findViewById(R.id.songImage)

        arguments?.getString(ARG_SONG_NAME)?.let {
            playingSong = it
        }

        playPauseButton.setImageResource(android.R.drawable.ic_media_play)

        if (playingSong != null) {
            // Fetch song metadata only if a song is selected
            fetchSongMetadata(playingSong!!)
            initMediaPlayer()
        } else {
            // Update UI to indicate no song is selected
            songTitle.text = "No Song Selected"
            songArtist.text = ""
            songImage.setImageResource(R.drawable.ic_launcher_background)
        }

        // PLAY PAUSE BUTTON LISTENER
        playPauseButton.setOnClickListener {
            if (playingSong != null) {
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
            } else {
                Toast.makeText(activity, "No song is selected to play", Toast.LENGTH_SHORT).show()
            }
        }

        // SEEKBAR CHANGE LISTENER
        songProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && playingSong != null) {
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

        mediaPlayer = MediaPlayer().apply {
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
            setDataSource(dynamicSongUrl)
            prepareAsync()

            // Trigger start only when MediaPlayer is ready
            setOnPreparedListener {
                it.start() // Start playback only when prepared
                totalTimeText.text = formatTime(it.duration)
                songProgress.max = it.duration
                startUpdatingProgress()
            }

            setOnErrorListener { mp, what, extra ->
                Log.e("MediaPlayerError", "Error code: $what, Extra code: $extra")
                true
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
                        // Update UI with song metadata
                        songTitle.text = it.songName
                        songArtist.text = it.artistName
                        art = it.artistName
                        imageURL = it.artWorkURL

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
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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
                isProcessingShake = true
                addToFavorites()
            }
        }
    }

    private fun addToFavorites() {
        // Check if the song is selected
        if (playingSong == null || songTitle.text == "No Song Selected") {
            Toast.makeText(activity, "No Song is Playing", Toast.LENGTH_SHORT).show()
            return
        }

            Log.e("ERROR", art)

        val currentSong = Song().apply {
            songName = songTitle.text.toString()
            artistName = art
            artWorkURL = imageURL
        }

        val userId = "bob"

        val playlistRequest = PlaylistRequest(
            userId, currentSong.songName, currentSong.artistName, currentSong.artWorkURL, 1
        )

        Log.d("CHECK","${currentSong.songName}, ${currentSong.artistName}, ${currentSong.artWorkURL}")

        val call = apiService.addToPlaylist(playlistRequest)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "Song added to favorites!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Song already added!", Toast.LENGTH_SHORT).show()
                    Log.d("API_CALL", "Error Response: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("NowPlayingFragment", "Error adding to favorites: ${t.message}")
            }
        })

        Handler(Looper.getMainLooper()).postDelayed({
            isProcessingShake = false
        }, 500) // Wait 500ms before allowing another shake to trigger addToFavorites
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
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }


}
