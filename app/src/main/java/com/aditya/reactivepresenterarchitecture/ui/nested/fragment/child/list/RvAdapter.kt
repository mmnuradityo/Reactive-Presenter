package com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aditya.reactivepresenterarchitecture.databinding.ItemVhBinding
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.ListValueItem

class ValuesDiffCallback : DiffUtil.ItemCallback<ListValueItem>() {
    override fun areItemsTheSame(oldItem: ListValueItem, newItem: ListValueItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListValueItem, newItem: ListValueItem): Boolean {
        return oldItem == newItem
    }
}

class RvAdapter: ListAdapter<ListValueItem, RvAdapter.ViewHolder>(ValuesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVhBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            getItem(position)
        )
    }

    class ViewHolder(private val binding: ItemVhBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(value: ListValueItem) {
            binding.tvItem.text = "ID: ${value.id}, Text: ${value.text}"
        }

    }
}