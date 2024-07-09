package com.homework.todolist.data.datasource.util.merge.politics

import com.homework.todolist.data.datasource.local.TodoEntity
import com.homework.todolist.data.datasource.local.toTodoEntity
import com.homework.todolist.data.datasource.util.merge.DuoMergeConflictResolver
import com.homework.todolist.data.model.TodoItem

/**
 * Filter values what exists locally but not in remote list AND local values not new because of creation
 */
class NonRelevantLocalFilter : DuoMergeConflictResolver<TodoEntity, TodoItem> {

    override fun resolve(local: List<TodoEntity>, remote: List<TodoItem>): List<TodoEntity> {
        // LocalOnly = false exists and in remote list, so can delete it from local list
        return local.filter { it.localOnly } + remote.map { it.toTodoEntity(false) }
    }
}