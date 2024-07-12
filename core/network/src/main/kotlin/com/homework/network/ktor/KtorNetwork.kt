package com.homework.network.ktor

import android.util.Log
import com.homework.datastore.ApiParamsProvider
import com.homework.network.ktor.KtorInterceptor.makeRequestPrepareInterceptor
import com.homework.network.ktor.KtorInterceptor.makeRevisionHandleInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.UnknownHostException

internal class KtorNetwork(apiParamsProvider: ApiParamsProvider, baseUrl: String) {
    val client = HttpClient(OkHttp) {
        engine { configBuilder(apiParamsProvider) }
        defaultRequest { url(baseUrl) }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                encodeDefaults = true
            })
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.v(CLIENT_LOG_TAG, message)
                }
            }
            level = LogLevel.ALL
        }

        install(HttpRequestRetry) {
            maxRetries = MAX_RETRIES_COUNT

            retryIf { _, response ->
                !response.status.isSuccess() && response.status != HttpStatusCode.Unauthorized
            }
            retryOnExceptionIf { _, cause ->
                cause !is IOException || cause !is UnknownHostException
            }

            exponentialDelay()
        }
    }

    private fun OkHttpConfig.configBuilder(apiParamsProvider: ApiParamsProvider) {
        config {
            interceptors() += makeRequestPrepareInterceptor(apiParamsProvider)
            interceptors() += makeRevisionHandleInterceptor(apiParamsProvider)
        }
    }

    companion object {
        internal const val REVISION_HEADER = "X-Last-Known-Revision"
        internal const val AUTHORIZATION_HEADER = "Authorization"
        internal const val CLIENT_LOG_TAG = "HTTP-REQUEST"
        internal const val REVISION_FIELD = "revision"

        private const val MAX_RETRIES_COUNT = 5
    }
}