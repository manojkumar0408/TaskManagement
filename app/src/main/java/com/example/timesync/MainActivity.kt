package com.example.timesync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        auth = Firebase.auth
//        val fab = findViewById<FloatingActionButton>(R.id.fab)

//        fab.setOnClickListener {
//            auth.signOut()
//            finishAffinity()
//            checkUserSignedIn()
//        }
    }

    override fun onStart() {
        super.onStart()
        checkUserSignedIn()
    }

    private fun checkUserSignedIn() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, TasksMainActivity::class.java))
        }
    }

}