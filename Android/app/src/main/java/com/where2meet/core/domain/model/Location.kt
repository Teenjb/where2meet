package com.where2meet.core.domain.model

data class Location(
    val id: Int,
    val name: String,
    val imageLink: String? = null,
    val lat: Double,
    val lng: Double,
)
