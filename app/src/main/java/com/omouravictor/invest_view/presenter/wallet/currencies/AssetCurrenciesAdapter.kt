package com.omouravictor.invest_view.presenter.wallet.currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.util.AssetUtil

class AssetCurrenciesAdapter : BaseRecyclerViewAdapter<AssetUiModel, AssetCurrenciesAdapter.AssetViewHolder>() {

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
            val currency = assetUiModel.currency
            AssetUtil.setupAssetUiModelBind(
                binding,
                assetUiModel,
                ContextCompat.getColor(context, currency.colorResId)
            )
            itemView.setOnClickListener { onClickItem(assetUiModel) }
        }
    }
}
