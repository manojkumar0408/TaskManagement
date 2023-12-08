package com.example.timesync.ui.home

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.timesync.db.Task
import com.example.timesync.db.TaskRepository

class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(application)

    fun getTaskById(id: Long): LiveData<Task> {
        return repository.getTaskById(id)
    }

    fun updateTaskNotes(taskId: Long, notes: String) {
        // Use AsyncTask or coroutines to perform this operation
        AsyncTask.execute {
            val task = repository.getTaskByIdSync(taskId)
            task?.let {
                it.notes = notes
                repository.update(it)
            }
        }
    }

}
