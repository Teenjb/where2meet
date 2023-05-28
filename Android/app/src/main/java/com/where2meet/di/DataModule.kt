package com.where2meet.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.where2meet.BuildConfig
import com.where2meet.core.data.preference.DataStoreManager
import com.where2meet.core.data.remote.NoConnectionInterceptor
import com.where2meet.core.data.remote.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import logcat.logcat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    companion object {
        const val DEFAULT_TIMEOUT = 5L
        var BASE_URL = "https://where2meet-backend-wtlln4sbra-et.a.run.app/w2m/"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Singleton
    @Provides
    fun provideNoConnectionInterceptor(
        @ApplicationContext appContext: Context,
    ): NoConnectionInterceptor =
        NoConnectionInterceptor(appContext)

    @Singleton
    @Provides
    fun provideHttpClient(netConn: NoConnectionInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES)

        // logging
        if (BuildConfig.DEBUG) {
            logcat { "Applying HTTP Logging" }
            val logger = HttpLoggingInterceptor { message ->
                logcat("API") { message }
            }.apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            builder.addInterceptor(logger)
        }

        // detect internet connection
        builder.addInterceptor(netConn)
        return builder
            .build()
    }

    @ExperimentalSerializationApi
    @Singleton
    @Provides
    fun provideJsonConverterFactory(): Converter.Factory {
        return json.asConverterFactory("application/json".toMediaType())
    }

    @Singleton
    @Provides
    fun provideApiService(client: OkHttpClient, factory: Converter.Factory): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(factory)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppPreferences(@ApplicationContext appContext: Context): DataStoreManager =
        DataStoreManager(appContext)
}
