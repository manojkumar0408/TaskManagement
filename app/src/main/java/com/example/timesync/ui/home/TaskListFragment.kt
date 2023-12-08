package com.example.timesync.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timesync.AddNewTaskActivity
import com.example.timesync.EditTaskActivity
import com.example.timesync.R
import com.example.timesync.SharedPref
import com.example.timesync.TaskDetailActivity
import com.example.timesync.TasksMainActivity
import com.example.timesync.adapters.TaskListAdapter
import com.example.timesync.databinding.FragmentHomeBinding


class TaskListFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var taskListAdapter: TaskListAdapter
    val notificationPermissionRequestCode = 103

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val sharedPref = SharedPref()
        val user = sharedPref.getUserInfo(requireContext())

        Log.d("userrrr", "${user.username} ${user.firstName}")

        // Initialize RecyclerView and Adapter
        taskListAdapter = TaskListAdapter({ task -> // onDeleteClickListener
            Log.i("Delete clicked", "fragment")
            homeViewModel.deleteTask(task)
        }, { task -> // onEditClickListener
            // Intent to navigate to the EditTaskActivity
            val editIntent = Intent(requireContext(), EditTaskActivity::class.java).apply {
                putExtra("taskId", task.id ?: -1L)
            }
            startActivity(editIntent)
        }, { task ->
            // Handle item click, open TaskDetailActivity
            val intent = Intent(context, TaskDetailActivity::class.java)
            // Pass additional data if needed, e.g., task ID
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)
        })

        binding!!.myCollectionsRv.adapter = taskListAdapter
        binding!!.myCollectionsRv.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.allTasks?.observe(viewLifecycleOwner, Observer { tasks ->
            if (!tasks?.isEmpty()!!) {
                binding?.calendarImage?.visibility = View.INVISIBLE
            }
            taskListAdapter.submitList(tasks)
        })
//        homeViewModel.filteredTasks.observe(viewLifecycleOwner, Observer { filteredTasks ->
//            taskListAdapter.submitList(filteredTasks)
//        })

        binding!!.fabBtn.setOnClickListener {
            sortByDueDate()
            //            startActivity(Intent(requireContext(), AddNewTaskActivity::class.java))
        }
        if (checkNotificationPermission()) {
            onPermissionGranted()
        } else {
            requestNotificationPermission()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true);
    }

    private fun checkNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                notificationPermissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            notificationPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please grant permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onPermissionGranted() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showFilterOptions() {
        val priorities = arrayOf("Low", "Medium", "High", "All")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Filter by Priority")
        builder.setItems(priorities) { _, which ->
            Log.i("detailss", which.toString())
            val selectedPriority = priorities[which]
            filterByPriority(selectedPriority)
        }
        builder.show()
    }

    private fun filterByPriority(selectedPriority: String) {
        homeViewModel.getAllTasksByPriority(selectedPriority)?.observe(
            requireActivity()
        ) { value -> taskListAdapter.submitList(value) }
    }

    private fun sortByDueDate() {
        homeViewModel.getAllTaskInASC()?.observe(
            requireActivity()
        ) { value -> taskListAdapter.submitList(value) }
    }

}
