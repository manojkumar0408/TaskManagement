package com.example.timesync.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task?): Long

    @Update
    fun update(task: Task?)

    @Delete
    fun delete(task: Task?)

    @Query("DELETE FROM TASK_TABLE")
    fun deleteAllTasks()

    @Query("SELECT * FROM TASK_TABLE ORDER BY ID ASC")
    fun getAllTasks(): LiveData<List<Task?>?>?

//    @Query("SELECT id, dueDate FROM TASK_TABLE ")
//    fun getAllDueDates(): LiveData<List<Task?>?>?

    @Query("SELECT * FROM TASK_TABLE WHERE category = :category")
    fun getAllTasksByCategory(category: String?): LiveData<List<Task?>?>?

    @Query("SELECT * FROM TASK_TABLE WHERE priority = :priority")
    fun getAllTasksByPriority(priority: String?): LiveData<List<Task?>?>?

    @Query("SELECT * FROM TASK_TABLE ORDER BY dueDate ASC")
    fun getAllTasksByDateASC(): LiveData<List<Task?>?>?

    @Query("SELECT * FROM TASK_TABLE ORDER BY dueDate DESC")
    fun getAllTasksByDateDESC(): LiveData<List<Task?>?>?

    @Query("SELECT * FROM TASK_TABLE WHERE title = :title")
    fun getTaskID(title: String?): LiveData<List<Task?>?>?

    @Query("SELECT * FROM TASK_TABLE WHERE status = :status")
    fun getAllTasksByStatus(status: String?): LiveData<List<Task?>?>?
}
