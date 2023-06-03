package com.where2meet.core.data.remote.json.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserJson(
    val id: Int,
    val username: String,
    val email: String = "",
)
