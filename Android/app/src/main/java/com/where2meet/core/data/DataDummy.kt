package com.where2meet.core.data

import com.where2meet.core.domain.model.Mood

fun provideDummyMoods() = listOf(
    Mood(id = 1, name = "fancy", display = "🤵 Fancy"),
    Mood(id = 2, name = "gloomy", display = "☁️ Gloomy"),
    Mood(id = 3, name = "cozy", display = "🍷 Cozy"),
    Mood(id = 4, name = "lively", display = "🎵 Lively"),
    Mood(id = 5, name = "melancholy", display = "😢 Melancholy"),
    Mood(id = 6, name = "playful", display = "😆 Playful"),
)
