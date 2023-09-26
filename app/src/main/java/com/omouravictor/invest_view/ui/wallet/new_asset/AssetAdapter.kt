package com.omouravictor.invest_view.ui.wallet.new_asset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBinding

class AssetAdapter(
    private val items: List<AssetUiModel>,
    private val onClickItem: (AssetUiModel) -> Unit
) : RecyclerView.Adapter<AssetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemListAssetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(asset: AssetUiModel) {
            binding.tvAssetName.text = asset.name
            binding.tvCompanyName.text = asset.companyName
            binding.tvAssetCost.text = asset.cost.toString()
            itemView.setOnClickListener { onClickItem(asset) }
        }

    }
}
