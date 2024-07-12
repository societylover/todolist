package com.homework.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.homework.database.dao.TodoEntityDao
import com.homework.database.model.TodoEntity

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoItemDao(): TodoEntityDao
}