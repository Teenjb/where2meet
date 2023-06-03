package com.where2meet.core.domain.model

data class Session(
    val token: String,
    val userId: Int,
    val username: String,
)
