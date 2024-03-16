package com.omouravictor.invest_view.presenter.wallet.assets.asset_types

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetTypeBinding
import com.omouravictor.invest_view.presenter.base.RecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.assets.asset_types.model.AssetTypeUiModel

class AssetTypeAdapter :
    RecyclerViewAdapter<AssetTypeUiModel, AssetTypeAdapter.AssetTypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetTypeViewHolder {
        val binding =
            ItemListAssetTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetTypeViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class AssetTypeViewHolder(private val binding: ItemListAssetTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assetTypeUiModel: AssetTypeUiModel) {
            binding.tvAssetType.text = assetTypeUiModel.description
            binding.ivCircle.backgroundTintList = assetTypeUiModel.color
            itemView.setOnClickListener { onClickItem(assetTypeUiModel) }
        }

    }
}
