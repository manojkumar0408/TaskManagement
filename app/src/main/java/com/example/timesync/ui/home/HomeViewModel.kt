package com.example.timesync.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.timesync.db.Task
import com.example.timesync.db.TaskRepository
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.timesync.TimeSyncDatabase
import com.example.timesync.db.Category
import com.example.timesync.db.CategoryDao
import com.example.timesync.db.TaskDao
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database: TimeSyncDatabase = TimeSyncDatabase.getDatabase(application)
    private val categoryDao: CategoryDao = database.categoryDao()
    private val taskDao: TaskDao = database.taskDao()

    val allCategories: LiveData<List<Category?>?>? = categoryDao.getAllCategories()
    val allTasks: LiveData<List<Task?>?>? = taskDao.getAllTasks()

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
            taskDao.delete(task)
        }
    }
}