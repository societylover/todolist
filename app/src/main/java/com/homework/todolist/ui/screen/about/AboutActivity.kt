package com.homework.todolist.ui.screen.about

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.homework.todolist.BuildConfig
import com.homework.todolist.R
import com.homework.todolist.ui.screen.about.custom_actions.TodoDivActionHandler
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.picasso.PicassoDivImageLoader
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

@AndroidEntryPoint
class AboutActivity : AppCompatActivity(), DivNavigationHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_activity)
        val list: LinearLayout = findViewById(R.id.list)
        list.addView(buildDivView())
    }

    private fun buildDivView() : Div2View {
        val divJson = getAboutPagePageContent()
        val templatesJson = divJson.optJSONObject("templates")
        val cardJson = divJson.getJSONObject("card")

        val divContext = Div2Context(
            baseContext = this,
            configuration = getDivConfiguration(),
            lifecycleOwner = this
        )

        return Div2ViewFactory(divContext, templatesJson).createView(cardJson)
    }

    private fun getDivConfiguration() : DivConfiguration {
        val imageLoader = PicassoDivImageLoader(this)
        val variableController = DivVariableController()
        initDivVariables(variableController)
        return DivConfiguration
            .Builder(imageLoader)
            .divVariableController(variableController)
            .actionHandler(TodoDivActionHandler(this))
            .build()

    }

    private fun getAboutPagePageContent() : JSONObject {
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

    override fun dismiss() {
        onBackPressedDispatcher.onBackPressed()
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
}