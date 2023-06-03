package com.where2meet.core.data.remote.json.group.admin

import com.where2meet.core.data.remote.json.group.GroupJson
import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupResponse(
    val group: GroupJson
)
