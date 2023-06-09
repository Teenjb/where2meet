package com.where2meet.core.data.remote.api

import com.where2meet.core.data.remote.json.ApiResponse
import com.where2meet.core.data.remote.json.StatusResponse
import com.where2meet.core.data.remote.json.auth.LoginBody
import com.where2meet.core.data.remote.json.auth.LoginResponse
import com.where2meet.core.data.remote.json.auth.RegisterBody
import com.where2meet.core.data.remote.json.auth.UserJson
import com.where2meet.core.data.remote.json.group.GroupJson
import com.where2meet.core.data.remote.json.group.GroupPagingJson
import com.where2meet.core.data.remote.json.group.MoodJson
import com.where2meet.core.data.remote.json.group.admin.UpdateGroupBody
import com.where2meet.core.data.remote.json.group.member.JoinGroupBody
import com.where2meet.core.data.remote.json.group.member.UpdateLocationBody
import com.where2meet.core.data.remote.json.group.member.UpdateMoodsBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // auth
    @POST("register")
    suspend fun register(
        @Body body: RegisterBody,
    ): Response<ApiResponse<StatusResponse>>

    @POST("login")
    suspend fun login(
        @Body body: LoginBody,
    ): Response<ApiResponse<LoginResponse>>

    @GET("me")
    suspend fun fetchUserDetail(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<UserJson>>

    // group administration
    @POST("groups")
    suspend fun createGroup(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<GroupJson>>

    @PUT("groups/{groupId}")
    suspend fun updateGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
        @Body body: UpdateGroupBody,
    ): Response<ApiResponse<GroupJson>>

    @DELETE("groups/{groupId}")
    suspend fun deleteGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
    ): Response<ApiResponse<StatusResponse>>

    @DELETE("groups/{groupId}/members/{userId}")
    suspend fun deleteMember(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
        @Path("userId") userId: Int,
    ): Response<ApiResponse<StatusResponse>>

    @POST("groups/{groupId}/recommend")
    suspend fun generateRecommendation(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
    ): Response<ApiResponse<GroupJson>>

    // group activity as member
    @POST("groups/join")
    suspend fun joinGroup(
        @Header("Authorization") token: String,
        @Body body: JoinGroupBody,
    ): Response<ApiResponse<GroupJson>>

    @GET("groups")
    suspend fun fetchGroups(
        @Header("Authorization") token: String,
        @Query("pageNumber") page: Int = 1,
        @Query("pageSize") size: Int = 10,
        @Query("search") q: String = "",
    ): Response<ApiResponse<GroupPagingJson>>

    @GET("groups/{groupId}")
    suspend fun fetchGroupById(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
    ): Response<ApiResponse<GroupJson>>

    @PUT("groups/{groupId}/location")
    suspend fun updateLocationForGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
        @Body body: UpdateLocationBody,
    ): Response<ApiResponse<StatusResponse>>

    @PUT("groups/{groupId}/mood")
    suspend fun updateMoodsForGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
        @Body body: UpdateMoodsBody,
    ): Response<ApiResponse<StatusResponse>>

    @GET("moods")
    suspend fun fetchMoods(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<List<MoodJson>>>
}
