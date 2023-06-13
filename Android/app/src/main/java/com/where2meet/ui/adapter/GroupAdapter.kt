package com.where2meet.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.where2meet.R
import com.where2meet.core.domain.model.MiniGroup
import com.where2meet.databinding.ItemGroupHangoutBinding
import com.where2meet.ui.adapter.GroupAdapter.GroupViewHolder
import com.where2meet.ui.ext.getColorFromAttr
import com.where2meet.ui.ext.viewBinding

class GroupAdapter(
    private val onClick: (MiniGroup) -> Unit,
) : ListAdapter<MiniGroup, GroupViewHolder>(MINI_GROUP_COMPARATOR) {
    inner class GroupViewHolder(private val binding: ItemGroupHangoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MiniGroup?) {
            if (data == null) return
            val ctx = binding.root.context
            with(binding) {
                root.setOnClickListener { onClick(data) }
                tvName.text = data.name
                tvMembers.text = data.members.joinToString(", ")
                if (data.status == "Done") {
                    tvStatus.text = ctx.getString(R.string.lbl_done)
                    tvStatus.setTextColor(ctx.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
                    statusCard.setCardBackgroundColor(
                        ctx.getColorFromAttr(com.google.android.material.R.attr.colorPrimary),
                    )
                } else {
                    tvStatus.text = ctx.getString(R.string.lbl_pending)
                    tvStatus.setTextColor(
                        ctx.getColorFromAttr(com.google.android.material.R.attr.colorOnTertiaryContainer),
                    )
                    statusCard.setCardBackgroundColor(
                        ctx.getColorFromAttr(com.google.android.material.R.attr.colorTertiaryContainer),
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder =
        GroupViewHolder(parent.viewBinding(ItemGroupHangoutBinding::inflate))

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
