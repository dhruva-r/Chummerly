package com.example.chummerly.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.chummerly.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

    @Insert
    fun insert(category: Category)

    @Query("SELECT * FROM category WHERE name = :name")
    fun findByName(name: String): Category

    @Update
    fun updateCategory(vararg category: Category)

    @Delete
    fun delete(category: Category)
}