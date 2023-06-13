package com.where2meet.core.domain.model

data class UserGroup(
    val user: User,
    val moods: List<Mood>,
    val lat: Double? = null,
    val lng: Double? = null,
)
