package com.omouravictor.br_investidor.presenter.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("NotifyDataSetChanged")
abstract class BaseRecyclerViewAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected var itemsList = listOf<T>()
    protected var onClickItem: (T) -> Unit = {}

    override fun getItemCount(): Int = itemsList.size

    fun getList(): List<T> = itemsList

    fun setList(list: List<T>) {
        itemsList = list
        notifyDataSetChanged()
    }

    fun updateOnClickItem(action: (T) -> Unit) {
        onClickItem = action
    }

}