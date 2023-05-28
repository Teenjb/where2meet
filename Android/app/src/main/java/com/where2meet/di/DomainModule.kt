package com.where2meet.di

import com.where2meet.core.data.interactor.AuthRepositoryImpl
import com.where2meet.core.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface DomainModule {
    @Binds
    fun authRepository(repo: AuthRepositoryImpl): AuthRepository
}
