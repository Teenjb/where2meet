package com.where2meet.core.data.remote.json.group

import com.where2meet.core.data.remote.json.auth.UserJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserGroupJson(
    @SerialName("User") val user: UserJson,
    val moods: List<MoodJson>,
    val lat: Double? = null,
    @SerialName("long") val lng: Double? = null,
)
