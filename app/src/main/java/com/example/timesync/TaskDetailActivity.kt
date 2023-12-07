package com.example.timesync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timesync.databinding.ActivityTaskDetailBinding
import com.google.firebase.database.annotations.Nullable
import java.util.Locale
import java.util.Objects

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var binding: ActivityTaskDetailBinding
    private var notesText: TextView? = null
    private var descriptionTextView: TextView? = null
    private var dueDateTextView: TextView? = null
    private var priorityTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notesText = findViewById(R.id.notes_text)
        descriptionTextView = findViewById(R.id.task_description_tv)
        dueDateTextView = findViewById(R.id.set_date_time_tv)
        priorityTextView = findViewById(R.id.set_priority_tv)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        binding.voiceNotes.setOnClickListener {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()
                )
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
                startActivityForResult(intent, RECORD_AUDIO_PERMISSION_CODE)
            } catch (e: Exception) {
                Toast.makeText(
                    this@TaskDetailActivity, " " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                notesText?.text =
                    "${notesText?.text.toString()} " + Objects.requireNonNull(result)?.get(0)
            }
        }
    }

    companion object {
        private const val RECORD_AUDIO_PERMISSION_CODE = 1
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}