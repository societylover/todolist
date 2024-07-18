package com.homework.todolist.ui.screen.about.custom_actions

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.homework.todolist.ui.screen.about.DivNavigationHandler
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivViewFacade
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction

/**
 * DivKit custom action handler
 * @param navigationHandler Navigation handler for DivKit
 */
internal class TodoDivActionHandler(
    private val navigationHandler: DivNavigationHandler
) : DivActionHandler() {
    override fun handleAction(
        action: DivAction,
        view: DivViewFacade,
        resolver: ExpressionResolver
    ): Boolean {
        val url = action.url?.evaluate(resolver) ?: return super.handleAction(action, view, resolver)

        return if (url.scheme == CUSTOM_SCHEME && handleCustomAction(url, view.view.context)) {
            true
        } else {
            super.handleAction(action, view, resolver)
        }
    }

    private fun handleCustomAction(action: Uri, context: Context): Boolean {
        return when (action.host) {
            "toast" -> {
                Toast.makeText(context, action.query, Toast.LENGTH_SHORT).show()
                true
            }
            "dismiss" -> {
                navigationHandler.dismiss()
                true
            }
            else -> false
        }
    }

    companion object {
        private const val CUSTOM_SCHEME = "todo-action"
    }
}
