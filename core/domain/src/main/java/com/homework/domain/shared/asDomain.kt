package com.homework.domain.shared

import com.homework.model.data.TodoItem

internal fun TodoItem.asDomain(): TodoDomainItem {
    return TodoDomainItem(
        id = id,
        text = text,
        importance = importance,
        isDone = done,
        deadlineAt = deadlineAt
    )
}