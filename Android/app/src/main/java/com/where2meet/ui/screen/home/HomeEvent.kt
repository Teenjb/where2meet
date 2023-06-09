package com.where2meet.ui.screen.home

import com.where2meet.ui.base.Event
import com.where2meet.ui.parcelable.ParcelableGroup

sealed class HomeEvent : Event() {
    data class NavigateToDetail(val group: ParcelableGroup) : HomeEvent()
    data class GroupCreated(val groupId: Int) : HomeEvent()
    data class GroupJoined(val groupId: Int) : HomeEvent()
}
