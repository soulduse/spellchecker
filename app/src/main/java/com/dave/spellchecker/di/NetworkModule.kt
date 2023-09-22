package com.dave.spellchecker.di

import android.content.Context
import com.dave.spellchecker.BuildConfig
import com.dave.spellchecker.network.interceptors.ConnectivityInterceptor
import com.dave.spellchecker.util.LocalDateTimeAdapter
import com.dave.spellchecker.util.Memory
import com.dave.spellchecker.util.notExist
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        connectivityInterceptor: ConnectivityInterceptor,
        httpCache: Cache,
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10L , TimeUnit.SECONDS)
        .writeTimeout(10L , TimeUnit.SECONDS)
        .readTimeout(10L , TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(connectivityInterceptor)
        .cache(httpCache)
        .build()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideConnectivityInterceptor(@ApplicationContext context: Context): ConnectivityInterceptor = ConnectivityInterceptor(context)

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context, file: File): Cache = Cache(file, Memory.calculateCacheSize(context, .15f))

    @Provides
    @Singleton
    fun provideCacheFile(@ApplicationContext context: Context): File = File(context.filesDir, "okHttpCache").also {
        if (it.notExist()) {
            it.mkdirs()
        }
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .add(LocalDateTime::class.java, LocalDateTimeAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        httpClient: OkHttpClient,
        moshi: Moshi,
        @BaseUrl baseUrl: String,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String = "https://oror.link/"
}
