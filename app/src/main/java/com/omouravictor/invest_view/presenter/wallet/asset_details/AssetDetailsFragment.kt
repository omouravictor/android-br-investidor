package com.omouravictor.invest_view.presenter.wallet.asset_details

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.data.remote.model.asset_quote.GlobalQuote
import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.ConversionResultResponse
import com.omouravictor.invest_view.databinding.FragmentAssetDetailsBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.asset.AssetViewModel
import com.omouravictor.invest_view.presenter.wallet.currency_exchange_rates.CurrencyExchangeRatesViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAssetPrice
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalInvested
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.clearPileAndNavigateToStart
import com.omouravictor.invest_view.util.getGenericErrorMessage
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
    private val currencyExchangeRatesViewModel: CurrencyExchangeRatesViewModel by activityViewModels()
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetUiModel = args.assetUiModel
        navController = findNavController()
        assetViewModel.getQuote(assetUiModel.symbol)
        checkCurrencyForConversion()
    }

    private fun checkCurrencyForConversion() {
        val appCurrency = LocaleUtil.appCurrency.toString()
        val assetCurrency = assetUiModel.currency
        if (appCurrency != assetCurrency) {
            currencyExchangeRatesViewModel.convert(assetCurrency, appCurrency)
        }
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
            setPositiveButton(getString(R.string.yes)) { _, _ -> walletViewModel.deleteAsset(assetUiModel) }
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
        val appCurrency = LocaleUtil.appCurrency.toString()

        binding.apply {
            val context = root.context
            val assetCurrency = assetUiModel.currency

            if (appCurrency == assetCurrency) {
                trCurrency.visibility = View.GONE
            } else {
                trCurrency.visibility = View.VISIBLE
                tvCurrencyTittle.text = getString(R.string.showInLocalCurrency, appCurrency)
            }

            tvAssetType.text = getString(assetUiModel.type.nameResId)
            tvAssetType.backgroundTintList = getColorStateList(context, assetUiModel.type.colorResId)
            tvAssetCurrency.text = assetCurrency
            tvAssetCurrency.backgroundTintList =
                getColorStateList(context, AssetUtil.getCurrencyResColor(assetCurrency))
            tvName.text = assetUiModel.name
            tvPrice.text = assetUiModel.getFormattedAssetPrice()
            tvAmount.text = assetUiModel.getFormattedAmount()
            tvTotalInvested.text = assetUiModel.getFormattedTotalInvested()
            tvTotalPrice.text = assetUiModel.getFormattedTotalPrice()
            tvYield.setupYieldForAsset(assetUiModel)
            ivChangeReload.setOnClickListener { assetViewModel.getQuote(assetUiModel.symbol) }
            ivCurrencyReload.setOnClickListener { currencyExchangeRatesViewModel.convert(assetCurrency, appCurrency) }
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
        if (isLoading) {
            binding.incChangeShimmer.root.startShimmer()
            binding.incChangeShimmer.root.visibility = View.VISIBLE
            binding.tvChange.visibility = View.INVISIBLE
            binding.ivChangeReload.visibility = View.INVISIBLE
        } else {
            binding.incChangeShimmer.root.stopShimmer()
            binding.incChangeShimmer.root.visibility = View.INVISIBLE
            binding.tvChange.visibility = View.VISIBLE
            binding.ivChangeReload.visibility = View.INVISIBLE
        }
    }

    private fun handleQuoteSuccess(globalQuote: GlobalQuote) {
        handleQuoteLoading(false)
        val change = globalQuote.change
        val changePercent = globalQuote.changePercent.removeSuffix("%").toDoubleOrNull()
        binding.tvChange.setupVariation(
            assetUiModel.currency, change, changePercent?.div(100)
        )
    }

    private fun handleQuoteError() {
        handleQuoteLoading(false)
        binding.tvChange.visibility = View.INVISIBLE
        binding.ivChangeReload.visibility = View.VISIBLE
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
            binding.incProgressBar.root.visibility = View.VISIBLE
        } else {
            binding.mainLayout.visibility = View.VISIBLE
            binding.incProgressBar.root.visibility = View.GONE
        }
    }

    private fun handleDeleteAssetError(e: Exception) {
        handleDeleteAssetLoading(false)
        with(requireActivity()) { showErrorSnackBar(getGenericErrorMessage(e)) }
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
        if (isLoading) {
            binding.incCurrencyShimmer.root.startShimmer()
            binding.incCurrencyShimmer.root.visibility = View.VISIBLE
            binding.switchCurrency.visibility = View.INVISIBLE
            binding.ivCurrencyReload.visibility = View.INVISIBLE
        } else {
            binding.incCurrencyShimmer.root.stopShimmer()
            binding.incCurrencyShimmer.root.visibility = View.INVISIBLE
            binding.switchCurrency.visibility = View.VISIBLE
            binding.ivCurrencyReload.visibility = View.INVISIBLE
        }
    }

    private fun handleConversionResultSuccess(currencyExchangeRates: ConversionResultResponse) {
        handleConversionResultLoading(false)
        Toast.makeText(requireContext(), "$currencyExchangeRates", Toast.LENGTH_LONG).show()
    }

    private fun handleConversionResultError() {
        handleConversionResultLoading(false)
        binding.switchCurrency.visibility = View.INVISIBLE
        binding.ivCurrencyReload.visibility = View.VISIBLE
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