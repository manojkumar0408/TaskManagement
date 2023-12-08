package com.example.timesync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timesync.databinding.ActivityTaskDetailBinding
import com.example.timesync.db.Task
import com.example.timesync.ui.home.TaskDetailViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.database.annotations.Nullable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var viewModel: TaskDetailViewModel

    // private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    //private lateinit var Toolbar: Toolbar
    private var notesEditText: TextView? = null
    private var descriptionTextView: TextView? = null
    private var dueDateTextView: TextView? = null
    private var priorityTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        notesEditText = findViewById(R.id.notes_text)
        descriptionTextView = findViewById(R.id.task_description_tv)
        dueDateTextView = findViewById(R.id.set_date_time_tv)
        priorityTextView = findViewById(R.id.set_priority_tv)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val toolbar = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)
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
        viewModel = ViewModelProvider(this).get(TaskDetailViewModel::class.java)
        // Retrieve the task ID passed from the previous activity
        val taskId = intent.getLongExtra("TASK_ID", -1L)
        if (taskId != -1L) {
            viewModel.getTaskById(taskId).observe(this, Observer { task ->
                toolbar.title = task.title
                displayTaskDetails(task)
            })
        }
        val notificationTaskId = intent.getLongExtra(Constants.ID, -1L)
        if (notificationTaskId != -1L) {
            viewModel.getTaskById(notificationTaskId).observe(this, Observer { task ->
                if (task != null) {
                    toolbar.title = task.title
                    displayTaskDetails(task)
                }
            })
        }
    }

    private fun displayTaskDetails(task: Task?) {
        task?.let {
            notesEditText?.setText(task.notes) // Assuming you have a notes field
            descriptionTextView?.text = task.description
            dueDateTextView?.text = formatDate(task.dueDate)
            priorityTextView?.text = task.priority
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
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
                notesEditText?.text =
                    "${notesEditText?.text.toString()} " + Objects.requireNonNull(result)?.get(0)
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

    override fun onPause() {
        super.onPause()
        saveNotes()
    }

    private fun saveNotes() {
        val taskId = intent.getLongExtra("TASK_ID", -1L)
        if (taskId != -1L) {
            val updatedNotes = notesEditText?.text.toString()
            viewModel.updateTaskNotes(taskId, updatedNotes)
        }
    }
}