package com.example.timesync

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.timesync.databinding.ActivityTasksMainBinding
import com.example.timesync.ui.home.HomeViewModel
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
        val sharePref = SharedPref()
        if (sharedPref != null) {
            name.text = "${sharedPref.firstName} ${sharedPref.lastName}"
            email.text = sharedPref.email
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkForTiramisu()) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.person))
                } else {
                }
            } else if (sharePref.getImageUri(this) != null) {
                var img = sharePref.getImageUri(this)
                img = Uri.parse(img.toString()).toString()
                imageView.setImageURI(Uri.parse(img))
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.person));
            }
        }

        homeViewModel.imageUri.observe(this) {
            Log.d("feinie", "etter")
            if (homeViewModel.getImageUri() != null) imageView.setImageURI(
                homeViewModel.getImageUri()?.toUri()
            ) else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.person));
            }
        }

        homeViewModel.user_Details.observe(this) {
            Log.d("feinie", "etter")
            if (homeViewModel.getUserDetails() != null) name.text = homeViewModel.getUserDetails()

        }
    }

    private fun checkForTiramisu(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
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

    override fun onResume() {
        super.onResume()
    }

}
