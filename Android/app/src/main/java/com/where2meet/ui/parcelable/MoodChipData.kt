package com.where2meet.ui.parcelable

import com.where2meet.core.domain.model.Mood

data class MoodChipData(
    val mood: Mood,
    var isSelected: Boolean
)

fun Mood.toChipData() = MoodChipData(mood = this, isSelected = false)
