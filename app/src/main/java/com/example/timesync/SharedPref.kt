package com.example.timesync

import android.content.Context

class SharedPref  {
     val PREFS_NAME = "MyPrefs"
      val KEY_USERNAME = ""
      val KEY_FIRST_NAME = ""
      val KEY_LAST_NAME = ""
    val KEY_EMAIL=""
    fun saveUserInfo(context: Context, username: String?, firstName: String?, lastName: String?, email:String?) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_FIRST_NAME, firstName)
        editor.putString(KEY_LAST_NAME, lastName)
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun getUserInfo(context: Context): UserInfo {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(KEY_USERNAME, "")
        val firstName = sharedPreferences.getString(KEY_FIRST_NAME, "")
        val lastName = sharedPreferences.getString(KEY_LAST_NAME, "")
        val email = sharedPreferences.getString(KEY_EMAIL,"")
        return UserInfo(username, firstName, lastName, email)
    }

    class UserInfo(val username: String?, val firstName: String?, val lastName: String?, val email: String?)
}
