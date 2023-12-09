package com.example.timesync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.timesync.databinding.ActivityTasksMainBinding
import com.example.timesync.ui.home.HomeViewModel
import com.example.timesync.ui.home.TaskListFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


class TasksMainActivity : AppCompatActivity() {

    //private lateinit var appBar: ActionBar
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTasksMainBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var navView: NavigationView

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
        navView = binding.navView
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
                homeViewModel.deleteAllTasks()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
