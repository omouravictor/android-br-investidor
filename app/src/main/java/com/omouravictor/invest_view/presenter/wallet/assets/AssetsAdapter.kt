package com.omouravictor.invest_view.presenter.wallet.assets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.base.RecyclerViewAdapter
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel

class AssetsAdapter :
    RecyclerViewAdapter<AssetBySearchUiModel, AssetsAdapter.AssetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding =
            ItemListAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        //        holder.bind(itemsList[position])
    }

    inner class AssetViewHolder(private val binding: ItemListAssetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        fun bind(assetUiModel: AssetUiModel) {
            //            binding.tvSymbolAndQuantity.text =
            //                context.getString(R.string.placeholderSymbolAndQuantity, assetUiModel.symbol, assetUiModel.quantity)
            //            binding.tvName.text = assetUiModel.name
            //            binding.tv
        }
    }
}
