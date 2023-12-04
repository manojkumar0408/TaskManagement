package com.example.timesync

import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.RadioGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.timesync.Fragments.DatePickerFragment
import com.example.timesync.Fragments.TimePickerFragment
import com.example.timesync.databinding.ActivityAddNewTaskBinding
import com.example.timesync.db.Task
import com.example.timesync.db.TaskRepository
import com.example.timesync.ui.TaskActivityViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


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
            Toast.makeText(applicationContext, radioChoice.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTaskClicked() {
        val title: String = binding.taskTitleEditText.text.toString()
        val description: String = binding.taskDescEditText.text.toString()
        val date: String = binding.textViewDate.text.toString()
        val time: String = binding.textViewTime.text.toString()

//        val category: String = categoriesSpinner.getSelectedItem().toString()

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
        var timeMillis: Long?
        var task: Task?
        if (!(date == "No date" && time == "No time") && radioChoice != null) {
            timeMillis = parseDate(date, time)
            task = Task(2, title, description, radioChoice!!, "college", timeMillis, "college")
        } else {
            radioChoice = "High"
            task = Task(2, title, description, radioChoice!!, "college", 0L, "college")

        }
        val repository = TaskRepository(application)
        repository.insert(
            task
        ) { result ->
            newTaskID = result
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
        //        cal.setTimeInMillis(System.currentTimeMillis());
//        cal.clear();
        cal[Calendar.YEAR] = 2000 + year?.toInt()!!
        cal[Calendar.MONTH] = month?.toInt()!! - 1
        cal[Calendar.DATE] = this.day?.toInt()!!
        cal[Calendar.HOUR_OF_DAY] = hour
        cal[Calendar.MINUTE] = minute
        cal[Calendar.SECOND] = 0
        return cal.timeInMillis
    }
}