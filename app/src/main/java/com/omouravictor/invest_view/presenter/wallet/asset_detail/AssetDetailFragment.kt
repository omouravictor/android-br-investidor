package com.omouravictor.invest_view.presenter.wallet.asset_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetDetailBinding
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_search.AssetSearchViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAssetPrice
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedCurrentPosition
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalInvested
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedYield
import com.omouravictor.invest_view.util.LocaleUtil
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
        binding.tvYield.text = assetUiModel.getFormattedYield()
        binding.tvCurrentPosition.text = assetUiModel.getFormattedCurrentPosition()
    }

    private fun observeAssetQuote() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetSearchViewModel.assetQuoteStateFlow.collectLatest {
                    when (it) {
                        is UiState.Loading -> {
                            // Show loading
                        }

                        is UiState.Success -> {
                            val data = it.data
                            val change = data.change

                            when {
                                change > 0 -> {
                                    val color = ContextCompat.getColor(requireContext(), R.color.green)
                                    binding.tvVariation.setTextColor(color)
                                    binding.tvVariationPercent.setTextColor(color)
                                    binding.bracketStart.setTextColor(color)
                                    binding.bracketEnd.setTextColor(color)
                                    binding.ivArrow.visibility = View.VISIBLE
                                    binding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
                                }

                                change < 0 -> {
                                    val color = ContextCompat.getColor(requireContext(), R.color.red)
                                    binding.tvVariation.setTextColor(color)
                                    binding.tvVariationPercent.setTextColor(color)
                                    binding.bracketStart.setTextColor(color)
                                    binding.bracketEnd.setTextColor(color)
                                    binding.ivArrow.visibility = View.VISIBLE
                                    binding.ivArrow.setImageResource(R.drawable.ic_arrow_down)
                                }

                                else -> {
                                    val color = ContextCompat.getColor(requireContext(), R.color.gray)
                                    binding.tvVariation.setTextColor(color)
                                    binding.tvVariationPercent.setTextColor(color)
                                    binding.bracketStart.setTextColor(color)
                                    binding.bracketEnd.setTextColor(color)
                                    binding.ivArrow.visibility = View.GONE
                                }
                            }

                            val changeFormatted =
                                LocaleUtil.getFormattedCurrencyValue(assetUiModel.currency, change)
                            val changePercent = data.changePercent.removeSuffix("%").toDoubleOrNull()?.div(100)
                            val formattedChangePercent = LocaleUtil.getFormattedValueForPercent(changePercent)

                            binding.tvVariation.text = changeFormatted
                            binding.tvVariationPercent.text = formattedChangePercent
                        }

                        is UiState.Error -> {
                            // Show error
                        }

                        is UiState.Initial -> Unit
                    }
                }
            }
        }
    }

}