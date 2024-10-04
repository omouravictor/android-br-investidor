package com.omouravictor.invest_view.presenter.wallet.asset_details

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetDetailsBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.user.UserViewModel
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.asset.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.asset.AssetViewModel
import com.omouravictor.invest_view.presenter.wallet.asset.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.asset.getFormattedAssetPrice
import com.omouravictor.invest_view.presenter.wallet.asset.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.asset.getFormattedTotalInvested
import com.omouravictor.invest_view.presenter.wallet.asset.getFormattedTotalPrice
import com.omouravictor.invest_view.presenter.wallet.asset.getTotalPrice
import com.omouravictor.invest_view.presenter.wallet.asset.getYield
import com.omouravictor.invest_view.presenter.wallet.currency_exchange_rates.CurrencyExchangeRatesViewModel
import com.omouravictor.invest_view.presenter.wallet.model.ConversionResultUiModel
import com.omouravictor.invest_view.presenter.wallet.model.GlobalQuoteUiModel
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.clearPileAndNavigateToStart
import com.omouravictor.invest_view.util.getErrorMessage
import com.omouravictor.invest_view.util.setupToolbarTitle
import com.omouravictor.invest_view.util.setupVariation
import com.omouravictor.invest_view.util.setupYieldForAsset
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AssetDetailsFragment : Fragment(R.layout.fragment_asset_details) {

    private lateinit var binding: FragmentAssetDetailsBinding
    private lateinit var assetUiModel: AssetUiModel
    private lateinit var navController: NavController
    private val args by navArgs<AssetDetailsFragmentArgs>()
    private val assetViewModel: AssetViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val currencyExchangeRatesViewModel: CurrencyExchangeRatesViewModel by activityViewModels()
    private val walletViewModel: WalletViewModel by activityViewModels()
    private var globalQuote: GlobalQuoteUiModel? = null
    private var conversionResult: ConversionResultUiModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetUiModel = args.assetUiModel
        navController = findNavController()
        assetViewModel.getQuote(assetUiModel.symbol)
        checkCurrencyForConversion()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssetDetailsBinding.bind(view)
        updateAssetUiModel()
        setupToolbar()
        setupViews()
        setupButtons()
        observeGetQuoteUiState()
        observeDeleteAssetUiState()
        observeGetConversionResultUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walletViewModel.resetDeleteAssetUiState()
    }

    private fun checkCurrencyForConversion() {
        val appCurrency = LocaleUtil.appCurrency.toString()
        val assetCurrency = assetUiModel.currency
        if (appCurrency != assetCurrency) {
            currencyExchangeRatesViewModel.convert(assetCurrency, appCurrency)
        }
    }

    private fun updateAssetUiModel() {
        assetUiModel = navController
            .currentBackStackEntry!!
            .savedStateHandle[ConstantUtil.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL]
            ?: assetUiModel
    }

    private fun showAlertDialogForDelete() {
        AlertDialog.Builder(context).apply {
            setTitle(assetUiModel.getFormattedSymbol())
            setMessage(getString(R.string.deleteAssetAlertMessage))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                walletViewModel.deleteAsset(
                    assetUiModel,
                    userViewModel.user.value.uid
                )
            }
            setNegativeButton(getString(R.string.not)) { dialog, _ -> dialog.dismiss() }
            setIcon(R.drawable.ic_delete)
        }.show()
    }

    private fun setupToolbar() {
        val activity = requireActivity()

        activity.setupToolbarTitle(assetUiModel.getFormattedSymbol())

        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_details, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.editMenuItem -> {
                        navController.navigate(AssetDetailsFragmentDirections.navToSaveAssetFragment(assetUiModel))
                        true
                    }

                    R.id.deleteMenuItem -> {
                        showAlertDialogForDelete()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun setupViews() {
        setupCardWalletDetails()
        setupCardAssetDetails()
        setupCardConversionRate()
    }

    private fun setupCardWalletDetails() {
        binding.incCardWalletDetails.apply {
            tvAmount.text = assetUiModel.getFormattedAmount()
            tvCurrentPosition.text = assetUiModel.getFormattedTotalPrice()
            tvTotalInvested.text = assetUiModel.getFormattedTotalInvested()
            tvYield.setupYieldForAsset(assetUiModel)
        }
    }

    private fun setupCardAssetDetails() {
        val context = requireContext()

        binding.incCardAssetDetails.apply {
            tvAssetType.text = getString(assetUiModel.type.nameResId)
            tvAssetType.backgroundTintList = getColorStateList(context, assetUiModel.type.colorResId)
            tvSymbol.text = assetUiModel.getFormattedSymbol()
            tvCompanyName.text = assetUiModel.name
            tvCurrencyBuy.text = assetUiModel.currency
            tvCurrentPrice.text = assetUiModel.getFormattedAssetPrice()
            ivLastChangeReload.setOnClickListener { assetViewModel.getQuote(assetUiModel.symbol) }
        }
    }

    private fun setupCardConversionRate() {
        val context = requireContext()
        val appCurrency = LocaleUtil.appCurrency.toString()
        val assetCurrency = assetUiModel.currency

        binding.incCardConversionRate.apply {
            if (appCurrency == assetCurrency) {
                root.visibility = View.GONE
            } else {
                root.visibility = View.VISIBLE
                tvCurrencyExchange.text = assetCurrency
                tvCurrencyExchange.backgroundTintList =
                    getColorStateList(context, AssetUtil.getCurrencyResColor(assetCurrency))
                tvTitleCurrencyConversion.text = getString(R.string.convertValuesToLocalCurrency, appCurrency)
            }

            switchCurrencyConversion.setOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    val rate = conversionResult!!.info.rate
                    convertCurrencyViews(appCurrency, rate)
                } else {
                    button.isChecked = false
                    resetCurrencyViews()
                }
            }

            ivSwitchReload.setOnClickListener { currencyExchangeRatesViewModel.convert(assetCurrency, appCurrency) }
        }
    }

    private fun convertCurrencyViews(currency: String, rate: Double) {
        binding.apply {
            incCardAssetDetails.tvCurrentPrice.text =
                LocaleUtil.getFormattedCurrencyValue(currency, assetUiModel.price * rate)

            globalQuote?.let { globalQuote ->
                incCardAssetDetails.tvLastChange
                    .setupVariation(currency, globalQuote.change * rate, globalQuote.changePercent / 100)
            }

            incCardWalletDetails.tvCurrentPosition.text =
                LocaleUtil.getFormattedCurrencyValue(currency, assetUiModel.getTotalPrice() * rate)

            incCardWalletDetails.tvTotalInvested.text =
                LocaleUtil.getFormattedCurrencyValue(currency, assetUiModel.totalInvested * rate)

            assetUiModel.getYield()?.let { yield ->
                incCardWalletDetails.tvYield
                    .setupVariation(currency, yield * rate, yield / assetUiModel.totalInvested)
            }
        }
    }

    private fun resetCurrencyViews() {
        binding.apply {
            incCardAssetDetails.tvCurrentPrice.text = assetUiModel.getFormattedAssetPrice()

            incCardAssetDetails.tvLastChange
                .setupVariation(assetUiModel.currency, globalQuote!!.change, globalQuote!!.changePercent / 100)

            incCardWalletDetails.tvCurrentPosition.text = assetUiModel.getFormattedTotalPrice()

            incCardWalletDetails.tvTotalInvested.text = assetUiModel.getFormattedTotalInvested()

            incCardWalletDetails.tvYield.setupYieldForAsset(assetUiModel)
        }
    }

    private fun setupButtons() {
        binding.incBtnNewTransaction.root.apply {
            text = getString(R.string.newTransaction)
            setOnClickListener {
                navController.navigate(AssetDetailsFragmentDirections.navToTransactionFragment(assetUiModel))
            }
        }
    }

    private fun handleQuoteLoading(isLoading: Boolean) {
        binding.incCardAssetDetails.apply {
            if (isLoading) {
                incLastChangeShimmer.root.startShimmer()
                incLastChangeShimmer.root.visibility = View.VISIBLE
                tvLastChange.visibility = View.INVISIBLE
                ivLastChangeReload.visibility = View.INVISIBLE
            } else {
                incLastChangeShimmer.root.stopShimmer()
                incLastChangeShimmer.root.visibility = View.INVISIBLE
                tvLastChange.visibility = View.VISIBLE
                ivLastChangeReload.visibility = View.INVISIBLE
            }
        }
    }

    private fun handleQuoteSuccess(globalQuote: GlobalQuoteUiModel) {
        handleQuoteLoading(false)
        val change = globalQuote.change
        val changePercent = globalQuote.changePercent
        binding.incCardAssetDetails.tvLastChange.setupVariation(assetUiModel.currency, change, changePercent / 100)
        this.globalQuote = globalQuote
    }

    private fun handleQuoteError() {
        handleQuoteLoading(false)
        binding.incCardAssetDetails.apply {
            tvLastChange.visibility = View.INVISIBLE
            ivLastChangeReload.visibility = View.VISIBLE
        }
    }

    private fun observeGetQuoteUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetViewModel.getQuoteUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleQuoteLoading(true)
                        is UiState.Success -> handleQuoteSuccess(it.data)
                        is UiState.Error -> handleQuoteError()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun handleDeleteAssetLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mainLayout.visibility = View.INVISIBLE
            binding.incBtnNewTransaction.root.visibility = View.INVISIBLE
            binding.incProgressBar.root.visibility = View.VISIBLE
        } else {
            binding.mainLayout.visibility = View.VISIBLE
            binding.incBtnNewTransaction.root.visibility = View.VISIBLE
            binding.incProgressBar.root.visibility = View.GONE
        }
    }

    private fun handleDeleteAssetError(e: Exception) {
        handleDeleteAssetLoading(false)
        with(requireActivity()) { showErrorSnackBar(getErrorMessage(e)) }
    }

    private fun observeDeleteAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.deleteAssetUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleDeleteAssetLoading(true)
                        is UiState.Success -> navController.clearPileAndNavigateToStart()
                        is UiState.Error -> handleDeleteAssetError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun handleConversionResultLoading(isLoading: Boolean) {
        binding.incCardConversionRate.apply {
            if (isLoading) {
                incSwitchShimmer.root.startShimmer()
                incSwitchShimmer.root.visibility = View.VISIBLE
                switchCurrencyConversion.visibility = View.INVISIBLE
                ivSwitchReload.visibility = View.INVISIBLE
            } else {
                incSwitchShimmer.root.stopShimmer()
                incSwitchShimmer.root.visibility = View.INVISIBLE
                switchCurrencyConversion.visibility = View.VISIBLE
                ivSwitchReload.visibility = View.INVISIBLE
            }
        }
    }

    private fun handleConversionResultSuccess(conversionResult: ConversionResultUiModel) {
        if (conversionResult.success == true) {
            handleConversionResultLoading(false)
            binding.incCardConversionRate.tvCurrencyRate.text = LocaleUtil.getFormattedCurrencyValue(
                LocaleUtil.appCurrency.toString(),
                conversionResult.info.rate
            )
            this.conversionResult = conversionResult
        } else {
            handleConversionResultError()
        }
    }

    private fun handleConversionResultError() {
        handleConversionResultLoading(false)
        binding.incCardConversionRate.apply {
            switchCurrencyConversion.visibility = View.INVISIBLE
            ivSwitchReload.visibility = View.VISIBLE
        }
    }

    private fun observeGetConversionResultUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currencyExchangeRatesViewModel.getConversionResultUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleConversionResultLoading(true)
                        is UiState.Success -> handleConversionResultSuccess(it.data)
                        is UiState.Error -> handleConversionResultError()
                        else -> Unit
                    }
                }
            }
        }
    }

}