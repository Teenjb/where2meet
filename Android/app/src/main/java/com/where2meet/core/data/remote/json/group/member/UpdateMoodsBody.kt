package com.where2meet.core.data.remote.json.group.member

import com.where2meet.core.data.remote.json.group.MoodJson
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMoodsBody(
    val moods: String
)
