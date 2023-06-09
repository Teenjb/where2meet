package com.where2meet.core.data.remote.json.group.member

import kotlinx.serialization.Serializable

@Serializable
data class UpdateMoodsBody(
    val moods: String,
)
