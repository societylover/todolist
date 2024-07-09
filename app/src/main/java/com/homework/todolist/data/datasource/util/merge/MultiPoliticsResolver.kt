package com.homework.todolist.data.datasource.util.merge

import com.homework.todolist.data.datasource.local.TodoEntity
import com.homework.todolist.data.datasource.util.merge.politics.LastByUpdateDateFilter
import com.homework.todolist.data.datasource.util.merge.politics.LocallyDeletedFilter
import com.homework.todolist.data.datasource.util.merge.politics.NonRelevantLocalFilter
import com.homework.todolist.data.model.TodoItem

class MultiPoliticsResolver {

    /**
     * Calculate actual items list state.
     * 1. Filter from remote list items where local items has [TodoEntity.deletedLocally] flag as true (by it's id)
     * local {                          remote {                    current {
     *      (id = 1, deleted),                (id = 1),                    (id = 3, not deleted, local, remote flag),
     *      (id = 2, deleted),                (id = 4),    filter =>       (id = 4, not deleted, server),
     *      (id = 3, not deleted, remote),    (id = 5),                    (id = 4, not deleted, local),
     *      (id = 4, not deleted),            (id = 6)                     (id = 5, not deleted, local),
     *      (id = 5, not deleted)       }                                  (id = 5, not deleted, server)
     * }                                                                   (id = 6, not deleted)
     *                                                              }
     *
     * 2. Filter values what "local with remote exist flag"
     * current {
     *      (id = 4, not deleted, server),
     *      (id = 4, not deleted, local),
     *      (id = 5, not deleted, local),
     *      (id = 5, not deleted, server),
     *      (id = 6, not deleted)
     * }
     *
     * 3. Get most relevant item by it's updateAt marker (with same id) or take server variant if updateAt equals
     * current {                                                    result {
     *       (id = 4, not deleted, server, updateAt = 100),               (id = 4, not deleted, local,  updateAt = 200)
     *       (id = 4, not deleted, local,  updateAt = 200),               (id = 5, not deleted, server, updateAt = 100)
     *       (id = 5, not deleted, local,  updateAt = 100),    =>         (id = 6, not deleted)
     *       (id = 5, not deleted, server, updateAt = 100),         }
     *       (id = 6, not deleted)
     * }
     * @param localItems current items in local source
     * @param remoteItems remote items from remote source
     */
    fun resolve(localItems: List<TodoEntity>, remoteItems: List<TodoItem>) : List<TodoEntity> {
        val locallyDeletedFilter = LocallyDeletedFilter().resolve(localItems, remoteItems)
        val nonRelevantLocalFilter = NonRelevantLocalFilter().resolve(locallyDeletedFilter, remoteItems)
        return LastByUpdateDateFilter().resolve(nonRelevantLocalFilter)
    }
}