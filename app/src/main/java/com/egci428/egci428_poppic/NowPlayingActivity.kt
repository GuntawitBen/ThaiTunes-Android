package com.egci428.egci428_poppic

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

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


