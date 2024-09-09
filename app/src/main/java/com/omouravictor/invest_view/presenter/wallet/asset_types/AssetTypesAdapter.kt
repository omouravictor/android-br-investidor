package com.omouravictor.invest_view.presenter.wallet.asset_types

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemAssetBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.model.AssetUiModel
import com.omouravictor.invest_view.presenter.model.getFormattedSymbolAndAmount
import com.omouravictor.invest_view.presenter.model.getFormattedTotalPrice
import com.omouravictor.invest_view.util.setupYieldForAsset

class AssetTypesAdapter : BaseRecyclerViewAdapter<AssetUiModel, AssetTypesAdapter.AssetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class AssetViewHolder(private val binding: ItemAssetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetUiModel: AssetUiModel) {
            binding.color
                .setBackgroundColor(ContextCompat.getColor(binding.root.context, assetUiModel.assetType.colorResId))
            binding.tvSymbolAmount.text = assetUiModel.getFormattedSymbolAndAmount()
            binding.tvName.text = assetUiModel.name
            binding.tvTotalPrice.text = assetUiModel.getFormattedTotalPrice()
            binding.tvYield.setupYieldForAsset(assetUiModel)
            itemView.setOnClickListener { onClickItem(assetUiModel) }
        }
    }
}
