package com.omouravictor.invest_view.presenter.wallet.assets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalAssetPrice
import com.omouravictor.invest_view.presenter.wallet.model.getTotalAssetPrice
import com.omouravictor.invest_view.util.AssetUtil

class AssetsAdapter : BaseRecyclerViewAdapter<AssetUiModel, AssetsAdapter.AssetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemListAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class AssetViewHolder(private val binding: ItemListAssetBinding) : RecyclerView.ViewHolder(binding.root) {
        private val context = itemView.context

        fun bind(assetUiModel: AssetUiModel) {
            val assetType = assetUiModel.assetType
            binding.color.backgroundTintList = assetType.getColorStateList(context)
            binding.tvSymbol.text = AssetUtil.getFormattedSymbol(assetUiModel.symbol)
            binding.tvAmount.text = assetUiModel.getFormattedAmount()
            binding.tvAssetType.text = assetType.getName(context)
            binding.tvTotal.text = assetUiModel.getFormattedTotalAssetPrice()
            AssetUtil.setupVariationViews(
                binding,
                assetUiModel.currency,
                assetUiModel.totalInvested,
                assetUiModel.getTotalAssetPrice()
            )
            itemView.setOnClickListener { onClickItem(assetUiModel) }
        }
    }
}
