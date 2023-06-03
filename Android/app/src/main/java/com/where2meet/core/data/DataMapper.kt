package com.where2meet.core.data

import com.where2meet.core.data.remote.json.auth.UserJson
import com.where2meet.core.data.remote.json.group.GroupJson
import com.where2meet.core.data.remote.json.group.MoodJson
import com.where2meet.core.data.remote.json.group.PositionJson
import com.where2meet.core.data.remote.json.group.UserGroupJson
import com.where2meet.core.domain.model.Group
import com.where2meet.core.domain.model.Mood
import com.where2meet.core.domain.model.Position
import com.where2meet.core.domain.model.User
import com.where2meet.core.domain.model.UserGroup

fun GroupJson.toModel() = Group(
    status = this.status,
    name = this.name,
    code = this.code,
    adminId = this.admin.id,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    generatedAt = this.generatedAt,
    users = this.users.map(UserGroupJson::toModel)
)

fun UserGroupJson.toModel() = UserGroup(
    user = this.user.toModel(),
    moods = this.moods.map(MoodJson::toModel),
    position = this.position.toModel(),
)

fun UserJson.toModel() =
    User(
        id = this.id,
        username = this.username
    )

fun MoodJson.toModel() =
    Mood(
        id = this.id,
        name = this.name,
        display = this.displayText
    )

fun PositionJson.toModel() =
    Position(
        lat = this.lat,
        lng = this.lng
    )
