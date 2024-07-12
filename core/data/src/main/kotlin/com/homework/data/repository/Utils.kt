package com.homework.data.repository

import com.homework.data.model.toTodoEntity
import com.homework.database.model.TodoEntity
import com.homework.network.model.Todo

/**
 * Function to resolve local and remote data conflicts
 */
internal fun resolveMergeErrors(local: List<TodoEntity>, remote: List<Todo>) : List<TodoEntity> {
    val notDeletedLocally = local.filter { !it.deletedLocally }
    val nonRelevantFilter = notDeletedLocally.filter { it.localOnly } + remote.map { it.toTodoEntity() }
    return nonRelevantFilter.sortedByDescending { it.updatedAt }.distinctBy { it.id }
}

