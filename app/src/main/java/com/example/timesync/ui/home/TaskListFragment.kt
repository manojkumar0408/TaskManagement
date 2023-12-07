package com.example.timesync.ui.home

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
import com.example.timesync.AddNewTaskActivity
import com.example.timesync.EditTaskActivity
import com.example.timesync.databinding.FragmentHomeBinding
import com.example.timesync.adapters.TaskListAdapter

class TaskListFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var taskListAdapter: TaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        // Initialize ViewModel
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

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
            }
        )
        binding!!.myCollectionsRv.adapter = taskListAdapter
        binding!!.myCollectionsRv.layoutManager = LinearLayoutManager(requireContext())

        // Observe LiveData and update RecyclerView when data changes
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
