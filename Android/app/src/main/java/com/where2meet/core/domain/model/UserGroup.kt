package com.where2meet.core.domain.model

data class UserGroup(
    val user: User,
    val moods: List<Mood>,
    val position: Position,
)
