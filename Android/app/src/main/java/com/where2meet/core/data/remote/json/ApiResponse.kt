package com.where2meet.core.data.remote.json

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val message: String,
    val data: T? = null,
)
