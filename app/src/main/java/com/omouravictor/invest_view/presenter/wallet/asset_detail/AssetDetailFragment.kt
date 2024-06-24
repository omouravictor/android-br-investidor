package com.omouravictor.invest_view.presenter.wallet.asset_detail

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
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
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedPriceCurrentPosition
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalInvested
import com.omouravictor.invest_view.presenter.wallet.model.getPriceCurrentPosition
import com.omouravictor.invest_view.util.AppUtil
import com.omouravictor.invest_view.util.BindingUtil
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
        setupToolbar()
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

    private fun setupToolbar() {
        val activity = requireActivity()

        activity.findViewById<Toolbar>(R.id.toolbar).apply {
            title = ""
            findViewById<TextView>(R.id.tvToolbarCenterText).text = assetUiModel.getFormattedSymbol()
        }

        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_edit, menu)
                val menuItem = menu.findItem(R.id.editMenuItem)
                val spanStr = SpannableString(menuItem.title.toString())
                spanStr.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(activity, R.color.green)), 0, spanStr.length, 0
                )
                menuItem.title = spanStr
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                AppUtil.showSuccessSnackBar(activity, "Testandoooooooooo")
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setupViews() {
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

    private fun setupLoadingLayout(isLoading: Boolean) {
        if (isLoading) {
            binding.incShimmerItemVariation.root.startShimmer()
            binding.incShimmerItemVariation.root.visibility = View.VISIBLE
            binding.incLayoutVariation.root.visibility = View.INVISIBLE
        } else {
            binding.incShimmerItemVariation.root.stopShimmer()
            binding.incShimmerItemVariation.root.visibility = View.INVISIBLE
            binding.incLayoutVariation.root.visibility = View.VISIBLE
        }
    }

    private fun setupErrorLayout() {
        binding.incLayoutVariation.root.visibility = View.GONE
    }

    private fun observeAssetQuote() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetSearchViewModel.assetQuoteStateFlow.collectLatest {
                    when (it) {
                        is UiState.Loading -> setupLoadingLayout(true)

                        is UiState.Success -> {
                            val variation = it.data.change
                            val variationPercent = it.data.changePercent.removeSuffix("%").toDoubleOrNull()?.div(100)

                            binding.incLayoutVariation.tvVariation.text =
                                getFormattedCurrencyValue(assetUiModel.currency, variation)
                            binding.incLayoutVariation.tvVariationPercent.text =
                                getFormattedValueForPercent(variationPercent)
                            BindingUtil.setupColorsAndArrow(binding.incLayoutVariation, variation)
                            setupLoadingLayout(false)
                        }

                        is UiState.Error -> {
                            setupLoadingLayout(false)
                            setupErrorLayout()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

}