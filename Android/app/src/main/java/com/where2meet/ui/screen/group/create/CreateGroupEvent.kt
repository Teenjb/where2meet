package com.where2meet.ui.screen.group.create

import com.where2meet.core.domain.model.Mood
import com.where2meet.ui.base.Event

sealed class CreateGroupEvent : Event() {
    object MoodsSubmitted : CreateGroupEvent()
    object LocationSubmitted: CreateGroupEvent()
}
