package com.example.timesync

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.timesync.databinding.ActivityLoginPageBinding
import com.example.timesync.db.FirebaseDatabaseManager
import com.example.timesync.ui.SignUpActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var appBar: ActionBar

    private val PERMISSION_CODE = 1001
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginPageBinding
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseDatabaseManager = FirebaseDatabaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        // Get support action bar
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        auth = Firebase.auth
        if(!getPermissions())
            requestGalleryPermission()

        if (SharedPref().hasValues(applicationContext)) {
            val signUpIntent = Intent(this, TasksMainActivity::class.java)
            signUpIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(signUpIntent)
            finish()
        }

        val email = findViewById<TextInputLayout>(R.id.emailAddress)
        val password = findViewById<TextInputLayout>(R.id.password)
        val signup = findViewById<Button>(R.id.signup)
        val login = findViewById<Button>(R.id.login)

        signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            val animationBundle = ActivityOptions.makeCustomAnimation(
                this, R.anim.slide_in_right, R.anim.slide_out_left
            ).toBundle()
            startActivity(intent, animationBundle)
        }

        login.setOnClickListener {
            val userEmail = email.editText?.text.toString()
            val userPassword = password.editText?.text.toString()

            if (userEmail.isNullOrEmpty() && !userPassword.isNullOrEmpty()) {
                Toast.makeText(
                    this, resources.getString(R.string.email_not_valid), Toast.LENGTH_SHORT
                ).show()
            } else if (!isValidEmail(userEmail)) {
                Toast.makeText(
                    this, resources.getString(R.string.email_not_valid), Toast.LENGTH_SHORT
                ).show()
            } else {
                login(userEmail, userPassword)
            }
        }
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun getPermissions(): Boolean {
      //  Log.d("permissiom", "check Gall")
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestGalleryPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    firebaseDatabaseManager.getUserData(user.uid) { userData ->
                        userData?.let {
                            SharedPref().saveUserInfo(
                                this, it.userId, it.username, it.firstName, it.lastName, it.email
                            );
                            Toast.makeText(this, "Welcome ${user?.email}", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, TasksMainActivity::class.java)
                            val animationBundle = ActivityOptions.makeCustomAnimation(
                                this, R.anim.slide_in_right, R.anim.slide_out_left
                            ).toBundle()
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent, animationBundle)
                            finish()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}



