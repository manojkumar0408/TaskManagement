package com.example.timesync

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.example.timesync.db.User
import java.io.ByteArrayOutputStream
import android.util.Base64

class SharedPref {

    private val PREFS_NAME = "MyPrefs"
    private val KEY_USERID = "userId"
    private val KEY_USERNAME = "username"
    private val KEY_FIRST_NAME = "firstName"
    private val KEY_LAST_NAME = "lastName"
    private val KEY_EMAIL = "email"
    private val KEY_IMAGE_URI = "imageUri"

    fun saveUserInfo(
        context: Context,
        userID: String?,
        username: String?,
        firstName: String?,
        lastName: String?,
        email: String?
    ) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERID, userID)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_FIRST_NAME, firstName)
        editor.putString(KEY_LAST_NAME, lastName)
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun getUserInfo(context: Context): User {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString(KEY_USERID, "")
        val username = sharedPreferences.getString(KEY_USERNAME, "")
        val firstName = sharedPreferences.getString(KEY_FIRST_NAME, "")
        val lastName = sharedPreferences.getString(KEY_LAST_NAME, "")
        val email = sharedPreferences.getString(KEY_EMAIL, "")
        return User(userId, username, email, firstName, lastName)
    }

    fun hasValues(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val email = sharedPreferences.getString(KEY_EMAIL, "")
        return email?.isNotBlank() == true
    }

    fun clearSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USERNAME)
        editor.remove(KEY_FIRST_NAME)
        editor.remove(KEY_LAST_NAME)
        editor.remove(KEY_EMAIL)
        editor.apply()
        Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
    }

    fun saveImageUri(context: Context, imageUri: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_IMAGE_URI, imageUri)
        editor.apply()
    }

    fun getImageUri(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_IMAGE_URI, null)
    }
}
