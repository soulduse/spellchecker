package com.dave.spellchecker.network.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dave.spellchecker.network.exceptions.NoNetworkException
import com.dave.spellchecker.network.extensions.awaitSafe
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

abstract class NetworkBoundResource<TResourceData : Any, TNetworkDto : Any> :
    IBoundResource<TResourceData> {
    private val result = MutableLiveData<Resource<TResourceData>>()

    final override fun getLiveData(): LiveData<Resource<TResourceData>> = result

    fun launchIn(scope: CoroutineScope): IBoundResource<TResourceData> {
        scope.launch {
            result.value = tryGetResponse()
        }
        return this
    }

    override suspend fun getAsync(): Resource<TResourceData> {
        return tryGetResponse()
    }

    private suspend fun tryGetResponse(): Resource<TResourceData> {
        return try {
            fetchFromNetworkAsync()
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            Resource.Error.parse(e)
        } catch (e: IOException) {
            Timber.e(e)
            Resource.Error.parse(e)
        } catch (e: JsonDataException) {
            Timber.e(e)
            Resource.Error.parse(e)
        } catch (e: JsonEncodingException) {
            Timber.e(e)
            Resource.Error.parse(e)
        }
    }

    private suspend fun fetchFromNetworkAsync(): Resource<TResourceData> {
        result.value = Resource.Loading(true)
        val apiResponse = getApiCallAsync().awaitSafe()
        result.value = Resource.Loading(false)

        return when (apiResponse) {
            is HttpResult.Success -> {
                val data = mapToPOJO(apiResponse.data)
                if (data == null) {
                    Resource.Error(Cause.UNEXPECTED, "Error during fetching data from server.")
                } else {
                    Resource.Success(data)
                }
            }
            is HttpResult.Empty -> Resource.Error(
                Cause.UNEXPECTED,
                "Error during fetching data from server."
            )
            is HttpResult.Error -> Resource.Error.parse(apiResponse)
            is HttpResult.Exception -> {
                when (apiResponse.exception) {
                    is NoNetworkException -> Resource.Error(Cause.NO_INTERNET_CONNECTION)
                    is JsonEncodingException,
                    is JsonDataException -> Resource.Error(Cause.JSON_DATA_EXCEPTION)
                    else -> Resource.Error(Cause.UNEXPECTED, apiResponse.exception.localizedMessage ?: "")
                }
            }
        }
    }

    abstract suspend fun getApiCallAsync(): Response<TNetworkDto>

    abstract suspend fun mapToPOJO(data: TNetworkDto): TResourceData?
}
