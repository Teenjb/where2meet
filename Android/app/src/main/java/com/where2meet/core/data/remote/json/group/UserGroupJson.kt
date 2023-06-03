package com.where2meet.core.data.remote.json.group

import com.where2meet.core.data.remote.json.auth.UserJson
import com.where2meet.core.data.remote.json.group.MoodJson
import com.where2meet.core.data.remote.json.group.PositionJson
import kotlinx.serialization.Serializable

@Serializable
data class UserGroupJson(
    val user: UserJson,
    val moods: List<MoodJson>,
    val position: PositionJson,
)
