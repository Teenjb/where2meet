package com.where2meet.core.data.remote.json.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationJson(
    val id: Int,
    val name: String,
    @SerialName("image_link") val imgUrl: String? = null,
    val position: PositionJson,
)
