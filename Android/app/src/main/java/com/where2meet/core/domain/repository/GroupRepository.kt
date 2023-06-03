package com.where2meet.core.domain.repository

import androidx.paging.PagingData
import com.where2meet.core.domain.model.Group
import com.where2meet.core.domain.model.Mood
import com.where2meet.core.domain.model.Position
import com.where2meet.core.domain.model.form.DeleteMember
import com.where2meet.core.domain.model.form.JoinGroup
import com.where2meet.core.domain.model.form.UpdateGroup
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    //admin
    suspend fun createGroup(): Flow<Result<Group>>
    suspend fun updateGroup(data: UpdateGroup): Flow<Result<Group>>
    suspend fun deleteMember(data: DeleteMember): Flow<Result<String>>
    suspend fun deleteGroup(groupId: Int): Flow<Result<String>>
    suspend fun generateRecommendation(groupId: Int): Flow<Result<Group>>

    //member
    suspend fun joinGroup(data: JoinGroup): Flow<Result<Group>>
    suspend fun fetchGroups(size: Int): Flow<Result<List<Group>>>
    suspend fun fetchPagedGroups(): Flow<PagingData<Group>>
    suspend fun fetchGroup(groupId: Int): Flow<Result<Group>>
    suspend fun updateLocation(groupId: Int, data: Position): Flow<Result<String>>
    suspend fun updateMoods(groupId: Int, data: List<Mood>): Flow<Result<String>>

    suspend fun fetchMoods(): Flow<Result<List<Mood>>>
}
