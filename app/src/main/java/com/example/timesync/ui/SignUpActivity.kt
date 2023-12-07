package com.example.timesync.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.timesync.R
import com.example.timesync.SharedPref
import com.example.timesync.TasksMainActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = Firebase.auth
        val email = findViewById<EditText>(R.id.email1)
        val fname = findViewById<EditText>(R.id.fname)
        val lname = findViewById<EditText>(R.id.lname)
        val username = findViewById<EditText>(R.id.user1)
        val password = findViewById<EditText>(R.id.password)
        val signup = findViewById<Button>(R.id.signup)

        signup.setOnClickListener {
            //signup(email.editText?.text.toString(), password.editText?.text.toString())
            val userEmail = email.text.toString()
            val userFirstName = fname.text.toString()
            val userLastName = lname.text.toString()
            val userUsername = username.text.toString()
            val userPassword = password.text.toString()

            if (userEmail.isNotBlank() && userFirstName.isNotBlank() &&
                userLastName.isNotBlank() && userUsername.isNotBlank() && userPassword.isNotBlank()
            ) {
                signup(userEmail, userPassword, userFirstName, userLastName, userUsername)
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signup(email: String, password: String,FirstName: String, LastName: String, Username: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val SharedPref = SharedPref()
                SharedPref.saveUserInfo(this, Username, FirstName, LastName, email);
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