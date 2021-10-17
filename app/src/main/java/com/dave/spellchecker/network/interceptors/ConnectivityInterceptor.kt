package com.dave.spellchecker.network.interceptors

import android.content.Context
import com.dave.spellchecker.network.common.Connectivity
import com.dave.spellchecker.network.exceptions.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class ConnectivityInterceptor @Inject constructor(private val context: Context) : Interceptor {

    private val isConnected: Boolean get() = Connectivity.isInternetAvailable(context)

    @Throws(NoNetworkException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return if (isConnected) {
            chain.proceed(originalRequest)
        } else {
            throw NoNetworkException("No internet connection.")
        }
    }
}
