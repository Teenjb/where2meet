package com.where2meet.core.data.remote.json.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: String,
)
