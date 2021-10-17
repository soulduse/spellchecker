package com.dave.spellchecker.network.common

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException

sealed class Resource<out TValue> {
    data class Success<out TValue>(val data: TValue) : Resource<TValue>()
    data class Error(val cause: Cause, val message: String = "") : Resource<Nothing>() {
        companion object {

            fun parse(response: HttpResult.Error): Error {
                val cause = when (response.httpStatusCode) {
                    HttpStatusCode.UNAUTHORIZED -> Cause.NOT_AUTHENTICATED
                    else -> Cause.UNEXPECTED
                }

                val message = response.error?.errors?.joinToString { it }
                    ?: response.httpStatusCode.description
                return Error(cause, message)
            }

            fun parse(throwable: Throwable): Error {
                return when (throwable) {
                    is JsonDataException,
                    is JsonEncodingException -> Error(
                        Cause.JSON_DATA_EXCEPTION,
                        throwable.localizedMessage ?: "Unexpected error"
                    )
                    else -> Error(
                        Cause.UNEXPECTED,
                        throwable.localizedMessage ?: "Unexpected error"
                    )
                }
            }
        }
    }

    data class Loading(val isLoading: Boolean) : Resource<Nothing>()
}
