package com.omouravictor.invest_view.ui.wallet.assets.asset_types

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetTypeBinding
import com.omouravictor.invest_view.ui.wallet.assets.asset_types.model.AssetTypeUi

class AssetTypeAdapter(
    private val items: List<AssetTypeUi>,
    private val onClickItem: (AssetTypeUi) -> Unit
) : RecyclerView.Adapter<AssetTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListAssetTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemListAssetTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assetTypeUi: AssetTypeUi) {
            binding.tvAssetType.text = assetTypeUi.description

            binding.ivCircle.backgroundTintList = assetTypeUi.color

            itemView.setOnClickListener { onClickItem(assetTypeUi) }
        }

    }
}
