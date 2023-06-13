package com.where2meet.core.data.remote.json.group

import com.where2meet.core.data.remote.json.auth.UserJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MiniGroupJson(
    val id: Int,
    val name: String,
    val status: String,
    @SerialName("Users") val users: List<UserJson>,
)
