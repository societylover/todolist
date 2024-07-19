package com.homework.todolist.ui.screen.about

import android.content.Context
import android.content.res.Resources
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.homework.todolist.BuildConfig
import com.homework.todolist.R
import com.homework.todolist.ui.screen.about.custom_actions.TodoDivActionHandler
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.picasso.PicassoDivImageLoader
import com.yandex.div2.DivData
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * About screen (DivKit-ed)
 * @param modifier Screen modifier
 * @param contextThemeWrapper Context theme wrapper (parameter for [Div2View])
 * @param onBackPressed Custom navigation item for back-pressed
 */
@Composable
internal fun AboutScreen(
    modifier: Modifier = Modifier,
    contextThemeWrapper: ContextThemeWrapper,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val divContext = remember {
        Div2Context(
            baseContext = contextThemeWrapper,
            configuration = getDivConfiguration(context,
                object : DivNavigationHandler {
                override fun dismiss() {
                    onBackPressed()
                }
            }),
            lifecycleOwner = lifecycleOwner
        )
    }

    CompositionLocalProvider(LocalDivContext provides divContext) {
        DivView(modifier = modifier, tag = DivDataTag("About page"))
    }
}

private val noopReset: (View) -> Unit = {}
private val LocalDivContext: ProvidableCompositionLocal<Div2Context> = staticCompositionLocalOf {
    error("No Div2Context provided")
}

@Composable
private fun DivView(
    modifier: Modifier = Modifier,
    data: DivData? = null,
    tag: DivDataTag = DivDataTag("About Screen DivKit")
) {
    val divContext: Div2Context = LocalDivContext.current
    var divView: Div2View? by remember {
        mutableStateOf(null)
    }

    AndroidView(
        modifier = modifier,
        factory = { factoryContext ->
            makeDivScreen(factoryContext,
                buildDivView(divContext, factoryContext.resources).also {
                divView = it
            })
        },
        update = { _ ->
            divView?.setData(data, tag)
        },
        onReset = noopReset,
        onRelease = { _ ->
            divView?.cleanup()
        }
    )
}

/**
 * Workaround for long vertical content screen
 */
private fun makeDivScreen(context: Context, div2View: Div2View) : View {
    return ScrollView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        linearLayout.addView(div2View)
        addView(linearLayout)
    }
}

private fun buildDivView(
    divContext: Div2Context,
    resources: Resources) : Div2View
{
    val divJson = getAboutPagePageContent(resources)
    val templatesJson = divJson.optJSONObject("templates")
    val cardJson = divJson.getJSONObject("card")
    return Div2ViewFactory(divContext, templatesJson).createView(cardJson)
}

private fun getDivConfiguration(context: Context,
                                divNavigationHandler: DivNavigationHandler) : DivConfiguration {
    val imageLoader = PicassoDivImageLoader(context)
    val variableController = DivVariableController()
    initDivVariables(variableController)
    return DivConfiguration
        .Builder(imageLoader)
        .divVariableController(variableController)
        .actionHandler(TodoDivActionHandler(divNavigationHandler))
        .build()
}

private fun getAboutPagePageContent(resources: Resources) : JSONObject {
    val inputStream = resources.openRawResource(R.raw.aboutpage)
    val buffer = CharArray(2048)
    val reader = BufferedReader(InputStreamReader(inputStream))
    val builder: StringBuilder = StringBuilder(inputStream.available())
    var read: Int
    while ((reader.read(buffer).also { read = it }) != -1) {
        builder.appendRange(buffer, 0, read)
    }
    return JSONObject(builder.toString())
}

private fun initDivVariables(variableController: DivVariableController) {
    val buildType = Variable.StringVariable("buildType", BuildConfig.BUILD_TYPE)
    val versionCode = Variable.IntegerVariable("versionCode", BuildConfig.VERSION_CODE.toLong())
    val versionName = Variable.StringVariable("versionName", BuildConfig.VERSION_NAME)
    val flavour = Variable.StringVariable("flavour", BuildConfig.FLAVOR)
    val buildTime = Variable.StringVariable("buildTime", BuildConfig.BUILD_TIME)

    variableController.putOrUpdate(buildType)
    variableController.putOrUpdate(versionCode)
    variableController.putOrUpdate(versionName)
    variableController.putOrUpdate(flavour)
    variableController.putOrUpdate(buildTime)
}