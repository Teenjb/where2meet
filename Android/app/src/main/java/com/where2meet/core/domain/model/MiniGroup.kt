package com.where2meet.core.domain.model

data class MiniGroup(
    val id: Int,
    val name: String,
    val status: String,
    val members: List<String>
)
