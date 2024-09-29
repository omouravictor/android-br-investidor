package com.omouravictor.invest_view.presenter.wallet.asset_search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemAssetBySearchBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.asset.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.asset.getFormattedSymbol

class AssetBySearchAdapter :
    BaseRecyclerViewAdapter<AssetUiModel, AssetBySearchAdapter.AssetBySearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetBySearchViewHolder {
        val binding = ItemAssetBySearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetBySearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetBySearchViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class AssetBySearchViewHolder(private val binding: ItemAssetBySearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        @SuppressLint("SetTextI18n")
        fun bind(assetUiModel: AssetUiModel) {
            val assetType = assetUiModel.type
            binding.color.setBackgroundColor(ContextCompat.getColor(context, assetType.colorResId))
            binding.tvSymbol.text = "${assetUiModel.getFormattedSymbol()} (${assetUiModel.currency})"
            binding.tvName.text = assetUiModel.name
            binding.tvAssetType.text = context.getString(assetType.nameResId)
            itemView.setOnClickListener { onClickItem(assetUiModel) }
        }
    }
}
