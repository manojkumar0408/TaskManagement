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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database: TimeSyncDatabase = TimeSyncDatabase.getDatabase(application)
    private val categoryDao: CategoryDao = database.categoryDao()
    private val taskDao: TaskDao = database.taskDao()
    val repository = TaskRepository(application)
    val allCategories: LiveData<List<Category?>?>? = categoryDao.getAllCategories()
    val allTasks: LiveData<List<Task?>?>? = taskDao.getAllTasks()

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

//    private val _priorityFilter = MutableLiveData<String>()
//
//    val filteredTasks: LiveData<List<Task>> = Transformations.switchMap(_priorityFilter) { priority ->
//        if (priority.isEmpty() || priority == "All") {
//            repository.getAllTasks() // Your method to get all tasks
//        } else {
//            repository.getTasksByPriority(priority) // Your method to get tasks filtered by priority
//        }
//    }
//    fun setPriorityFilter(priority: String) {
//        _priorityFilter.value = priority
//    }
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
    fun getTaskById(id: Long): LiveData<Task>  {
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
}