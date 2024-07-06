package com.homework.todolist.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.homework.todolist.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getItemsList(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getItemDetails(id: String): TodoItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(todoItem: TodoItem) : Long

    @Update
    suspend fun updateItem(todoItem: TodoItem) : Int

    @Transaction
    suspend fun setItemsList(items: List<TodoItem>) {
        // Clear all existing items
        deleteAllItems()
        // Insert new items
        items.forEach { addItem(it) }
    }

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun removeItemById(id: String) : Int

    @Query("DELETE FROM todo_items")
    suspend fun deleteAllItems()
}