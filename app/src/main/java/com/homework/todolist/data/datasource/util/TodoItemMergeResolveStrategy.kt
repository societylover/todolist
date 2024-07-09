package com.homework.todolist.data.datasource.util

import com.homework.todolist.data.datasource.local.TodoEntity
import com.homework.todolist.data.model.TodoItem

/**
 *
 */
abstract class TodoItemMergeResolveStrategy {
    abstract fun resolve(local: List<TodoEntity>, remote: List<TodoItem>)
}