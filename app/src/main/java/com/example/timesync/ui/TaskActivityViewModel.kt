package com.example.timesync.ui

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.timesync.db.Category
import com.example.timesync.db.Task
import com.example.timesync.db.TaskRepository

class TaskActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val allTasks: LiveData<List<Task>>
    private val allCategories: LiveData<List<Category>>

    init {
        repository = TaskRepository(application)
        allTasks = repository.allTasks
        allCategories = repository.allCategories
    }

    fun getAllTasksByCategory(category: String?): LiveData<List<Task>> {
        return repository.getAllTasksByCategory(category)
    }

    fun getAllTasksByPriority(priority: String?): LiveData<List<Task>> {
        return repository.getAllTasksByPriority(priority)
    }

//    val allTasksByDateASC: LiveData<List<Any>>
//        get() = repository.getAllTasksByDateASC()
//    val allTasksByDateDESC: LiveData<List<Any>>
//        get() = repository.getAllTasksByDateDESC()

    fun getAllTasksByStatus(status: String?): LiveData<List<Task>> {
        return repository.getAllTasksByStatus(status)
    }

    fun getTaskID(title: String?): LiveData<List<Task>> {
        return repository.getTaskID(title)
    }

    fun insert(task: Task?) {
        repository.insert(
            task
        ) { result -> Log.d(ContentValues.TAG, "onResult: $result") }
    }

    fun update(task: Task?) {
        repository.update(task)
    }

    fun delete(task: Task?) {
        repository.delete(task)
    }

    fun deleteAllTasks() {
        repository.deleteAllTasks()
    }

    fun getAllTasks(): LiveData<List<Task>> {
        return allTasks
    }

    fun insertCategory(category: Category?) {
        repository.insertCategory(category)
    }

    fun updateCategory(category: Category?) {
        repository.updateCategory(category)
    }

    fun deleteCategory(category: Category?) {
        repository.deleteCategory(category)
    }

    fun getAllCategories(): LiveData<List<Category>> {
        return allCategories
    }
}
