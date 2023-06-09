package com.where2meet.ui.parcelable

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableGroup(
    val id: Int,
    val name: String,
    val members: List<String>,
    val isAdmin: Boolean,
    val hasMood: Boolean,
    val hasLocation: Boolean,
    val hasResult: Boolean,
) : Parcelable
