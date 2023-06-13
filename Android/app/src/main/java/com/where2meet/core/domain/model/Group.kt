package com.where2meet.core.domain.model

data class Group(
    val id: Int,
    val status: String,
    val name: String,
    val adminId: Int,
    val result: List<GroupResult>? = null,
    val code: String,
    val createdAt: String,
    val updatedAt: String,
    val generatedAt: String? = null,
    val users: List<UserGroup>,
)
