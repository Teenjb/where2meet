package com.where2meet.core.data.interactor

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.where2meet.core.data.paging.GroupPagingSource
import com.where2meet.core.data.preference.DataStoreManager
import com.where2meet.core.data.remote.SafeApiRequest
import com.where2meet.core.data.remote.api.ApiService
import com.where2meet.core.data.remote.json.group.MiniGroupJson
import com.where2meet.core.data.remote.json.group.MoodJson
import com.where2meet.core.data.remote.json.group.admin.UpdateGroupBody
import com.where2meet.core.data.remote.json.group.member.JoinGroupBody
import com.where2meet.core.data.remote.json.group.member.UpdateLocationBody
import com.where2meet.core.data.remote.json.group.member.UpdateMoodsBody
import com.where2meet.core.data.remote.wrapFlowApiCall
import com.where2meet.core.data.toModel
import com.where2meet.core.domain.model.Group
import com.where2meet.core.domain.model.MiniGroup
import com.where2meet.core.domain.model.Mood
import com.where2meet.core.domain.model.form.DeleteMember
import com.where2meet.core.domain.model.form.JoinGroup
import com.where2meet.core.domain.model.form.UpdateGroup
import com.where2meet.core.domain.model.form.UpdateLocation
import com.where2meet.core.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import logcat.logcat
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val preference: DataStoreManager
) : GroupRepository, SafeApiRequest() {
    override suspend fun createGroup(): Flow<Result<Group>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val response = apiRequest { api.createGroup(token) }
                val group = response.data ?: throw IllegalStateException("Data is empty")
                Result.success(group.toModel())
            }
        }

    override suspend fun updateGroup(form: UpdateGroup): Flow<Result<Group>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val body = UpdateGroupBody(form.name)
                val response = apiRequest { api.updateGroup(token, form.groupId, body) }
                val group = response.data ?: throw IllegalStateException("Data is empty")
                Result.success(group.toModel())
            }
        }

    override suspend fun deleteMember(form: DeleteMember): Flow<Result<String>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val (groupId, userId) = form
                val response = apiRequest { api.deleteMember(token, groupId, userId) }
                Result.success(response.message)
            }
        }

    override suspend fun deleteGroup(groupId: Int): Flow<Result<String>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val response = apiRequest { api.deleteGroup(token, groupId) }
                Result.success(response.message)
            }
        }

    override suspend fun generateRecommendation(groupId: Int): Flow<Result<Group>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val response = apiRequest { api.generateRecommendation(token, groupId) }
                val group = response.data ?: throw IllegalStateException("Data is empty")
                Result.success(group.toModel())
            }
        }

    override suspend fun joinGroup(form: JoinGroup): Flow<Result<Group>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val body = JoinGroupBody(form.code)
                val response = apiRequest { api.joinGroup(token, body) }
                val group = response.data ?: throw IllegalStateException("Data is empty")
                Result.success(group.toModel())
            }
        }

    override suspend fun fetchGroups(size: Int): Flow<Result<List<MiniGroup>>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val response = apiRequest { api.fetchGroups(token, size = size) }
                val pagingData = response.data ?: throw IllegalStateException("Data is empty")
                Result.success(pagingData.groups.map(MiniGroupJson::toModel))
            }
        }

    override suspend fun fetchPagedGroups(): Flow<PagingData<MiniGroup>> =
        preference.token.flatMapLatest { token ->
            Pager(
                PagingConfig(pageSize = 10, initialLoadSize = 30, enablePlaceholders = true)
            ) {
                GroupPagingSource(api, token)
            }.flow
        }

    override suspend fun fetchGroup(groupId: Int): Flow<Result<Group>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val response = apiRequest { api.fetchGroupById(token, groupId) }
                val group = response.data ?: throw IllegalStateException("Data is empty")
                Result.success(group.toModel())
            }
        }

    override suspend fun updateLocation(groupId: Int, form: UpdateLocation): Flow<Result<String>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val body = UpdateLocationBody(form.lat, form.lng)
                val response = apiRequest { api.updateLocationForGroup(token, groupId, body) }
                Result.success(response.message)
            }
        }

    override suspend fun updateMoods(groupId: Int, form: List<Mood>): Flow<Result<String>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val body = UpdateMoodsBody(form.map { it.id }.toString())
                logcat { "updateMoods($groupId, $body)" }
                val response = apiRequest { api.updateMoodsForGroup(token, groupId, body) }
                Result.success(response.message)
            }
        }

    override suspend fun fetchMoods(): Flow<Result<List<Mood>>> =
        preference.token.flatMapLatest { token ->
            wrapFlowApiCall {
                val response = apiRequest { api.fetchMoods(token) }
                val data = response.data ?: throw IllegalStateException("Data is empty")
                Result.success(data.map(MoodJson::toModel))
            }
        }
}
