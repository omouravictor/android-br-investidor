package com.omouravictor.invest_view.presenter.wallet.asset_search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ItemListAssetBySearchBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol

class AssetBySearchAdapter :
    BaseRecyclerViewAdapter<AssetUiModel, AssetBySearchAdapter.AssetBySearchViewHolder>() {

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
        fun bind(assetUiModel: AssetUiModel) {
            binding.color.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            binding.tvSymbol.text = assetUiModel.getFormattedSymbol()
            binding.tvName.text = assetUiModel.name
            binding.tvCurrency.text = "(${assetUiModel.currency})"
            binding.tvAssetType.text = context.getString(assetUiModel.assetType.nameResId)
            itemView.setOnClickListener { onClickItem(assetUiModel) }
        }
    }
}
