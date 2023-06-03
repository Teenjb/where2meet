package com.where2meet.core.data.interactor

import androidx.paging.PagingData
import com.where2meet.core.data.preference.DataStoreManager
import com.where2meet.core.data.provideDummyMoods
import com.where2meet.core.data.remote.SafeApiRequest
import com.where2meet.core.data.remote.api.ApiService
import com.where2meet.core.data.remote.json.group.admin.UpdateGroupBody
import com.where2meet.core.data.remote.wrapFlowApiCall
import com.where2meet.core.data.toModel
import com.where2meet.core.domain.model.Group
import com.where2meet.core.domain.model.Mood
import com.where2meet.core.domain.model.Position
import com.where2meet.core.domain.model.form.DeleteMember
import com.where2meet.core.domain.model.form.JoinGroup
import com.where2meet.core.domain.model.form.UpdateGroup
import com.where2meet.core.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val preference: DataStoreManager
) : GroupRepository, SafeApiRequest() {
    override suspend fun createGroup(): Flow<Result<Group>> = wrapFlowApiCall {
        val (token) = preference.session.last()
        val response = apiRequest { api.createGroup(token) }
        val (group) = response.data ?: throw IllegalStateException("Data is empty")

        Result.success(group.toModel())
    }

    override suspend fun updateGroup(data: UpdateGroup): Flow<Result<Group>> = wrapFlowApiCall {
        val (token) = preference.session.last()
        val body = UpdateGroupBody(data.groupId, data.name)
        val response = apiRequest { api.updateGroup(token, body) }
        val group = response.data ?: throw IllegalStateException("Data is empty")

        Result.success(group.toModel())
    }

    override suspend fun deleteMember(data: DeleteMember): Flow<Result<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroup(groupId: Int): Flow<Result<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun generateRecommendation(groupId: Int): Flow<Result<Group>> {
        TODO("Not yet implemented")
    }

    override suspend fun joinGroup(data: JoinGroup): Flow<Result<Group>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchGroups(size: Int): Flow<Result<List<Group>>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchPagedGroups(): Flow<PagingData<Group>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchGroup(groupId: Int): Flow<Result<Group>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLocation(groupId: Int, data: Position): Flow<Result<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMoods(groupId: Int, data: List<Mood>): Flow<Result<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMoods(): Flow<Result<List<Mood>>> = flow {
        emit(Result.success(provideDummyMoods()))
    }
}
