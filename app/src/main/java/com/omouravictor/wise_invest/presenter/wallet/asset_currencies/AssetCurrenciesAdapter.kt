package com.omouravictor.wise_invest.presenter.wallet.asset_currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.wise_invest.databinding.ItemAssetBinding
import com.omouravictor.wise_invest.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.wise_invest.presenter.wallet.asset.AssetUiModel
import com.omouravictor.wise_invest.presenter.wallet.asset.getFormattedSymbolAndAmount
import com.omouravictor.wise_invest.presenter.wallet.asset.getFormattedTotalPrice
import com.omouravictor.wise_invest.util.AssetUtil
import com.omouravictor.wise_invest.util.setupYieldForAsset

class AssetCurrenciesAdapter : BaseRecyclerViewAdapter<AssetUiModel, AssetCurrenciesAdapter.AssetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class AssetViewHolder(private val binding: ItemAssetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetUiModel: AssetUiModel) {
            val currencyResColor = AssetUtil.getCurrencyResColor(assetUiModel.currency)
            binding.color.setBackgroundColor(ContextCompat.getColor(binding.root.context, currencyResColor))
            binding.tvSymbolAmount.text = assetUiModel.getFormattedSymbolAndAmount()
            binding.tvName.text = assetUiModel.name
            binding.tvTotalPrice.text = assetUiModel.getFormattedTotalPrice()
            binding.tvYield.setupYieldForAsset(assetUiModel)
            itemView.setOnClickListener { onClickItem(assetUiModel) }
        }
    }
}
