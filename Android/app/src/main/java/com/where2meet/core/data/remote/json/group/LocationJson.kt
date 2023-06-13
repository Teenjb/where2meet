package com.where2meet.core.data.remote.json.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationJson(
    val id: Int,
    val name: String,
    val imgLink: String? = null,
    val lat: Double,
    @SerialName("long") val lng: Double,
)
