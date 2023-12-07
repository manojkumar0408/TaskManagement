package com.example.timesync

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.timesync.db.Category
import com.example.timesync.db.CategoryDao
import com.example.timesync.db.Task
import com.example.timesync.db.TaskDao

@Database(entities = [Category::class, Task::class], version = 2, exportSchema = false)
abstract class TimeSyncDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TimeSyncDatabase? = null

        fun getDatabase(context: Context): TimeSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, TimeSyncDatabase::class.java, "time_sync_database"
                ).allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
