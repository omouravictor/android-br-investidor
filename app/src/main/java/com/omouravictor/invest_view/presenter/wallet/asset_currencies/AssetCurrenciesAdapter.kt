package com.omouravictor.invest_view.presenter.wallet.asset_currencies

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbolAndAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedYield
import com.omouravictor.invest_view.util.AssetUtil

class AssetCurrenciesAdapter : BaseRecyclerViewAdapter<AssetUiModel, AssetCurrenciesAdapter.AssetViewHolder>() {

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
            val currencyResColor = AssetUtil.getCurrencyResColor(assetUiModel.currency)
            binding.color.setBackgroundColor(ContextCompat.getColor(binding.root.context, currencyResColor))
            binding.tvSymbolAmount.text = assetUiModel.getFormattedSymbolAndAmount()
            binding.tvName.text = assetUiModel.name
            binding.tvTotalPrice.text = assetUiModel.getFormattedTotalPrice()
            binding.tvYield.text = assetUiModel.getFormattedYield()
            itemView.setOnClickListener { onClickItem(assetUiModel) }
        }
    }
}
