package com.example.timesync.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category?)

    @Update
    fun update(category: Category?)

    @Delete
    fun delete(category: Category?)

    @Query("SELECT * FROM category_table")
    fun getAllCategories(): LiveData<List<Category?>?>?

    @Query("SELECT id FROM category_table WHERE category_table.name = :catName")
    fun getCategoryID(catName: String?): Int
}