package com.example.timesync

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.service.autofill.Validators.or
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.timesync.Fragments.DatePickerFragment
import com.example.timesync.Fragments.TimePickerFragment
import com.example.timesync.alarm.AlertReceiver
import com.example.timesync.databinding.ActivityAddNewTaskBinding
import com.example.timesync.db.Task
import com.example.timesync.db.TaskRepository
import com.example.timesync.ui.TaskActivityViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs


class AddNewTaskActivity : AppCompatActivity(), DatePickerFragment.OnDateSetListener,
    OnTimeSetListener {

    //private lateinit var appBar: ActionBar
    private lateinit var binding: ActivityAddNewTaskBinding
    private lateinit var taskActivityViewModel: TaskActivityViewModel
    private val cal = Calendar.getInstance()
    private var radioChoice: String? = null
    private val date: String? = null
    private var time: String? = null
    private var year: String? = null
    private var month: String? = null
    private var day: String? = null
    private var hour = 0
    private var minute: Int = 0
    private var newTaskID: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setTitle("Add New Task")
        // Get support action bar
        //appBar = supportActionBar?
        //appBar.title="Add New Task"

        taskActivityViewModel = ViewModelProvider(this)[TaskActivityViewModel::class.java]
        initialise()
        setListeners()
        setupCategorySpinner()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initialise() {
        radioChoice = "Low"
    }

    private fun setListeners() {
        binding.textViewDate.setOnClickListener {
            showDataPicker()
        }
        binding.textViewTime.setOnClickListener {
            val datePicker: DialogFragment = TimePickerFragment()
            datePicker.show(supportFragmentManager, "Time picker")
        }
        binding.saveTaskBtn.setOnClickListener {
            saveTaskClicked()
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            radioChoice = if (binding.radioPriorityNone.isChecked) {
                "None"
            } else if (binding.radioPriorityLow.isChecked) {
                "Low"
            } else if (binding.radioPriorityMedium.isChecked) {
                "Medium"
            } else {
                "High"
            }
        }
    }

    private fun showDataPicker() {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.setOnDateSetListener(this)
        datePickerFragment.show(supportFragmentManager, "datePicker")
    }

    private fun saveTaskClicked() {
        val title: String = binding.taskTitleEditText.text.toString()
        val description: String = binding.taskDescEditText.text.toString()
        val date: String = binding.textViewDate.text.toString()
        val time: String = binding.textViewTime.text.toString()
        val category: String = binding.categoriesSpin.selectedItem.toString()

        if (title.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please insert a Title", Toast.LENGTH_SHORT).show()
            return
        } else if (description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a Description", Toast.LENGTH_SHORT).show()
            return
        } else if ((date.toString() == "Task Date") or (time.toString() == "Task Time")) {
            Toast.makeText(this, "Please insert a Date and Time", Toast.LENGTH_SHORT).show()
            return
        } else {
            insertData(title, description, date, time, category)
        }
    }

    private fun insertData(
        title: String, description: String, date: String, time: String, category: String
    ) {
        var timeMillis: Long? = null
        val id = abs((0..999999999999).random())
        var task: Task?
        if (date != application.getString(R.string.task_date) && time != application.getString(R.string.task_time) && radioChoice != null) {
            timeMillis = convertTimeInMillis()
            task = Task(id, title, description, radioChoice!!, "college", timeMillis, "category")
        } else {
            task = Task(id, title, description, radioChoice!!, "college", 0L, "category")
        }
        val repository = TaskRepository(application)
        repository.insert(
            task
        ) { result ->
            newTaskID = result
            Toast.makeText(applicationContext, "Task Saved", Toast.LENGTH_SHORT).show()
            if (timeMillis != null) {
                convertTimeInMillis()
                startAlarm(id, title, description, radioChoice, "college", timeMillis, "category")
            }
            finish()
        }
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        cal[Calendar.HOUR_OF_DAY] = p1
        cal[Calendar.MINUTE] = p2
        cal[Calendar.SECOND] = 0
        binding.textViewTime.text = "$p1:$p2"
        binding.deleteDatetimeButton.visibility = View.VISIBLE
    }

    private fun convertTimeInMillis(): Long {
        val selectedDate = binding.textViewDate.text.toString()
        val selectedTime = binding.textViewTime.text.toString()

        // Parse the selected date
        val dateParts = selectedDate.split("-")
        val year = dateParts[0].toInt()
        val month = dateParts[1].toInt() - 1 // Calendar months are 0-based
        val dayOfMonth = dateParts[2].toInt()

        // Parse the selected time
        val timeParts = selectedTime.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        Log.d(
            "Converted Time in millis",
            convertToMillis(year, month, dayOfMonth, hour, minute).toString()
        )
        return convertToMillis(year, month, dayOfMonth, hour, minute)
    }

    private fun convertToMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    fun startAlarm(
        id: Long,
        title: String?,
        description: String?,
        priority: String?,
        status: String?,
        timeMillis: Long,
        category: String?
    ) {
        Log.i("actionss", "satarrt")
        val alertIntent = Intent(this, AlertReceiver::class.java)
        alertIntent.putExtra(Constants.ID, id)
        alertIntent.putExtra(Constants.TITLE, title)
        alertIntent.putExtra(Constants.DESCRIPTION, description)
        alertIntent.putExtra(Constants.PRIORITY, priority)
        alertIntent.putExtra(Constants.EXTRA_ALERTMILLI, timeMillis)
        alertIntent.putExtra(Constants.STATUS, status)
        alertIntent.putExtra(Constants.CATEGORY, category)
        val mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        alertIntent.putExtra(Constants.EXTRA_RINGTONE, mediaPlayer.toString())

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            this, 1, alertIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(RTC_WAKEUP, timeMillis, pendingIntent)

    }


    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)
        val formattedDate = formatDate(selectedDate)
        binding.textViewDate.text = "$formattedDate"
    }

    private fun formatDate(date: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date.time)
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Class", "Part-time", "Personal")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoriesSpin.adapter = adapter
    }

}