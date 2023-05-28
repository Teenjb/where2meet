package com.where2meet.core.data.remote.api

import com.where2meet.core.data.remote.json.ApiResponse
import com.where2meet.core.data.remote.json.StatusResponse
import com.where2meet.core.data.remote.json.auth.LoginResponse
import com.where2meet.core.data.remote.json.auth.RegisterBody
import com.where2meet.core.data.remote.json.auth.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    suspend fun register(
        @Body body: RegisterBody,
    ): Response<ApiResponse<StatusResponse>>

    @GET("login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String,
    ): Response<ApiResponse<LoginResponse>>

    @GET("details")
    suspend fun fetchUserDetail(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<UserResponse>>
}
