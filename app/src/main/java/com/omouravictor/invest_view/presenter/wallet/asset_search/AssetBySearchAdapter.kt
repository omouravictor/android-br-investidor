package com.omouravictor.invest_view.presenter.wallet.asset_search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBySearchBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.util.AssetUtil

class AssetBySearchAdapter : BaseRecyclerViewAdapter<AssetBySearchUiModel, AssetBySearchAdapter.AssetBySearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetBySearchViewHolder {
        val binding = ItemListAssetBySearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetBySearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetBySearchViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class AssetBySearchViewHolder(private val binding: ItemListAssetBySearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        @SuppressLint("SetTextI18n")
        fun bind(assetBySearchUiModel: AssetBySearchUiModel) {
            val assetType = AssetUtil.getAssetType(assetBySearchUiModel.symbol, assetBySearchUiModel.type)
            binding.tvSymbol.text = AssetUtil.getFormattedSymbol(assetBySearchUiModel.symbol)
            binding.tvName.text = assetBySearchUiModel.name
            binding.tvCurrency.text = "(${assetBySearchUiModel.currency})"
            binding.tvAssetType.text = assetType.getName(context)
            binding.color.backgroundTintList = assetType.getColorStateList(context)
            itemView.setOnClickListener { onClickItem(assetBySearchUiModel) }
        }
    }
}
