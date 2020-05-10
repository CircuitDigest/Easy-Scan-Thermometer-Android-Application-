package com.assignment.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.circuitloop.easyscan.utils.ApplicationHandler
import java.util.*

class BaseRecyclerAdapter : RecyclerView.Adapter<BaseViewHolder> {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater, viewType, parent, false
        )
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return layoutResource
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * adding data for the first time in the list and scroll if required to last position
     *
     * @param li
     */
    open fun updateList(li: List<Any>?) {
        if (li != null) {
            this.list.clear()
            this.list = ArrayList(li)
            notifyDataSetChanged()
        }
    }

    open fun updateItem(item: Any?, position: Int) {
        if (item != null && position >= 0) {
            this.list[position] = item
            notifyItemChanged(position)
        }
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val obj = list[position]
        holder.bind(obj, position,list,handler)
    }


    private var layoutResource = 0
    var list: MutableList<Any> = ArrayList()
    var handler: ApplicationHandler? = null

    constructor(
        layoutResource: Int,
        listener: ApplicationHandler
    ) {
        this.layoutResource = layoutResource
        this.handler = listener
    }

}