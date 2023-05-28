package com.where2meet.core.domain.repository

import com.where2meet.core.domain.model.AuthLogin
import com.where2meet.core.domain.model.AuthRegister
import com.where2meet.core.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // api functions
    suspend fun login(data: AuthLogin): Flow<Result<String>>
    suspend fun logout()
    suspend fun register(data: AuthRegister): Flow<Result<String>>

    // local functions
    fun isLoggedIn(): Flow<Boolean>
    fun session(): Flow<Session>
}
