package com.example.timesync.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = false) val id: Long? = 0,
    var title: String,
    var description: String,
    var priority: String?,
    var status: String,
    var dueDate: Long,
    var category: String
) {
}
