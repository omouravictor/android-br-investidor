package com.omouravictor.invest_view.ui.wallet.new_asset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBinding

class AssetAdapter(
    private val assets: List<String>,
    private val onClickItem: () -> Unit
) :
    RecyclerView.Adapter<AssetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = assets[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return assets.size
    }

    inner class ViewHolder(private val binding: ItemListAssetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(asset: String) {
            binding.tvAssetSymbol.text = asset
            binding.tvCompanyName.text = "Petrobras"
            binding.tvAssetCost.text = "R$ 10,00"
            itemView.setOnClickListener { onClickItem() }
        }

    }
}
