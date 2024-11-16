package com.egci428.egci428_poppic

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
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
                        .replace(R.id.fragment_container, homeFragment)
                        .commit()
                    true
                }
                R.id.pop -> {
                    // Show PopFragment when Pop item is selected
                    // Show the PopFragment (implement it as needed)
//                    val popFragment = PopFragment()
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, popFragment)
//                        .commit()
                    true
                }
                R.id.user -> {
                    // Show UserFragment when User item is selected
                    // Show the UserFragment (implement it as needed)
//                    val userFragment = UserFragment()
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, userFragment)
//                        .commit()
                    true
                }
                else -> false
            }
        }

        // Set default fragment (HomeFragment) when the activity is created
        if (savedInstanceState == null) {
            val homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit()
        }
    }
}



