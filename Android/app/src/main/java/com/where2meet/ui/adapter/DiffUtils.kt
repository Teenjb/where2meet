package com.where2meet.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.where2meet.core.domain.model.MiniGroup

val MINI_GROUP_COMPARATOR = object : DiffUtil.ItemCallback<MiniGroup>() {
    override fun areContentsTheSame(
        oldItem: MiniGroup,
        newItem: MiniGroup
    ): Boolean = oldItem.id == newItem.id

    override fun areItemsTheSame(
        oldItem: MiniGroup,
        newItem: MiniGroup
    ): Boolean = oldItem.id == newItem.id
}
