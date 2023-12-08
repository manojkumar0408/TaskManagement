package com.example.timesync.ui.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timesync.AddNewTaskActivity
import com.example.timesync.EditTaskActivity
import com.example.timesync.R
import com.example.timesync.TaskDetailActivity
import com.example.timesync.adapters.TaskListAdapter
import com.example.timesync.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class TaskListFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var taskListAdapter: TaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        Log.d("userrrr", FirebaseAuth.getInstance().currentUser?.uid.toString())

        // Initialize RecyclerView and Adapter
        taskListAdapter = TaskListAdapter(
            { task -> // onDeleteClickListener
                Log.i("Delete clicked", "fragment")
                homeViewModel.deleteTask(task)
            },
            { task -> // onEditClickListener
                // Intent to navigate to the EditTaskActivity
                val editIntent = Intent(requireContext(), EditTaskActivity::class.java).apply {
                    putExtra("taskId", task.id ?: -1L)
                }
                startActivity(editIntent)
            },
            { task ->
                // Handle item click, open TaskDetailActivity
                val intent = Intent(context, TaskDetailActivity::class.java)
                // Pass additional data if needed, e.g., task ID
                intent.putExtra("TASK_ID", task.id)
                startActivity(intent)
            }
        )

        binding!!.myCollectionsRv.adapter = taskListAdapter
        binding!!.myCollectionsRv.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.allTasks?.observe(viewLifecycleOwner, Observer { tasks ->
            taskListAdapter.submitList(tasks)
        })

        // Floating action button click listener
        binding!!.fabBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AddNewTaskActivity::class.java))
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
