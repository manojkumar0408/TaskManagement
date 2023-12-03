package com.example.timesync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.timesync.databinding.ActivityAddNewTaskBinding

class AddNewTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewTaskBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}