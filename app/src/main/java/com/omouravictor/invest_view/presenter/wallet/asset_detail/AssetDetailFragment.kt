package com.omouravictor.invest_view.presenter.wallet.asset_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omouravictor.invest_view.databinding.FragmentAssetDetailBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel

class AssetDetailFragment : Fragment() {

    private lateinit var binding: FragmentAssetDetailBinding
    private lateinit var assetUiModel: AssetUiModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEssentialVars()
        binding.et.text = assetUiModel.symbol
    }

    private fun initEssentialVars() {
        val assetUiModel = AssetDetailFragmentArgs.fromBundle(requireArguments()).assetUiModel
        this.assetUiModel = AssetUiModel(
            assetUiModel.symbol,
            assetUiModel.name,
            assetUiModel.assetType,
            assetUiModel.region,
            assetUiModel.currency,
            assetUiModel.price,
            assetUiModel.amount,
            assetUiModel.totalInvested
        )
    }

}