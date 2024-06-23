package com.omouravictor.invest_view.presenter.wallet.asset_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omouravictor.invest_view.databinding.FragmentAssetDetailBinding
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_search.AssetSearchViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAssetPrice
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedPriceCurrentPosition
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalInvested
import com.omouravictor.invest_view.presenter.wallet.model.getPriceCurrentPosition
import com.omouravictor.invest_view.util.BindingUtil
import com.omouravictor.invest_view.util.BindingUtil.setupColorsAndArrow
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedCurrencyValue
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForPercent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AssetDetailFragment : Fragment() {

    private lateinit var binding: FragmentAssetDetailBinding
    private lateinit var assetUiModel: AssetUiModel
    private val assetSearchViewModel: AssetSearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEssentialVars()
        setupViews()
        observeAssetQuote()
        assetSearchViewModel.getAssetQuote(assetUiModel.symbol)
    }

    override fun onStop() {
        super.onStop()
        assetSearchViewModel.resetAssetQuoteLiveData()
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

    private fun setupViews() {
        binding.tvSymbol.text = assetUiModel.getFormattedSymbol()
        binding.tvName.text = assetUiModel.name
        binding.tvPrice.text = assetUiModel.getFormattedAssetPrice()
        binding.tvAmount.text = assetUiModel.getFormattedAmount()
        binding.tvTotalInvested.text = assetUiModel.getFormattedTotalInvested()
        binding.tvCurrentPosition.text = assetUiModel.getFormattedPriceCurrentPosition()
        BindingUtil.calculateAndSetupVariationLayout(
            binding = binding.incLayoutYield,
            currency = assetUiModel.currency,
            reference = assetUiModel.getPriceCurrentPosition(),
            totalReference = assetUiModel.totalInvested
        )
    }

    private fun observeAssetQuote() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetSearchViewModel.assetQuoteStateFlow.collectLatest {
                    when (it) {
                        is UiState.Loading -> Unit
                        is UiState.Success -> {
                            val variation = it.data.change
                            val formattedVariation = getFormattedCurrencyValue(assetUiModel.currency, variation)
                            val variationPercent = it.data.changePercent.removeSuffix("%").toDoubleOrNull()?.div(100)
                            val formattedVariationPercent = getFormattedValueForPercent(variationPercent)

                            binding.incLayoutVariation.tvVariation.text = formattedVariation
                            binding.incLayoutVariation.tvVariationPercent.text = formattedVariationPercent
                            setupColorsAndArrow(binding.incLayoutVariation, variation)
                        }

                        is UiState.Error -> Unit
                        else -> Unit
                    }
                }
            }
        }
    }

}