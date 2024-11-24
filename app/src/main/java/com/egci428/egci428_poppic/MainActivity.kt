package com.egci428.egci428_poppic

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.egci428.egci428_poppic.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Handle BottomNavigationView item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.pageFragment, homeFragment)
                        .commit()
                    true
                }
                R.id.pop -> {
                    val popFragment = PopFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.pageFragment, popFragment)
                        .commit()
                    true
                }
                R.id.user -> {
                    val userFragment = UserFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.pageFragment, userFragment)
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Set default fragment (HomeFragment) when the activity is created
        if (savedInstanceState == null) {
            val homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.pageFragment, homeFragment)
                .commit()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nowPlayingFragment, NowPlayingFragment())
                .commit()
        }
    }
}



