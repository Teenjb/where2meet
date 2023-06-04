package com.where2meet.core.data.remote.json.group

import com.where2meet.core.data.remote.json.auth.UserJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserGroupJson(
    val user: UserJson,
    val moods: List<MoodJson>,
    @SerialName("lang") val lng: Double? = null,
    val lat: Double? = null,
)
