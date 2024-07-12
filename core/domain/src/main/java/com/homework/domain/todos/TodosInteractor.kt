package com.homework.domain.todos

import com.homework.common.result.Result
import com.homework.data.repository.TodosRepository
import com.homework.domain.shared.TodoDomainItem
import com.homework.domain.shared.asDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodosInteractor @Inject constructor(
    private val todosRepository: TodosRepository
) {

    fun getTodosList (): Flow<List<TodoDomainItem>> {
        return todosRepository.getTodosList().map { items ->
            items.map { it.asDomain() }
        }
    }

    suspend fun fetchItems() : Result<Boolean> {
        return todosRepository.fetchItems()
    }

    suspend fun deleteItem(id: String) : Result<Boolean> {
        return todosRepository.removeItemById(id)
    }

    suspend fun changeTodoDoneState(id: String, newDone: Boolean) : Result<Boolean>{
       return todosRepository.changeItemDoneState(id, newDone)
    }

    suspend fun removeItemById (id: String) : Result<Boolean> {
        return todosRepository.removeItemById(id)
    }
}