package com.dave.spellchecker.network.common

import com.dave.spellchecker.util.LocalDateTimeAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.time.LocalDateTime
import java.util.*

sealed class HttpResult<out T> {
    object Empty : HttpResult<Nothing>()
    data class Success<out T>(val data: T) : HttpResult<T>()
    data class Error(val httpStatusCode: HttpStatusCode, val error: UnsplashError?, val message: String = "") : HttpResult<Nothing>() {
        companion object {
            private val converter by lazy {
                Moshi.Builder()
                    .add(Date::class.java, Rfc3339DateJsonAdapter())
                    .add(LocalDateTime::class.java, LocalDateTimeAdapter())
                    .addLast(KotlinJsonAdapterFactory())
                    .build().adapter(UnsplashError::class.java)
            }

            fun <T> parse(response: Response<T>): Error {
                val statusCode = HttpStatusCode.getByCode(response.code())
                val unsplashError = response.errorBody().toUnsplashError()
                Timber.e("Status code : $statusCode. ${unsplashError?.errors?.joinToString { it } ?: statusCode.description}")
                return Error(statusCode, unsplashError, statusCode.description)
            }

            private fun ResponseBody?.toUnsplashError(): UnsplashError? = this?.let {
                return@let try {
                    converter.fromJson(it.source())
                } catch (e: kotlin.Exception) {
                    Timber.e(e)
                    null
                }
            }
        }
    }

    data class Exception(val exception: Throwable) : HttpResult<Nothing>()
}
