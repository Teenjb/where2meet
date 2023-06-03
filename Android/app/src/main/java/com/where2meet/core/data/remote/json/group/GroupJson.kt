package com.where2meet.core.data.remote.json.group

import com.where2meet.core.data.remote.json.recommendation.ResultJson
import com.where2meet.core.data.remote.json.auth.UserJson
import kotlinx.serialization.Serializable

@Serializable
data class GroupJson(
    val status: String,
    val id: Int,
    val name: String,
    val code: String,
    val admin: UserJson,
    val updatedAt: String,
    val createdAt: String,
    val generatedAt: String = "",
    val users: List<UserGroupJson> = emptyList(),
    val result: List<ResultJson>? = emptyList()
)
