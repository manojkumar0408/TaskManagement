package com.example.timesync

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.PendingIntent
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.timesync.Fragments.DatePickerFragment
import com.example.timesync.Fragments.TimePickerFragment
import com.example.timesync.alarm.AlertReceiver
import com.example.timesync.databinding.ActivityAddNewTaskBinding
import com.example.timesync.db.Task
import com.example.timesync.db.TaskRepository
import com.example.timesync.ui.TaskActivityViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.abs


class AddNewTaskActivity : AppCompatActivity(), OnDateSetListener, OnTimeSetListener {

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

        taskActivityViewModel = ViewModelProvider(this)[TaskActivityViewModel::class.java]
        initialise()
        setListeners()
    }

    private fun initialise() {
        radioChoice = "Low"
    }

    private fun setListeners() {
        binding.textViewDate.setOnClickListener {
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "date picker")
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

    private fun saveTaskClicked() {
        val title: String = binding.taskTitleEditText.text.toString()
        val description: String = binding.taskDescEditText.text.toString()
        val date: String = binding.textViewDate.text.toString()
        val time: String = binding.textViewTime.text.toString()

        if (title.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please insert a Title", Toast.LENGTH_SHORT).show()
            return
        } else if (description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a Description", Toast.LENGTH_SHORT).show()
            return
        } else {
            insertData(title, description, date, time)
        }
    }

    private fun insertData(title: String, description: String, date: String, time: String) {
        var timeMillis: Long? = null
        val id = abs((0..999999999999).random())
        var task: Task?
        if (date != application.getString(R.string.task_date) && time != application.getString(R.string.task_time) && radioChoice != null) {
            timeMillis = parseDate(date, time)
            task = Task(id, title, description, radioChoice!!, "college", timeMillis, "college")
        } else {
            task = Task(id, title, description, radioChoice!!, "college", 0L, "college")
        }
        val repository = TaskRepository(application)
        repository.insert(
            task
        ) { result ->
            newTaskID = result
            Toast.makeText(applicationContext, "Task Saved", Toast.LENGTH_SHORT).show()
            if (timeMillis != null) {
                parseDate(date, time)
                startAlarm(id, title, description, radioChoice, "college", timeMillis, "college")
            }
            finish()
        }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        cal[Calendar.YEAR] = p1
        cal[Calendar.MONTH] = p1
        cal[Calendar.DAY_OF_MONTH] = p3
        val currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(cal.time)
        if (!currentDateString.isEmpty()) {
            binding.textViewDate.text = currentDateString
            binding.deleteDatetimeButton.visibility = View.VISIBLE
        }
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        cal[Calendar.HOUR_OF_DAY] = p1
        cal[Calendar.MINUTE] = p2
        cal[Calendar.SECOND] = 0
        binding.textViewTime.text = "$p1:$p2"
        binding.deleteDatetimeButton.visibility = View.VISIBLE
    }

    private fun parseDate(date: String?, time: String?): Long {
        if (date == null && time == null) {
            return 0L
        }
        val sdfYear = SimpleDateFormat("yy")
        val sdfMonth = SimpleDateFormat("MM")
        val sdfDay = SimpleDateFormat("dd")
        val split = time!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        year = sdfYear.format(Date.parse(date))
        month = sdfMonth.format(Date.parse(date))
        day = sdfDay.format(Date.parse(date))
        hour = Integer.valueOf(split[0])
        minute = Integer.valueOf(split[1])
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = 2000 + year?.toInt()!!
        cal[Calendar.MONTH] = month?.toInt()!! - 1
        cal[Calendar.DATE] = this.day?.toInt()!!
        cal[Calendar.HOUR_OF_DAY] = hour
        cal[Calendar.MINUTE] = minute
        cal[Calendar.SECOND] = 0
        return cal.timeInMillis
    }

    @SuppressLint("ScheduleExactAlarm")
    fun startAlarm(
        id: Long,
        title: String?,
        description: String?,
        priority: String?,
        status: String?,
        timeMillis: Long,
        category: String?
    ) {
        val alertIntent = Intent(this, AlertReceiver::class.java)
        alertIntent.putExtra(Constants.ID, id)
        alertIntent.putExtra(Constants.TITLE, title)
        alertIntent.putExtra(Constants.DESCRIPTION, description)
        alertIntent.putExtra(Constants.PRIORITY, priority)
        alertIntent.putExtra(Constants.EXTRA_ALERTMILLI, timeMillis)
        alertIntent.putExtra(Constants.STATUS, status)
        alertIntent.putExtra(Constants.CATEGORY, category)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_MUTABLE)
        alarmManager.setExact(RTC_WAKEUP, timeMillis, pendingIntent)
    }
}