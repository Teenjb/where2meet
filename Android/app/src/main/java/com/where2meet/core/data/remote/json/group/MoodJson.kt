package com.where2meet.core.data.remote.json.group

import kotlinx.serialization.Serializable

@Serializable
data class MoodJson(
    val id: Int,
    val name: String,
    val displayText: String
)
