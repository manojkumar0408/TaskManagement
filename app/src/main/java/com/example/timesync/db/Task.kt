package com.example.timesync.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var title: String,
    var description: String,
    var priority: String,
    var status: String,
    var dueDate: Long,
    var category: String // Foreign key linking to Category
) {

}
