package com.where2meet.core.data

import com.where2meet.core.data.remote.json.auth.UserJson
import com.where2meet.core.data.remote.json.group.GroupJson
import com.where2meet.core.data.remote.json.group.LocationJson
import com.where2meet.core.data.remote.json.group.MiniGroupJson
import com.where2meet.core.data.remote.json.group.MoodJson
import com.where2meet.core.data.remote.json.group.UserGroupJson
import com.where2meet.core.data.remote.json.recommendation.ResultJson
import com.where2meet.core.domain.model.Group
import com.where2meet.core.domain.model.GroupResult
import com.where2meet.core.domain.model.Location
import com.where2meet.core.domain.model.MiniGroup
import com.where2meet.core.domain.model.Mood
import com.where2meet.core.domain.model.User
import com.where2meet.core.domain.model.UserGroup
import com.where2meet.ui.parcelable.ParcelableGroup

// data -> domain
fun GroupJson.toModel() = Group(
    id = this.id,
    status = this.status,
    name = this.name,
    adminId = this.adminId,
    result = this.result?.map(ResultJson::toModel),
    code = this.code,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    generatedAt = this.generatedAt,
    users = this.users.map(UserGroupJson::toModel),
)

fun ResultJson.toModel() = GroupResult(
    rank = this.rank,
    location = this.locations.toModel(),
)

fun LocationJson.toModel() = Location(
    id = this.id,
    name = this.name,
    lat = this.lat,
    lng = this.lng,
)

fun UserGroupJson.toModel(): UserGroup {
    return UserGroup(
        user = this.user.toModel(),
        moods = this.moods.map(MoodJson::toModel),
        lat = this.lat,
        lng = this.lng,
    )
}

fun UserJson.toModel() =
    User(
        id = this.id,
        username = this.username,
    )

fun MoodJson.toModel() =
    Mood(
        id = this.id,
        name = this.name,
        display = this.displayText,
    )

fun MiniGroupJson.toModel() =
    MiniGroup(
        id = this.id,
        name = this.name,
        status = this.status,
        members = this.users.map { it.username },
    )

// domain -> ui

fun Group.toParcelable(currentUserId: Int): ParcelableGroup {
    val userData = this.users.find { it.user.id == currentUserId }

    return ParcelableGroup(
        id = this.id,
        name = this.name,
        members = this.users.map { it.user.username },
        isAdmin = this.adminId == currentUserId,
        hasLocation = (userData?.lat != null && userData.lng != null),
        hasMood = !userData?.moods.isNullOrEmpty(),
        hasResult = !this.result.isNullOrEmpty(),
    )
}
