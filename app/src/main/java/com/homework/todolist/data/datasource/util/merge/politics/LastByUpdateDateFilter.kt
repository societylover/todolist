package com.homework.todolist.data.datasource.util.merge.politics

import com.homework.todolist.data.datasource.local.TodoEntity
import com.homework.todolist.data.datasource.util.merge.SingleMergeConflictResolver

/**
 * Get list of unique items by id and most last by update at marker
 */
class LastByUpdateDateFilter : SingleMergeConflictResolver<TodoEntity> {
    override fun resolve(local: List<TodoEntity>): List<TodoEntity> {
        return local.sortedByDescending { it.updatedAt }.distinctBy { it.id }
    }
}