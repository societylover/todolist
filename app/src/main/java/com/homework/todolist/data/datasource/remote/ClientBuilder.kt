package com.homework.todolist.data.datasource.remote

import android.util.Log
import com.homework.todolist.BuildConfig
import com.homework.todolist.data.provider.ApiParamsProvider
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

internal fun HttpClient(apiParamsProvider: ApiParamsProvider) =
    io.ktor.client.HttpClient(OkHttp) {
        engine {
            config {
                interceptors() += Interceptor { chain ->
                    val (token, revision) = Pair(
                        apiParamsProvider.getClientTokenBlocking(),
                        apiParamsProvider.getKnownRevisionBlocking()
                    )
                    if (token == null) {
                        throw HttpPreparationException(
                            "Token must be not null.",
                            PreparationExceptionReason.BAD_TOKEN
                        );
                    }
                    if (revision == null) {
                        throw HttpPreparationException(
                            "Null revision provided.",
                            PreparationExceptionReason.BAD_REVISION
                        );
                    }
                    val requestBuilder = chain.request().newBuilder()
                    requestBuilder.addHeader(REVISION_HEADER, "$revision")
                    requestBuilder.addHeader(AUTHORIZATION_HEADER, "OAuth $token")
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }

                interceptors() += Interceptor { chain ->
                    ResponseInterceptor(chain, apiParamsProvider)
                }
            }
        }

        defaultRequest {
            url(BuildConfig.BASE_URL)
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                // isLenient = true
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
    }

private fun ResponseInterceptor(
    chain: Interceptor.Chain,
    apiParamsProvider: ApiParamsProvider
): Response {

    val response: Response = chain.proceed(chain.request())

    return if (response.isSuccessful) {
        val responseBody = response.body
        val contentType = responseBody?.contentType()
        val responseBodyString = responseBody?.string()

        responseBodyString?.let {
            val jsonElement = Json.parseToJsonElement(it)
            val jsonObject = jsonElement.jsonObject
            val revision = jsonObject["revision"]?.jsonPrimitive?.content
            revision?.let { rev ->
                apiParamsProvider.setKnownRevisionBlocking(rev.toLong())
            }
        }

        val newResponseBody = responseBodyString?.toResponseBody(contentType)
        response.newBuilder().body(newResponseBody).build()
    } else {
        response
    }
}

private const val REVISION_HEADER = "X-Last-Known-Revision"
private const val AUTHORIZATION_HEADER = "Authorization"
private const val CLIENT_LOG_TAG = "HTTP-REQUEST"