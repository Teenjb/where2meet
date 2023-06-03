package com.where2meet.core.data.remote.json.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PositionJson(
    val lat: Double,
    @SerialName("lang") val lng: Double,
)
