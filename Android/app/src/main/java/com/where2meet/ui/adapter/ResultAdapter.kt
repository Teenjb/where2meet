package com.where2meet.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.where2meet.core.domain.model.GroupResult
import com.where2meet.databinding.ItemResultBinding
import com.where2meet.ui.adapter.ResultAdapter.GroupResultViewHolder
import com.where2meet.ui.ext.getOrdinal
import com.where2meet.ui.ext.viewBinding

class ResultAdapter(
    private val imageLoader: ImageLoader,
    private val onClick: (GroupResult) -> Unit,
) : ListAdapter<GroupResult, GroupResultViewHolder>(GROUP_RESULT_COMPARATOR) {
    inner class GroupResultViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GroupResult?) {
            if (data == null) return
            with(binding) {
                val ctx = root.context
                root.setOnClickListener { onClick(data) }
                tvName.text = data.location.name
                tvRank.text = data.rank.getOrdinal()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupResultViewHolder =
        GroupResultViewHolder(parent.viewBinding(ItemResultBinding::inflate))

    override fun onBindViewHolder(holder: GroupResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
