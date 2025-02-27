package com.omouravictor.br_investidor.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setupRecyclerViewWithLinearLayout(recyclerViewAdapter: RecyclerView.Adapter<*>) {
    adapter = recyclerViewAdapter
    layoutManager = LinearLayoutManager(context)
}