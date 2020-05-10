package com.assignment.ui.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.circuitloop.easyscan.BR
import com.circuitloop.easyscan.utils.ApplicationHandler


class BaseViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.getRoot()) {
    fun bind(
        obj: Any,
        pos: Int,
        list: Any,
        handler: ApplicationHandler?
    ) {
        binding.setVariable(BR.obj, obj)
        binding.setVariable(BR.pos, pos)
        binding.setVariable(BR.list,list)
        binding.setVariable(BR.handler,handler)
        binding.executePendingBindings()
    }
}