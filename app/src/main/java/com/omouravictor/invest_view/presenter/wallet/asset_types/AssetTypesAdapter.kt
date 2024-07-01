package com.omouravictor.invest_view.presenter.wallet.asset_types

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getTotalPrice
import com.omouravictor.invest_view.util.calculateAndSetupVariationLayout

class AssetTypesAdapter : BaseRecyclerViewAdapter<AssetUiModel, AssetTypesAdapter.AssetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemListAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    @SuppressLint("SetTextI18n")
    inner class AssetViewHolder(private val binding: ItemListAssetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetUiModel: AssetUiModel) {
            binding.color
                .setBackgroundColor(ContextCompat.getColor(binding.root.context, assetUiModel.assetType.colorResId))
            binding.tvSymbol.text = assetUiModel.getFormattedSymbol()
            binding.tvAmount.text = "(${assetUiModel.getFormattedAmount()})"
            binding.tvName.text = assetUiModel.name
            binding.tvTotalPrice.text = assetUiModel.getFormattedTotalPrice()
            itemView.setOnClickListener { onClickItem(assetUiModel) }
            binding.incLayoutVariation.calculateAndSetupVariationLayout(
                textSize = 12f,
                currency = assetUiModel.currency,
                reference = assetUiModel.getTotalPrice(),
                totalReference = assetUiModel.totalInvested
            )
        }
    }
}
