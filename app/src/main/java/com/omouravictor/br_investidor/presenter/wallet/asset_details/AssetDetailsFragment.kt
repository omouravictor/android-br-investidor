package com.omouravictor.br_investidor.presenter.wallet.asset_details

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omouravictor.br_investidor.R
import com.omouravictor.br_investidor.databinding.FragmentAssetDetailsBinding
import com.omouravictor.br_investidor.presenter.model.UiState
import com.omouravictor.br_investidor.presenter.user.UserViewModel
import com.omouravictor.br_investidor.presenter.wallet.WalletViewModel
import com.omouravictor.br_investidor.presenter.wallet.asset.AssetUiModel
import com.omouravictor.br_investidor.presenter.wallet.asset.AssetViewModel
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedAmount
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedAssetPrice
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedSymbol
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedTotalInvested
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedTotalPrice
import com.omouravictor.br_investidor.presenter.wallet.asset.getTotalPrice
import com.omouravictor.br_investidor.presenter.wallet.asset.getYield
import com.omouravictor.br_investidor.presenter.wallet.currency_exchange_rates.CurrencyExchangeRatesViewModel
import com.omouravictor.br_investidor.presenter.wallet.model.CurrencyExchangeRateUiModel
import com.omouravictor.br_investidor.presenter.wallet.model.GlobalQuoteUiModel
import com.omouravictor.br_investidor.presenter.wallet.model.getRateForAppCurrency
import com.omouravictor.br_investidor.util.AppConstants.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL
import com.omouravictor.br_investidor.util.AssetUtil
import com.omouravictor.br_investidor.util.LocaleUtil
import com.omouravictor.br_investidor.util.clearPileAndNavigateToStart
import com.omouravictor.br_investidor.util.getErrorMessage
import com.omouravictor.br_investidor.util.setupToolbarTitle
import com.omouravictor.br_investidor.util.setupVariation
import com.omouravictor.br_investidor.util.setupYieldForAsset
import com.omouravictor.br_investidor.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AssetDetailsFragment : Fragment(R.layout.fragment_asset_details) {

    private lateinit var binding: FragmentAssetDetailsBinding
    private lateinit var activity: FragmentActivity
    private lateinit var assetUiModel: AssetUiModel
    private lateinit var navController: NavController
    private val args by navArgs<AssetDetailsFragmentArgs>()
    private val assetViewModel: AssetViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val currencyExchangeRatesViewModel: CurrencyExchangeRatesViewModel by activityViewModels()
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
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
        observeGetExchangeRateUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walletViewModel.resetDeleteAssetUiState()
    }

    private fun checkCurrencyForConversion() {
        val appCurrency = LocaleUtil.appCurrency.toString()
        val assetCurrency = assetUiModel.currency
        if (appCurrency != assetCurrency) {
            currencyExchangeRatesViewModel.getExchangeRate(assetCurrency, appCurrency)
        }
    }

    private fun updateAssetUiModel() {
        assetUiModel = navController
            .currentBackStackEntry!!
            .savedStateHandle[SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL]
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
            val assetCurrency = assetUiModel.currency
            tvAssetType.text = getString(assetUiModel.type.nameResId)
            tvAssetType.backgroundTintList = getColorStateList(context, assetUiModel.type.colorResId)
            tvSymbol.text = assetUiModel.getFormattedSymbol()
            tvName.text = assetUiModel.name
            tvCurrencyBuy.text = AssetUtil.getCurrencyResName(assetCurrency)
                ?.let { "$assetCurrency - ${getString(it)}" } ?: assetCurrency
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
                tvCurrencyExchange.text = AssetUtil.getCurrencyResName(assetCurrency)
                    ?.let { "$assetCurrency - ${getString(it)}" } ?: assetCurrency
                tvCurrencyExchange.backgroundTintList =
                    getColorStateList(context, AssetUtil.getCurrencyResColor(assetCurrency))
                tvTitleCurrencyConversion.text = getString(
                    R.string.convertValuesToLocalCurrency,
                    appCurrency,
                    AssetUtil.getCurrencyResName(appCurrency)?.let { "(${getString(it)})" } ?: ""
                )
            }

            switchCurrencyConversion.setOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    val rate = currencyExchangeRatesViewModel.exchangeRate.value?.getRateForAppCurrency() ?: 0.0
                    convertCurrencyViews(appCurrency, rate)
                } else {
                    button.isChecked = false
                    resetCurrencyViews()
                }
            }

            ivSwitchReload.setOnClickListener {
                currencyExchangeRatesViewModel.getExchangeRate(assetCurrency, appCurrency)
            }
        }
    }

    private fun convertCurrencyViews(currency: String, rate: Double) {
        binding.apply {
            incCardAssetDetails.tvCurrentPrice.text =
                LocaleUtil.getFormattedCurrencyValue(currency, assetUiModel.price * rate)

            assetViewModel.quote.value?.let { globalQuote ->
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

            assetViewModel.quote.value?.let {
                incCardAssetDetails.tvLastChange
                    .setupVariation(assetUiModel.currency, it.change, it.changePercent / 100)
            }

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

        assetUiModel.price = globalQuote.price
        setupCardWalletDetails()
        walletViewModel.updateAssetInDataBase(assetUiModel, userViewModel.user.value.uid)

        if (binding.incCardConversionRate.switchCurrencyConversion.isChecked) {
            val rate = currencyExchangeRatesViewModel.exchangeRate.value?.getRateForAppCurrency() ?: 0.0
            binding.incCardAssetDetails.tvLastChange.setupVariation(
                LocaleUtil.appCurrency.toString(),
                globalQuote.change * rate,
                globalQuote.changePercent / 100
            )
        } else {
            binding.incCardAssetDetails.tvLastChange.setupVariation(
                assetUiModel.currency,
                globalQuote.change,
                globalQuote.changePercent / 100
            )
        }
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
        activity.showErrorSnackBar(activity.getErrorMessage(e))
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

    private fun handleExchangeRateLoading(isLoading: Boolean) {
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

    private fun handleExchangeRateSuccess(currencyExchangeRate: CurrencyExchangeRateUiModel) {
        currencyExchangeRate.getRateForAppCurrency()?.let { rate ->
            handleExchangeRateLoading(false)
            binding.incCardConversionRate.tvCurrencyRate.text = LocaleUtil.getFormattedCurrencyValue(
                LocaleUtil.appCurrency.toString(),
                rate
            )
        } ?: handleExchangeRateError()
    }

    private fun handleExchangeRateError() {
        handleExchangeRateLoading(false)
        binding.incCardConversionRate.apply {
            switchCurrencyConversion.visibility = View.INVISIBLE
            ivSwitchReload.visibility = View.VISIBLE
        }
        Toast.makeText(context, getString(R.string.errorGettingExchangeRate), Toast.LENGTH_SHORT).show()
    }

    private fun observeGetExchangeRateUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currencyExchangeRatesViewModel.getExchangeRateUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleExchangeRateLoading(true)
                        is UiState.Success -> handleExchangeRateSuccess(it.data)
                        is UiState.Error -> handleExchangeRateError()
                        else -> Unit
                    }
                }
            }
        }
    }

}