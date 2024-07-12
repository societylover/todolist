package com.homework.network.ktor

import android.util.Log
import com.homework.datastore.ApiParamsProvider
import com.homework.network.ktor.KtorNetwork.Companion.AUTHORIZATION_HEADER
import com.homework.network.ktor.KtorNetwork.Companion.CLIENT_LOG_TAG
import com.homework.network.ktor.KtorNetwork.Companion.REVISION_FIELD
import com.homework.network.ktor.KtorNetwork.Companion.REVISION_HEADER
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

internal object KtorInterceptor {

    /**
     * Create request preparation interceptor
     * Set token and revision values
     */
    internal fun makeRequestPrepareInterceptor(apiParamsProvider: ApiParamsProvider) =
        Interceptor { chain ->
            val (token, revision) = Pair(
                apiParamsProvider.getClientToken(),
                apiParamsProvider.getKnownRevision()
            )
            if (token == null) {
                Log.v(CLIENT_LOG_TAG, "Token must be not null.")
            }
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.addHeader(REVISION_HEADER, "$revision")
            requestBuilder.addHeader(AUTHORIZATION_HEADER, "OAuth $token")
            val request = requestBuilder.build()
            chain.proceed(request)
        }

    /**
     * Interceptor for getting and save revision tag from response
     */
    internal fun makeRevisionHandleInterceptor(apiParamsProvider: ApiParamsProvider) : Interceptor {
        return Interceptor { chain ->
            val response: Response = chain.proceed(chain.request())
            if (response.isSuccessful) {
                val responseBody = response.body
                val contentType = responseBody?.contentType()
                val responseBodyString = responseBody?.string()

                responseBodyString?.let {
                    val jsonElement = Json.parseToJsonElement(it)
                    val jsonObject = jsonElement.jsonObject
                    val revision = jsonObject[REVISION_FIELD]?.jsonPrimitive?.content
                    revision?.let { rev ->
                        apiParamsProvider.setKnownRevision(rev.toLong())
                    }
                }

                val newResponseBody = responseBodyString?.toResponseBody(contentType)
                response.newBuilder().body(newResponseBody).build()
            } else {
                response
            }
        }
    }
}