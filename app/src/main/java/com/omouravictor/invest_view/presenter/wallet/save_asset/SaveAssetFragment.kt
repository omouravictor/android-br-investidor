package com.omouravictor.invest_view.presenter.wallet.save_asset

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypes
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.calculateAndSetupVariationLayout
import com.omouravictor.invest_view.util.clearPileAndNavigateToStart
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.getOnlyNumbers
import com.omouravictor.invest_view.util.setEditTextCurrencyFormatMask
import com.omouravictor.invest_view.util.setEditTextLongNumberFormatMask
import com.omouravictor.invest_view.util.setupToolbarCenterText
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SaveAssetFragment : Fragment() {

    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var assetUiModelArg: AssetUiModel
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val saveViewModel: SaveViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetUiModelArg = SaveAssetFragmentArgs.fromBundle(requireArguments()).assetUiModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupViews()
        setupButtons()
        observeWalletUiState()
    }

    override fun onStop() {
        super.onStop()
        saveViewModel.resetUiStateFlow()
    }

    private fun setupToolbar() {
        val activity = requireActivity()
        val assetType = assetUiModelArg.assetType

        activity.setupToolbarCenterText(getString(assetType.nameResId))

        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_info, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                showInfoBottomSheetDialog(assetType)
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setupViews() {
        val context = requireContext()

        binding.incItemListAsset.color.setBackgroundColor(context.getColor(assetUiModelArg.assetType.colorResId))
        binding.etSymbol.setText(assetUiModelArg.getFormattedSymbol())
        binding.etLocation.setText(assetUiModelArg.region)
        setupAmountAndTotalInvestedViews()
    }

    private fun setupAmountAndTotalInvestedViews() {
        val currency = assetUiModelArg.currency
        val ietAmount = binding.ietAmount
        val ietTotalInvested = binding.ietTotalInvested

        ietAmount.doAfterTextChanged { updateCurrentPosition() }
        ietTotalInvested.doAfterTextChanged { updateCurrentPosition() }
        ietAmount.setEditTextLongNumberFormatMask()
        ietTotalInvested.setEditTextCurrencyFormatMask(currency)

        val amount = assetUiModelArg.amount
        ietAmount.setText(if (amount != 0L) LocaleUtil.getFormattedValueForLongNumber(amount) else "1")

        ietTotalInvested.hint = LocaleUtil.getFormattedCurrencyValue(currency, 0.0)
        val totalInvested = assetUiModelArg.totalInvested
        ietTotalInvested.setText(
            if (totalInvested != 0.0) LocaleUtil.getFormattedCurrencyValue(currency, totalInvested) else ""
        )
    }

    private fun showInfoBottomSheetDialog(assetTypes: AssetTypes) {
        val context = requireContext()
        with(BottomSheetDialog(context, R.style.Theme_App_OverlayBottomSheetDialog)) {
            setContentView(R.layout.bottom_sheet_dialog_info)
            findViewById<ImageView>(R.id.ivTitle)!!.imageTintList =
                ContextCompat.getColorStateList(context, assetTypes.colorResId)
            findViewById<TextView>(R.id.tvTitle)!!.text = getString(assetTypes.nameResId)
            findViewById<TextView>(R.id.tvInfo)!!.text = getString(assetTypes.descriptionResId)
            show()
        }
    }

    private fun getAmount(): Long {
        val amountText = binding.ietAmount.text.toString()
        return if (amountText.isNotEmpty()) amountText.getOnlyNumbers().toLong() else 0
    }

    private fun getTotalInvested(): Double {
        val totalInvestedText = binding.ietTotalInvested.text.toString()
        return if (totalInvestedText.isNotEmpty()) totalInvestedText.getOnlyNumbers().toDouble() / 100 else 0.0
    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrentPosition() {
        binding.incItemListAsset.apply {
            if (getAmount() != 0L) {
                val priceCurrentPosition = assetUiModelArg.price * getAmount()
                val totalInvested = getTotalInvested()
                val currency = assetUiModelArg.currency

                tvSymbol.text = binding.etSymbol.text.toString()
                tvAmount.text = "(${binding.ietAmount.text.toString()})"
                tvName.text = assetUiModelArg.name
                tvTotalPrice.text = LocaleUtil.getFormattedCurrencyValue(currency, priceCurrentPosition)
                layoutAssetInfo.visibility = View.VISIBLE
                tvInfoMessage.visibility = View.INVISIBLE
                binding.incBtnSave.root.isEnabled = true
                incLayoutVariation.calculateAndSetupVariationLayout(
                    textSize = 12f,
                    currency = currency,
                    reference = priceCurrentPosition,
                    totalReference = totalInvested
                )

            } else {
                tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
                tvInfoMessage.visibility = View.VISIBLE
                layoutAssetInfo.visibility = View.INVISIBLE
                binding.incBtnSave.root.isEnabled = false
            }
        }
    }

    private fun observeWalletUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                saveViewModel.uiStateFlow.collectLatest {
                    when (it) {
                        is UiState.Initial -> Unit
                        is UiState.Loading -> {
                            binding.layout.visibility = View.INVISIBLE
                            binding.incProgressBar.root.visibility = View.VISIBLE
                        }

                        is UiState.Success -> {
                            val navController = findNavController()
                            val previousBackStackEntry = navController.previousBackStackEntry
                            val previousDestinationId = previousBackStackEntry?.destination?.id ?: -1
                            if (previousDestinationId == R.id.fragmentAssetSearch) {
                                walletViewModel.addAsset(it.data)
                                navController.clearPileAndNavigateToStart()
                            } else if (previousDestinationId == R.id.fragmentAssetDetail) {
                                val assetUiModel = it.data
                                walletViewModel.updateAsset(assetUiModel)
                                previousBackStackEntry?.savedStateHandle?.set(
                                    ConstantUtil.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL, assetUiModel
                                )
                                navController.popBackStack()
                            }
                        }

                        is UiState.Error -> {
                            val activity = requireActivity()
                            binding.layout.visibility = View.VISIBLE
                            binding.incProgressBar.root.visibility = View.GONE
                            activity.showErrorSnackBar(activity.getGenericErrorMessage(it.e))
                        }
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            setOnClickListener {
                assetUiModelArg.amount = getAmount()
                assetUiModelArg.totalInvested = getTotalInvested()
                saveViewModel.saveAsset(assetUiModelArg)
            }
        }
    }

}