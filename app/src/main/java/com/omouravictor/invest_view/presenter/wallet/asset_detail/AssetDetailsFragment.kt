package com.omouravictor.invest_view.presenter.wallet.asset_detail

import android.app.AlertDialog
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
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetDetailsBinding
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.asset_search.AssetSearchViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAssetPrice
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedPriceCurrentPosition
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalInvested
import com.omouravictor.invest_view.presenter.wallet.model.getPriceCurrentPosition
import com.omouravictor.invest_view.util.AppUtil
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.BindingUtil
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedCurrencyValue
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForPercent
import com.omouravictor.invest_view.util.NavigationUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AssetDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAssetDetailsBinding
    private lateinit var assetUiModel: AssetUiModel
    private val assetSearchViewModel: AssetSearchViewModel by activityViewModels()
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val assetDetailsViewModel: AssetDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEssentialVars()
        setupToolbar()
        setupViews()
        setupButtons()
        observeAssetQuote()
        observeAssetDetailsUiState()
        assetSearchViewModel.getAssetQuote(assetUiModel.symbol)
    }

    override fun onStop() {
        super.onStop()
        assetSearchViewModel.resetAssetQuoteLiveData()
        assetDetailsViewModel.resetUiStateFlow()
    }

    private fun initEssentialVars() {
        val assetUiModel = AssetDetailsFragmentArgs.fromBundle(requireArguments()).assetUiModel
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
        val context = requireContext()
        binding.tvAssetType.text = getString(assetUiModel.assetType.nameResId)
        binding.tvAssetType.backgroundTintList = getColorStateList(context, assetUiModel.assetType.colorResId)
        binding.tvAssetCurrency.text = assetUiModel.currency
        binding.tvAssetCurrency.backgroundTintList =
            getColorStateList(context, AssetUtil.getCurrencyResColor(assetUiModel.currency))
        binding.tvName.text = assetUiModel.name
        binding.tvPrice.text = assetUiModel.getFormattedAssetPrice()
        binding.tvAmount.text = assetUiModel.getFormattedAmount()
        binding.tvTotalInvested.text = assetUiModel.getFormattedTotalInvested()
        binding.tvCurrentPosition.text = assetUiModel.getFormattedPriceCurrentPosition()
        BindingUtil.calculateAndSetupVariationLayout(
            binding = binding.incLayoutYield,
            textSize = 14f,
            currency = assetUiModel.currency,
            reference = assetUiModel.getPriceCurrentPosition(),
            totalReference = assetUiModel.totalInvested
        )
        binding.ivReloadVariation.setOnClickListener { assetSearchViewModel.getAssetQuote(assetUiModel.symbol) }
    }

    private fun setupButtons() {
        binding.incBtnDelete.root.apply {
            text = getString(R.string.delete)
            setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle(getString(R.string.deleteAsset))
                    setMessage(getString(R.string.deleteAssetAlertMessage))
                    setPositiveButton(getString(R.string.yes)) { _, _ -> assetDetailsViewModel.deleteAsset(assetUiModel) }
                    setNegativeButton(getString(R.string.not)) { dialog, _ -> dialog.dismiss() }
                    setIcon(R.drawable.ic_delete)
                }.show()
            }
        }

        binding.incBtnNewContribution.root.apply {
            text = getString(R.string.newContribution)
            setOnClickListener {
                AppUtil.showSuccessSnackBar(requireActivity(), "Testandoooooooooo")
            }
        }
    }

    private fun setupLoadingLayoutForAssetQuote(isLoading: Boolean) {
        if (isLoading) {
            binding.incShimmerItemVariation.root.startShimmer()
            binding.incShimmerItemVariation.root.visibility = View.VISIBLE
            binding.incLayoutVariation.root.visibility = View.INVISIBLE
            binding.ivReloadVariation.visibility = View.INVISIBLE
        } else {
            binding.incShimmerItemVariation.root.stopShimmer()
            binding.incShimmerItemVariation.root.visibility = View.INVISIBLE
            binding.incLayoutVariation.root.visibility = View.VISIBLE
            binding.ivReloadVariation.visibility = View.INVISIBLE
        }
    }

    private fun observeAssetQuote() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetSearchViewModel.assetQuoteStateFlow.collectLatest {
                    when (it) {
                        is UiState.Initial -> Unit
                        is UiState.Loading -> setupLoadingLayoutForAssetQuote(true)
                        is UiState.Success -> {
                            setupLoadingLayoutForAssetQuote(false)
                            val variation = it.data.change
                            val variationPercent =
                                it.data.changePercent.removeSuffix("%").toDoubleOrNull()?.div(100)

                            binding.incLayoutVariation.apply {
                                tvVariation.text = getFormattedCurrencyValue(assetUiModel.currency, variation)
                                tvVariationPercent.text = getFormattedValueForPercent(variationPercent)
                                BindingUtil.setupTextsSize(this, 13f)
                                BindingUtil.setupColorsAndArrow(this, variation)
                            }
                        }

                        is UiState.Error -> {
                            setupLoadingLayoutForAssetQuote(false)
                            binding.incLayoutVariation.root.visibility = View.INVISIBLE
                            binding.ivReloadVariation.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun observeAssetDetailsUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetDetailsViewModel.uiStateFlow.collectLatest {
                    when (it) {
                        is UiState.Initial -> Unit
                        is UiState.Loading -> {
                            binding.mainLayout.visibility = View.INVISIBLE
                            binding.incProgressBar.root.visibility = View.VISIBLE
                        }

                        is UiState.Success -> {
                            walletViewModel.removeAsset(assetUiModel)
                            NavigationUtil.clearPileAndNavigateToStart(findNavController())
                        }

                        is UiState.Error -> {
                            val activity = requireActivity()
                            binding.mainLayout.visibility = View.VISIBLE
                            binding.incProgressBar.root.visibility = View.GONE
                            AppUtil.showErrorSnackBar(activity, AppUtil.getGenericErrorMessage(activity, it.e))
                        }
                    }
                }
            }
        }
    }

}