package com.where2meet.ui.screen.group

import com.where2meet.ui.base.Event

sealed class GroupEvent : Event() {
    object GroupUpdated : GroupEvent()
    object GroupDeleted : GroupEvent()
    object MemberRemoved : GroupEvent()
    data class RecommendationGenerated(val groupId: Int) : GroupEvent()
}
