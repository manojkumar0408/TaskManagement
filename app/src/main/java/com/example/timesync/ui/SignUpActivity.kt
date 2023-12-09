package com.example.timesync.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.timesync.R
import com.example.timesync.SharedPref
import com.example.timesync.TasksMainActivity
import com.example.timesync.db.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var appBar: ActionBar
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        // Get support action bar
        auth = Firebase.auth
        val email = findViewById<EditText>(R.id.email1)
        val fname = findViewById<EditText>(R.id.fname)
        val lname = findViewById<EditText>(R.id.lname)
        val username = findViewById<EditText>(R.id.user1)
        val password = findViewById<EditText>(R.id.password)
        val signup = findViewById<Button>(R.id.signup)
        database = Firebase.database.reference

        signup.setOnClickListener {
            //signup(email.editText?.text.toString(), password.editText?.text.toString())
            val userEmail = email.text.toString()
            val userFirstName = fname.text.toString()
            val userLastName = lname.text.toString()
            val userUsername = username.text.toString()
            val userPassword = password.text.toString()
            if (!isValidEmail(userEmail)) {
                Toast.makeText(
                    this, resources.getString(R.string.email_not_valid), Toast.LENGTH_SHORT
                ).show()
            } else if (userPassword.length <= 6)
                Toast.makeText(this, "password should be atleast 6 characters", Toast.LENGTH_SHORT)
                    .show()
            else if (userEmail.isNotBlank() && userFirstName.isNotBlank() && userLastName.isNotBlank() && userUsername.isNotBlank() && userPassword.isNotBlank()) {
                signup(userEmail, userPassword, userFirstName, userLastName, userUsername)
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun signup(
        email: String, password: String, FirstName: String, LastName: String, Username: String
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val SharedPref = SharedPref()
                SharedPref.saveUserInfo(this, user?.uid, Username, FirstName, LastName, email);
                if (user?.uid != null) {
                    val user = User(user?.uid!!, Username, email, FirstName, LastName)
                    database.child("users").child(user.userId!!).setValue(user)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Welcome ${FirstName}", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this, TasksMainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
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