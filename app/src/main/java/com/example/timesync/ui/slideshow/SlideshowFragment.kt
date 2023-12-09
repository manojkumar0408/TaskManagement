package com.example.timesync.ui.slideshow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timesync.EditTaskActivity
import com.example.timesync.TaskDetailActivity
import com.example.timesync.adapters.TaskListAdapter
import com.example.timesync.databinding.FragmentSlideshowBinding
import com.example.timesync.db.Task
import com.example.timesync.ui.home.HomeViewModel
import java.util.Calendar

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskListAdapter: TaskListAdapter
    private lateinit var homeViewModel: HomeViewModel

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initialize()

        val currentDate = Calendar.getInstance()
        binding.calendarView.date = currentDate.timeInMillis
        selectedYear = currentDate.get(Calendar.YEAR)
        selectedYear = currentDate.get(Calendar.MONTH) + 1
        selectedYear = currentDate.get(Calendar.DAY_OF_MONTH)

        updateTaskList()

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedYear = year
            selectedMonth = month + 1 // Calendar months are 0-indexed
            selectedDay = dayOfMonth

            updateTaskList()
        }
        return root
    }

    private fun initialize() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        taskListAdapter = TaskListAdapter({ task ->
            Log.i("Delete clicked", "fragment")
            homeViewModel.deleteTask(task)
        }, { task ->
            val editIntent = Intent(requireContext(), EditTaskActivity::class.java).apply {
                putExtra("taskId", task.id ?: -1L)
            }
            startActivity(editIntent)
        }, { task ->
            val intent = Intent(requireContext(), TaskDetailActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)
        }, context)

        binding.recyclerView.adapter = taskListAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        homeViewModel.allTasks?.observe(viewLifecycleOwner, Observer { tasks ->
//            val filteredTasks = filterTasksBySelectedDay(tasks)
//            taskListAdapter.submitList(filteredTasks)
//        })
    }

    private fun updateTaskList() {
        homeViewModel.allTasks?.observe(viewLifecycleOwner, Observer { tasks ->
            val filteredTasks = filterTasksBySelectedDay(tasks)
            taskListAdapter.submitList(filteredTasks)
        })
    }

    private fun filterTasksBySelectedDay(tasks: List<Task?>?): List<Task> {
        val selectedDayTasks = mutableListOf<Task>()

        for (task in tasks.orEmpty()) {
            val taskDueDate = task?.dueDate
            if (taskDueDate != null && isSameDay(
                    taskDueDate, selectedYear, selectedMonth, selectedDay
                )
            ) {
                selectedDayTasks.add(task)
            }
        }

        return selectedDayTasks
    }

    private fun isSameDay(millis: Long, year: Int, month: Int, day: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        return (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) + 1 == month && calendar.get(
            Calendar.DAY_OF_MONTH
        ) == day)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
