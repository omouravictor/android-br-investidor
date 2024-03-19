package com.omouravictor.invest_view.presenter.base.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("NotifyDataSetChanged")
abstract class RecyclerViewAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected var itemsList = listOf<T>()
    protected var onClickItem: (T) -> Unit = {}

    fun updateItemsList(list: List<T>) {
        itemsList = list
        notifyDataSetChanged()
    }

    fun updateOnClickItem(action: (T) -> Unit) {
        onClickItem = action
    }

    override fun getItemCount(): Int = itemsList.size

}