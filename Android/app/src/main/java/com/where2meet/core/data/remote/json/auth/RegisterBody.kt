package com.where2meet.core.data.remote.json.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterBody(
    val email: String,
    val username: String,
    val password: String,
)
