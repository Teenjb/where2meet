package com.where2meet.core.data.remote.json.group.admin

import kotlinx.serialization.Serializable

@Serializable
data class UpdateGroupBody(
    val groupId: Int,
    val name: String,
)
