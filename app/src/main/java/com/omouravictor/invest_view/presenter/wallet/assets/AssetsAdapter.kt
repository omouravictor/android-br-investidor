package com.omouravictor.invest_view.presenter.wallet.assets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.base.RecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbolAndAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalAssetPrice
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedVariationAndPercent

class AssetsAdapter : RecyclerViewAdapter<AssetUiModel, AssetsAdapter.AssetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemListAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class AssetViewHolder(private val binding: ItemListAssetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(assetUiModel: AssetUiModel) {
            binding.tvSymbolAndAmount.text = assetUiModel.getFormattedSymbolAndAmount()
            binding.tvName.text = assetUiModel.name
            binding.tvTotal.text = assetUiModel.getFormattedTotalAssetPrice()
            binding.tvVariation.text = assetUiModel.getFormattedVariationAndPercent()
        }
    }
}
