package com.where2meet.core.data.interactor

import com.where2meet.core.data.remote.api.ApiService
import com.where2meet.core.domain.repository.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : GroupRepository
