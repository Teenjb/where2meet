package com.where2meet.core.domain.repository

import androidx.paging.PagingData
import com.where2meet.core.domain.model.Group
import com.where2meet.core.domain.model.MiniGroup
import com.where2meet.core.domain.model.Mood
import com.where2meet.core.domain.model.form.DeleteMember
import com.where2meet.core.domain.model.form.JoinGroup
import com.where2meet.core.domain.model.form.UpdateGroup
import com.where2meet.core.domain.model.form.UpdateLocation
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    //admin
    suspend fun createGroup(): Flow<Result<Group>>
    suspend fun updateGroup(form: UpdateGroup): Flow<Result<Group>>
    suspend fun deleteMember(form: DeleteMember): Flow<Result<String>>
    suspend fun deleteGroup(groupId: Int): Flow<Result<String>>
    suspend fun generateRecommendation(groupId: Int): Flow<Result<Group>>

    //member
    suspend fun joinGroup(form: JoinGroup): Flow<Result<Group>>
    suspend fun fetchGroups(size: Int): Flow<Result<List<MiniGroup>>>
    suspend fun fetchPagedGroups(): Flow<PagingData<MiniGroup>>
    suspend fun fetchGroup(groupId: Int): Flow<Result<Group>>
    suspend fun updateLocation(groupId: Int, form: UpdateLocation): Flow<Result<String>>
    suspend fun updateMoods(groupId: Int, form: List<Mood>): Flow<Result<String>>
    suspend fun fetchMoods(): Flow<Result<List<Mood>>>
}
