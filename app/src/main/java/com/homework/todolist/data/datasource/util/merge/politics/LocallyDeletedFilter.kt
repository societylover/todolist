package com.homework.todolist.data.datasource.util.merge.politics

import com.homework.todolist.data.datasource.local.TodoEntity
import com.homework.todolist.data.datasource.util.merge.DuoMergeConflictResolver
import com.homework.todolist.data.model.TodoItem

/**
 * Resolve items that deleted locally, but
 */
internal class LocallyDeletedFilter : DuoMergeConflictResolver<TodoEntity, TodoItem> {

    override fun resolve(local: List<TodoEntity>, remote: List<TodoItem>): List<TodoEntity> {
        return local.filter { !it.deletedLocally }
    }

}