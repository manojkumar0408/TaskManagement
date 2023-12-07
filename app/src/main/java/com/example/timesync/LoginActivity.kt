package com.example.timesync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate

import com.example.timesync.ui.SignUpActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var appBar: ActionBar
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        // Get support action bar
        appBar = supportActionBar!!
        appBar.title="Login"
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        auth = Firebase.auth

        if (checkUserSignedIn()) {
            val signUpIntent = Intent(this, TasksMainActivity::class.java)
            startActivity(signUpIntent)
        } else {
            val signUpIntent = Intent(this, LoginActivity::class.java)
            startActivity(signUpIntent)
        }
        val email = findViewById<TextInputLayout>(R.id.emailAddress)
        val password = findViewById<TextInputLayout>(R.id.password)
        val signup = findViewById<Button>(R.id.signup)
        val login = findViewById<Button>(R.id.login)

        signup.setOnClickListener {
            val signUpIntent = Intent(this, SignUpActivity::class.java)
            startActivity(signUpIntent)
        }

        login.setOnClickListener {
            //login(email.editText?.text.toString(), password.editText?.text.toString())
            val userEmail = email.editText?.text.toString()
            val userPassword = password.editText?.text.toString()

            if (!userEmail.isNullOrEmpty() && !userPassword.isNullOrEmpty()) {
                login(userEmail, userPassword)
            } else {
                Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkUserSignedIn(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    private fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Toast.makeText(this, "Welcome ${user?.email}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, TasksMainActivity::class.java))
            } else {
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Toast.makeText(this, "Welcome ${user?.email}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, TasksMainActivity::class.java))
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