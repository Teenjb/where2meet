package com.where2meet.di

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UiModule {
    @Singleton
    @Provides
    fun provideCoilLoader(
        @ApplicationContext context: Context,
        client: OkHttpClient
    ) = ImageLoader.Builder(context)
        .okHttpClient(client)
        .crossfade(true)
        .build()
}
