package com.homework.todolist.data.datasource.util.merge

/**
 * Resolve conflict of one list
 */
internal interface SingleMergeConflictResolver <L> : MergeConflictResolver {

    /**
     * Conflict resolving function
     * @param local Local items
     * @return List of local elements that will result from conflict resolution
     */
    fun resolve(local: List<L>) : List<L>
}