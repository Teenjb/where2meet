package com.where2meet.ui.adapter

import android.graphics.PorterDuff
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.where2meet.R
import com.where2meet.core.domain.model.UserGroup
import com.where2meet.databinding.ItemGroupMemberBinding
import com.where2meet.ui.adapter.UserGroupAdapter.UserGroupViewHolder
import com.where2meet.ui.ext.getColorFromAttr
import com.where2meet.ui.ext.viewBinding

class UserGroupAdapter(
    private val imageLoader: ImageLoader,
    private val userId: Int,
    private val adminId: Int,
    private val isAdmin: Boolean,
    private val onEdit: (UserGroup) -> Unit,
    private val onDelete: (UserGroup) -> Unit,
) : ListAdapter<UserGroup, UserGroupViewHolder>(USER_GROUP_COMPARATOR) {
    inner class UserGroupViewHolder(private val binding: ItemGroupMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserGroup?) {
            if (data == null) return
            val isOwner = userId == data.user.id
            val isAdminCard = adminId == data.user.id
            val ctx = binding.root.context
            with(binding) {
                tvName.text = if (isAdminCard) {
                    "👑 ${data.user.username}"
                } else {
                    data.user.username
                }
                tvMoods.text = ctx.getString(
                    R.string.lbl_current_mood,
                    data.moods.joinToString(", ") { it.name.replaceFirstChar { char -> char.uppercase() } },
                )
                ivAvatar.apply {
                    val imgData = ImageRequest.Builder(this.context)
                        .data("https://ui-avatars.com/api/?name=${data.user.username}&length=1")
                        .target(this).transformations(RoundedCornersTransformation(16f))
                        .allowHardware(true).build()
                    imageLoader.enqueue(imgData)
                }
                if (isAdmin) {
                    if (isOwner) {
                        cardCta.setOnClickListener { onEdit(data) }
                    } else {
                        cardCta.setCardBackgroundColor(
                            ctx.getColorFromAttr(com.google.android.material.R.attr.colorError),
                        )
                        ivCta.setColorFilter(
                            ctx.getColorFromAttr(com.google.android.material.R.attr.colorOnError),
                            PorterDuff.Mode.SRC_IN,
                        )
                        ivCta.setImageDrawable(
                            AppCompatResources.getDrawable(ctx, R.drawable.ic_delete),
                        )
                        cardCta.setOnClickListener { onDelete(data) }
                    }
                } else {
                    if (isOwner) {
                        cardCta.setOnClickListener { onEdit(data) }
                    } else {
                        cardCta.isVisible = false
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserGroupViewHolder =
        UserGroupViewHolder(parent.viewBinding(ItemGroupMemberBinding::inflate))

    override fun onBindViewHolder(holder: UserGroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
