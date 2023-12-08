package com.example.timesync.db

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class FirebaseDatabaseManager {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getUserData(userId: String, callback: (User?) -> Unit) {
        val userReference = databaseReference.child("users").child(userId)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                callback(user)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                callback(null)
            }
        })
    }
}
