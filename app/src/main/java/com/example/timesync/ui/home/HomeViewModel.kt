package com.example.timesync.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.timesync.TimeSyncDatabase
import com.example.timesync.db.Category
import com.example.timesync.db.CategoryDao
import com.example.timesync.db.Task
import com.example.timesync.db.TaskDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database: TimeSyncDatabase = TimeSyncDatabase.getDatabase(application)
    private val categoryDao: CategoryDao = database.categoryDao()
    private val taskDao: TaskDao = database.taskDao()
    val allTasks: LiveData<List<Task?>?>? = taskDao.getAllTasks()

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    private val _filteredTasks = MutableLiveData<List<Task>>()
    val filteredTasks: LiveData<List<Task>> = _filteredTasks
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val databaseReference = FirebaseDatabase.getInstance().reference

    private val _imageUri = MutableLiveData<String>()
    val imageUri: LiveData<String> get() = _imageUri
    private val _user_Details = MutableLiveData<String>()
    val user_Details: LiveData<String> get() = _user_Details
    fun setImageUri(uri: String) {
        _imageUri.value = uri
    }

    fun getImageUri(): String? {
        return _imageUri.value
    }

    fun setUserDetails(name: String) {
        _user_Details.value = name
    }

    fun getUserDetails(): String? {
        return _user_Details.value
    }

    fun insertCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.insert(category)
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun deleteTask(task: Task) {
        Log.i("clicked", "del")
        viewModelScope.launch {
            deleteFirebaseTask(task.id!!)
            taskDao.delete(task)
        }
    }

    fun deleteFirebaseTask(taskId: Long) {
        if (userId != null) {
            val userTasksReference =
                databaseReference.child("users").child(userId).child("tasks")
                    .child(taskId.toString())
            userTasksReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("task", "deleted")
                } else {
                    Log.d("task", "delete failed")
                }
            }
        }
    }

    fun getTaskById(id: Long): LiveData<Task> {
        return taskDao.getTaskById(id) // Assuming this method returns LiveData<Task>
    }

    fun update(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskDao.update(task)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getAllTasksByPriority(priority: String?): LiveData<List<Task?>?>? {
        return taskDao.getAllTasksByPriority(priority)
    }

    fun getAllTaskInASC(): LiveData<List<Task?>?>? {
        return taskDao.getAllTasksByDateASC()
    }

    fun getAllTaskInDESC(): LiveData<List<Task?>?>? {
        return taskDao.getAllTasksByDateDESC()
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            taskDao.deleteAllTasks()
        }
    }


}