package com.where2meet.core.data.remote.json.group

import com.where2meet.core.data.remote.json.recommendation.ResultJson
import kotlinx.serialization.Serializable

@Serializable
data class GroupJson(
    val id: Int,
    val status: String,
    val name: String,
    val adminId: Int,
    val result: List<ResultJson>? = null,
    val code: String,
    val createdAt: String,
    val updatedAt: String,
    val generatedAt: String? = null,
    val users: List<UserGroupJson> = emptyList(),
)
