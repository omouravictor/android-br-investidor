package com.omouravictor.invest_view.presenter.wallet.asset_search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBySearchBinding
import com.omouravictor.invest_view.presenter.base.RecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel

class AssetBySearchAdapter :
    RecyclerViewAdapter<AssetBySearchUiModel, AssetBySearchAdapter.AssetBySearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetBySearchViewHolder {
        val binding =
            ItemListAssetBySearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetBySearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetBySearchViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class AssetBySearchViewHolder(private val binding: ItemListAssetBySearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assetBySearchUiModel: AssetBySearchUiModel) {
            binding.tvSymbol.text = assetBySearchUiModel.symbol
            binding.tvAssetCompanyName.text = assetBySearchUiModel.companyName
            itemView.setOnClickListener { onClickItem(assetBySearchUiModel) }
        }
    }
}
