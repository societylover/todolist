package com.homework.todolist.data.datasource.util.merge

/**
 * Resolve conflict of two list items
 */
internal interface DuoMergeConflictResolver <L, in R> : MergeConflictResolver {

    /**
     * Conflict resolving function
     * @param local Local items
     * @param remote Remote items
     * @return List of local elements that will result from conflict resolution
     */
    fun resolve(local: List<L>, remote: List<R>) : List<L>
}