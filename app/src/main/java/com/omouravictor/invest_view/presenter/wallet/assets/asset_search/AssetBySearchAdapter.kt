package com.omouravictor.invest_view.presenter.wallet.assets.asset_search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBySearchBinding
import com.omouravictor.invest_view.presenter.wallet.assets.asset_search.model.AssetBySearchUiModel

class AssetBySearchAdapter : RecyclerView.Adapter<AssetBySearchAdapter.AssetBySearchViewHolder>() {

    private var assetBySearchList = listOf<AssetBySearchUiModel>()
    private var onAssetClick: (AssetBySearchUiModel) -> Unit = {}

    fun setAssetsBySearchList(assetBySearchList: List<AssetBySearchUiModel>) {
        this.assetBySearchList = assetBySearchList
        notifyDataSetChanged()
    }

    fun setOnAssetClick(onAssetClick: (AssetBySearchUiModel) -> Unit) {
        this.onAssetClick = onAssetClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetBySearchViewHolder {
        val binding =
            ItemListAssetBySearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetBySearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetBySearchViewHolder, position: Int) {
        holder.bind(assetBySearchList[position])
    }

    override fun getItemCount(): Int = assetBySearchList.size

    inner class AssetBySearchViewHolder(private val binding: ItemListAssetBySearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assetBySearchUiModel: AssetBySearchUiModel) {
            binding.tvAssetSymbol.text = assetBySearchUiModel.symbol
            binding.tvAssetName.text = assetBySearchUiModel.name
        }
    }
}
