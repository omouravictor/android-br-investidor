package com.omouravictor.invest_view.ui.wallet.new_asset

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListBinding
import com.omouravictor.invest_view.ui.base.ItemListUiModel

class AssetTypeAdapter(private val items: List<ItemListUiModel>) :
    RecyclerView.Adapter<AssetTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assetType: ItemListUiModel) {
            binding.tvItemName.text = assetType.name
            binding.ivItemIcon.setImageResource(assetType.icon)

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, assetType.name, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
