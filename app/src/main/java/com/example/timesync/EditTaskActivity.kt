package com.example.timesync

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.timesync.db.Task
import com.example.timesync.ui.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var appBar: ActionBar
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var radioGroupPriority: RadioGroup
    private lateinit var textViewDueDate: TextView
    private lateinit var spinnerCategory: Spinner
    private lateinit var buttonSave: Button
    private lateinit var taskViewModel: HomeViewModel
    private lateinit var textViewDueTime :TextView
    private var taskId: Long = 0
    private var year: String? = null
    private var month: String? = null
    private var day: String? = null
    private var hour = 0
    private var minute: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        // Get support action bar
        appBar = supportActionBar!!
        appBar.title="Edit Task"

        taskId = intent.getLongExtra("taskId", -1L)
        if (taskId == -1L) {
            finish()
            return
        }

        taskViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTaskTitle)
        editTextDescription = findViewById(R.id.editTextTaskDescription)
        radioGroupPriority = findViewById(R.id.radio_group)
        textViewDueDate = findViewById(R.id.text_view_date)
       // spinnerCategory = findViewById(R.id.categories_spinner)
        buttonSave = findViewById(R.id.save_task_btn)
        textViewDueTime = findViewById(R.id.text_view_time)

        // Load task data
        loadTaskData()

        // Set save button listener
        buttonSave.setOnClickListener {
            saveTask()
        }
        textViewDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this,
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    textViewDueDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
        textViewDueTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    textViewDueTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true) // 'true' for 24-hour time format
            timePickerDialog.show()
        }
    }

    private fun loadTaskData() {
        taskViewModel.getTaskById(taskId).observe(this, { task ->
            task?.let {
                editTextTitle.setText(it.title)
                editTextDescription.setText(it.description)

                // Set the priority radio button
                when (it.priority) {
                    "None" -> radioGroupPriority.check(R.id.radio_priority_none)
                    "Low" -> radioGroupPriority.check(R.id.radio_priority_low)
                    "Medium" -> radioGroupPriority.check(R.id.radio_priority_medium)
                    "High" -> radioGroupPriority.check(R.id.radio_priority_high)
                }

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.dueDate
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                textViewDueDate.text = formattedDate
                textViewDueTime.text = formattedTime
                // For spinner, set the appropriate category. You might need to set up an adapter for the spinner.
            }
        })
    }

    private fun saveTask() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()
        val priority = when (radioGroupPriority.checkedRadioButtonId) {
            R.id.radio_priority_none -> "None"
            R.id.radio_priority_low -> "Low"
            R.id.radio_priority_medium -> "Medium"
            R.id.radio_priority_high -> "High"
            else -> ""
        }
        //val dueDate = textViewDueDate.text.toString().toLongOrNull() ?: System.currentTimeMillis()
        val date: String = textViewDueDate.text.toString()
        val time: String = textViewDueTime.text.toString()
        // Fetch the existing task first to update it
        if (date.isNotEmpty() && time.isNotEmpty()) {
            val dueDate = parseDate(date, time)

            taskViewModel.getTaskById(taskId).observe(this, { task ->
                task?.let {
                    val updatedTask = Task(
                        id = taskId,
                        title = title,
                        description = description,
                        priority = priority,
                        status = it.status,  // Keep the existing status
                        dueDate = dueDate,
                        category = it.category  // Keep the existing category
                    )

                    taskViewModel.update(updatedTask)
                    finish()
                }
            })
        } else {
            Toast.makeText(this, "Date and time are required", Toast.LENGTH_SHORT).show()
        }
    }
    private fun parseDate(dateStr: String, timeStr: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return format.parse("$dateStr $timeStr")?.time ?: System.currentTimeMillis()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
