package com.where2meet.core.data.remote.json.group

import kotlinx.serialization.Serializable

@Serializable
data class GroupPagingJson(
    val pageCount: Int,
    val groups: List<GroupJson>
)
