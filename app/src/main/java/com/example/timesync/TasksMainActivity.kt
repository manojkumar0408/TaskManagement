package com.example.timesync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timesync.adapters.TaskListAdapter
import com.example.timesync.databinding.ActivityTasksMainBinding
import com.example.timesync.db.Task
import com.example.timesync.ui.home.HomeViewModel

class TasksMainActivity : AppCompatActivity() {

    //private lateinit var appBar: ActionBar
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTasksMainBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var taskListAdapter: TaskListAdapter


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setSupportActionBar(binding.appBarTasksMain.toolbar)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        binding.appBarTasksMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val header = binding.navView.getHeaderView(0)
        val name = header.findViewById<TextView>(R.id.nav_user_name)
        val email = header.findViewById<TextView>(R.id.nav_email)
        val imageView = header.findViewById<ImageView>(R.id.nav_imageView)
        val sharedPref = SharedPref().getUserInfo(applicationContext)
        if (sharedPref != null) {
            name.text = "${sharedPref.firstName} ${sharedPref.lastName}"
            email.text = sharedPref.email
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tasks_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                SharedPref().clearSharedPreferences(applicationContext)
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
                true
            }

            R.id.action_sort -> {
                sortTasksByDueDate()
                // Implement the logic to sort tasks

                true
            }

            R.id.action_filter -> {
                showFilterOptions()

                true

            }


            else -> super.onOptionsItemSelected(item)
        }

    }

    fun sortTasksByDueDate() {
        val currentList = homeViewModel.allTasks?.value ?: return
        val sortedList = currentList.filterNotNull().sortedBy { it.dueDate }

        // Update the adapter with the sorted list
        taskListAdapter.submitList(sortedList)
        taskListAdapter.notifyDataSetChanged()

        if (sortedList.isEmpty()) {
            // Show a message if no tasks are available
            Toast.makeText(this, "No tasks to sort", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showFilterOptions() {
        val priorities = arrayOf("Low", "Medium", "High", "All")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Filter by Priority")
        builder.setItems(priorities) { _, which ->
            Log.i("detailss", which.toString())
            val selectedPriority = priorities[which]
            filterByPriority(selectedPriority)
        }
        builder.show()
    }

    fun filterByPriority(priority: String?) {
        Log.i("detailss", priority.toString())
        val filteredList = ArrayList<Task>()
        val currentList = homeViewModel.allTasks?.value ?: return
        Log.i("detailss", currentList.toString())

        if (!priority.isNullOrBlank() && priority != "All") {
            for (task in currentList) {
                Log.i("detailss", "rereiri")
                if (task != null && task.priority.equals(priority, ignoreCase = true)) {
                    filteredList.add(task)
                }
            }
        } else {
            // If priority is "All" or blank, add all non-null tasks
            filteredList.addAll(currentList.filterNotNull())
        }

        // Update the adapter with the filtered list
        taskListAdapter.submitList(filteredList)
        taskListAdapter.notifyDataSetChanged()

        if (filteredList.isEmpty()) {
            // Show a message if no tasks match the filter
            Toast.makeText(this, "No tasks found with priority: $priority", Toast.LENGTH_SHORT)
                .show()
        }
    }


}
