package com.dave.spellchecker.network.extensions

import com.dave.spellchecker.network.common.HttpResult
import com.dave.spellchecker.network.common.HttpStatusCode
import com.dave.spellchecker.network.exceptions.NoNetworkException
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

suspend fun <T : Any> Response<T>.awaitSafe(): HttpResult<T> {
    return try {
        val response = this
        when {
            response.isSuccessful -> {
                val body = response.body()

                when {
                    body.isValidBody() -> HttpResult.Success(body!!)
                    response.noContent() -> HttpResult.Empty
                    else -> {
                        val exception =
                            IllegalStateException("Body is empty with ${response.code()} status code.")
                        Timber.e(exception)
                        HttpResult.Exception(exception)
                    }
                }
            }
            else -> HttpResult.Error.parse(response)
        }
    } catch (e: JsonEncodingException) {
        e.getThrowException()
    } catch (e: JsonDataException) {
        e.getThrowException()
    } catch (e: NoNetworkException) {
        e.getThrowException()
    } catch (e: IOException) {
        e.getThrowException()
    }
}
private fun Any?.isValidBody() = this != null && this != ""
private fun <T : Any> Response<T>.noContent() = code().toHttpCode() == HttpStatusCode.NO_CONTENT
private fun Int.toHttpCode() = HttpStatusCode.getByCode(this)
private fun Exception.getThrowException(): HttpResult.Exception {
    Timber.e(this)
    return HttpResult.Exception(this)
}
