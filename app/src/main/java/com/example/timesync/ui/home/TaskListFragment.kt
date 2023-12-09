package com.example.timesync.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timesync.AddNewTaskActivity
import com.example.timesync.EditTaskActivity
import com.example.timesync.SharedPref
import com.example.timesync.TaskDetailActivity
import com.example.timesync.adapters.TaskListAdapter
import com.example.timesync.databinding.FragmentHomeBinding
import com.example.timesync.db.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject


class TaskListFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var taskListAdapter: TaskListAdapter
    val notificationPermissionRequestCode = 103
    val allFirebaseTasks: MutableLiveData<List<Task>> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val sharedPref = SharedPref()
        val user = sharedPref.getLoginState(requireContext())
        if (user) getTasksForCurrentUser()
        else initSetup()
        return root
    }

    private fun initSetup() {
        taskListAdapter = TaskListAdapter({ task -> // onDeleteClickListener
            Log.i("Delete clicked", "fragment")
            homeViewModel.deleteTask(task)
        }, { task ->
            val editIntent = Intent(requireContext(), EditTaskActivity::class.java).apply {
                putExtra("taskId", task.id ?: -1L)
            }
            startActivity(editIntent)
        }, { task ->
            val intent = Intent(context, TaskDetailActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)
        }, context)

        binding!!.myCollectionsRv.adapter = taskListAdapter
        binding!!.myCollectionsRv.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.allTasks?.observe(viewLifecycleOwner, Observer { tasks ->
            if (!tasks?.isEmpty()!!) {
                binding?.calendarImage?.visibility = View.INVISIBLE
            }
            taskListAdapter.submitList(tasks)
        })

        binding!!.fabBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AddNewTaskActivity::class.java))
        }
        binding!!.sortBtn.setOnClickListener {
            sortByDueDate()
        }
        binding!!.filterBtn.setOnClickListener {
            showFilterOptions()

        }
        if (checkNotificationPermission()) {
            onPermissionGranted()
        } else {
            requestNotificationPermission()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true);
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
        if (selectedPriority != "All") {
            homeViewModel.getAllTasksByPriority(selectedPriority)
                ?.observe(viewLifecycleOwner) { tasks ->
                    if (tasks.isNullOrEmpty()) {
                        Toast.makeText(
                            context,
                            "No tasks found with priority $selectedPriority",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        taskListAdapter.submitList(tasks)
                    }
                }
        } else {
            homeViewModel.allTasks?.observe(viewLifecycleOwner, Observer { tasks ->
                if (tasks.isNullOrEmpty()) {
                    binding?.calendarImage?.visibility = View.VISIBLE
                    Toast.makeText(context, "No tasks found", Toast.LENGTH_SHORT).show()
                } else {
                    binding?.calendarImage?.visibility = View.INVISIBLE
                    taskListAdapter.submitList(tasks)
                }
            })
        }
    }


    private fun sortByDueDate() {
        homeViewModel.getAllTaskInASC()?.observe(
            requireActivity()
        ) { value -> taskListAdapter.submitList(value) }
    }

    private fun getTasksForCurrentUser() {
        Log.d("firebase---", "entered")
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val databaseReference = FirebaseDatabase.getInstance().reference

            if (userId != null) {
                val userTasksReference =
                    databaseReference.child("users").child(userId).child("tasks")

                userTasksReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val tasks: MutableList<Task> = mutableListOf()
                        for (taskSnapshot in dataSnapshot.children) {
                            if (taskSnapshot != null) {
                                Log.d("gengieng", taskSnapshot.value.toString())
                                val taskMap: Map<String, Any>? =
                                    taskSnapshot.value as? Map<String, Any>
                                if (taskMap != null) {
                                    val jsonString = JSONObject(taskMap).toString()
                                    val jsonObject = JSONObject(jsonString)
                                    val id = jsonObject.optLong("id", 0L)
                                    val title = jsonObject.optString("title", "")
                                    val description = jsonObject.optString("description", "")
                                    val priority = jsonObject.optString("priority", "")
                                    val status = jsonObject.optString("status", "")
                                    val dueDate = jsonObject.optLong("dueDate", 0L)
                                    val category = jsonObject.optString("category", "")
                                    val notes = jsonObject.optString("notes", "")
                                    val task = Task(
                                        id,
                                        title,
                                        description,
                                        priority,
                                        status,
                                        dueDate,
                                        category,
                                        notes
                                    )
                                    tasks.add(task)
                                    homeViewModel.insertTask(task)
                                    initSetup()
                                } else {
                                    Log.d("allllll minee task", "noooooo")
                                }
                            }
                        }
                        SharedPref().saveLoginState(requireContext(), false)
                        allFirebaseTasks.value = tasks
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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


}
