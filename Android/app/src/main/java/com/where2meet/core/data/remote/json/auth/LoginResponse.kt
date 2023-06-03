package com.where2meet.core.data.remote.json.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserJson,
)
