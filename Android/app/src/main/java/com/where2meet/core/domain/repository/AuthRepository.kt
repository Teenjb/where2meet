package com.where2meet.core.domain.repository

import com.where2meet.core.domain.model.Session
import com.where2meet.core.domain.model.form.AuthLogin
import com.where2meet.core.domain.model.form.AuthRegister
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
