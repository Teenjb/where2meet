package com.where2meet.core.domain.model

data class Group(
    val status: String,
    val name: String,
    val code: String,
    val adminId: Int,
    val users: List<UserGroup>,
    val createdAt: String,
    val updatedAt: String,
    val generatedAt: String? = null,
)
