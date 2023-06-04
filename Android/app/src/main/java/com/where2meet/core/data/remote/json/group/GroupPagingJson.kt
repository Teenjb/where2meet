package com.where2meet.core.data.remote.json.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupPagingJson(
    val totalPage: Int,
    val pageNumber: Int,
    val pageSize: Int,
    @SerialName("Groups") val groups: List<MiniGroupJson>
)
