package com.where2meet.core.data.remote.json.group.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateLocationBody(
    val lat: Double,
    @SerialName("lang") val lng: Double,
)
