package com.where2meet.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.where2meet.core.domain.model.GroupResult
import com.where2meet.core.domain.model.MiniGroup
import com.where2meet.core.domain.model.UserGroup

val MINI_GROUP_COMPARATOR = object : DiffUtil.ItemCallback<MiniGroup>() {
    override fun areContentsTheSame(
        oldItem: MiniGroup,
        newItem: MiniGroup,
    ): Boolean = oldItem.status == newItem.status

    override fun areItemsTheSame(
        oldItem: MiniGroup,
        newItem: MiniGroup,
    ): Boolean = oldItem.id == newItem.id
}

val USER_GROUP_COMPARATOR = object : DiffUtil.ItemCallback<UserGroup>() {
    override fun areContentsTheSame(
        oldItem: UserGroup,
        newItem: UserGroup,
    ): Boolean = oldItem.user.id == newItem.user.id

    override fun areItemsTheSame(
        oldItem: UserGroup,
        newItem: UserGroup,
    ): Boolean = oldItem.user.id == newItem.user.id
}

val GROUP_RESULT_COMPARATOR = object : DiffUtil.ItemCallback<GroupResult>() {
    override fun areContentsTheSame(
        oldItem: GroupResult,
        newItem: GroupResult,
    ): Boolean = oldItem.rank == newItem.rank

    override fun areItemsTheSame(
        oldItem: GroupResult,
        newItem: GroupResult,
    ): Boolean = oldItem.rank == newItem.rank
}
