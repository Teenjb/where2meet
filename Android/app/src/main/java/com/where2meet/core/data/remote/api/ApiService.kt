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
import com.where2meet.core.data.remote.json.group.PositionJson
import com.where2meet.core.data.remote.json.group.admin.CreateGroupResponse
import com.where2meet.core.data.remote.json.group.admin.DeleteGroupBody
import com.where2meet.core.data.remote.json.group.admin.DeleteMemberBody
import com.where2meet.core.data.remote.json.group.admin.UpdateGroupBody
import com.where2meet.core.data.remote.json.group.member.JoinGroupBody
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
    @POST("createGroup")
    suspend fun createGroup(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<CreateGroupResponse>>

    @PUT("updateGroup")
    suspend fun updateGroup(
        @Header("Authorization") token: String,
        @Body body: UpdateGroupBody,
    ): Response<ApiResponse<GroupJson>>

    @DELETE("deleteMember")
    suspend fun deleteMember(
        @Header("Authorization") token: String,
        @Body body: DeleteMemberBody,
    ): Response<ApiResponse<StatusResponse>>

    @DELETE("deleteGroup")
    suspend fun deleteGroup(
        @Header("Authorization") token: String,
        @Body body: DeleteGroupBody,
    ): Response<ApiResponse<StatusResponse>>

    @POST("groups/recommendation/{groupId}")
    suspend fun generateRecommendation(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
    ): Response<ApiResponse<GroupJson>>

    // group activity as member
    @POST("joinGroup")
    suspend fun joinGroup(
        @Header("Authorization") token: String,
        @Body body: JoinGroupBody,
    ): Response<ApiResponse<CreateGroupResponse>>

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

    @PUT("userGroup/location/{groupId}")
    suspend fun updateLocationForGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
        @Body body: PositionJson,
    ): Response<ApiResponse<StatusResponse>>

    @PUT("userGroup/mood/{groupId}")
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
