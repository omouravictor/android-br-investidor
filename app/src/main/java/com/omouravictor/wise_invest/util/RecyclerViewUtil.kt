package com.omouravictor.wise_invest.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setupRecyclerViewWithLinearLayout(recyclerViewAdapter: RecyclerView.Adapter<*>) {
    adapter = recyclerViewAdapter
    layoutManager = LinearLayoutManager(context)
}