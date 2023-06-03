package com.where2meet.core.data.interactor

import com.where2meet.core.data.preference.DataStoreManager
import com.where2meet.core.data.remote.SafeApiRequest
import com.where2meet.core.data.remote.api.ApiService
import com.where2meet.core.data.remote.json.auth.LoginBody
import com.where2meet.core.data.remote.json.auth.RegisterBody
import com.where2meet.core.data.remote.wrapFlowApiCall
import com.where2meet.core.domain.model.Session
import com.where2meet.core.domain.model.form.AuthLogin
import com.where2meet.core.domain.model.form.AuthRegister
import com.where2meet.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val preference: DataStoreManager,
) : AuthRepository, SafeApiRequest() {
    override suspend fun login(data: AuthLogin): Flow<Result<String>> = wrapFlowApiCall {
        val body = LoginBody(data.username, data.password)
        val response = apiRequest {
            api.login(body)
        }
        val (token, user) = response.data ?: throw IllegalStateException("Data is empty")
        // create new session
        val newSession = Session(
            token,
            user.id,
            user.username,
        )
        preference.addSession(newSession)
        Result.success(response.message)
    }

    override suspend fun logout() {
        preference.clearSession()
    }

    override suspend fun register(data: AuthRegister): Flow<Result<String>> = wrapFlowApiCall {
        val body = RegisterBody(
            email = data.email,
            username = data.username,
            password = data.password,
        )
        val register = apiRequest {
            api.register(body)
        }
        Result.success(register.message)
    }

    override fun isLoggedIn(): Flow<Boolean> = preference.isLoggedIn
    override fun session(): Flow<Session> = preference.session
}
