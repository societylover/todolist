package com.homework.todolist.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todos WHERE deletedLocally = 0 ORDER BY createdAt DESC")
    fun getItemsList(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getItemsSnapshot() : List<TodoEntity>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getItemDetails(id: String): TodoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(todoItem: TodoEntity) : Long

    @Update
    suspend fun updateItem(todoItem: TodoEntity) : Int

    @Transaction
    suspend fun setItemsList(items: List<TodoEntity>) {
        deleteAllItems()
        items.forEach { addItem(it) }
    }

    @Query("UPDATE todos SET deletedLocally = 1 WHERE id = :id")
    suspend fun markItemAsDeleted(id: String) : Int

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun removeItemById(id: String) : Int

    @Query("DELETE FROM todos")
    suspend fun deleteAllItems()
}