package com.omouravictor.invest_view.ui.wallet.new_asset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetTypeBinding

class AssetTypeAdapter(
    private val assetTypes: List<String>,
    private val onClickItem: () -> Unit
) :
    RecyclerView.Adapter<AssetTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListAssetTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = assetTypes[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return assetTypes.size
    }

    inner class ViewHolder(private val binding: ItemListAssetTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assetType: String) {
            binding.tvAssetType.text = assetType
            itemView.setOnClickListener { onClickItem() }
        }

    }
}
