package com.homework.domain.tododetails

import com.homework.common.result.Result
import com.homework.data.repository.TodoDetailsRepository
import com.homework.domain.shared.TodoDomainItem
import com.homework.domain.shared.asDomain
import com.homework.model.data.Importance
import java.time.LocalDate
import javax.inject.Inject

/**
 * Details todo interactor
 */
class DetailsInteractor @Inject constructor(
    private val todoDetailsRepository: TodoDetailsRepository
) {

    suspend fun getTodoDetails(id: String) : Result<TodoDomainItem> {
        val item = todoDetailsRepository.getTodoDetails(id)
        return if (item is Result.Error) {
            item
        } else {
            val data = (item as Result.Success).data.asDomain()
            Result.Success(data)
        }
    }

    suspend fun removeTodoItem(id: String) : Result<Boolean> {
        return todoDetailsRepository.removeItemById(id)
    }

    suspend fun updateItem(
        id: String,
        text: String,
        done: Boolean,
        importance: Importance,
        deadlineAt: LocalDate?) : Result<Boolean> {
        return todoDetailsRepository.updateItem(id, text, done, importance, deadlineAt)
    }

    suspend fun createItem(
        text: String,
        importance: Importance,
        deadlineAt: LocalDate?
    ) : Result<Boolean> {
        return todoDetailsRepository.createItem(text, importance, deadlineAt)
    }
}