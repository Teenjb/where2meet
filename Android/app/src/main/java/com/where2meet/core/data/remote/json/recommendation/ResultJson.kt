package com.where2meet.core.data.remote.json.recommendation

import com.where2meet.core.data.remote.json.group.LocationJson
import kotlinx.serialization.Serializable

@Serializable
data class ResultJson(
    val rank: Int,
    val location: LocationJson
)
