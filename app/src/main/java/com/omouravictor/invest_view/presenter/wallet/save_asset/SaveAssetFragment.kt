package com.omouravictor.invest_view.presenter.wallet.save_asset

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
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypes
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbolAndAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedYield
import com.omouravictor.invest_view.presenter.wallet.model.getYield
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.clearPileAndNavigateToStart
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.getOnlyNumbers
import com.omouravictor.invest_view.util.setEditTextCurrencyFormatMask
import com.omouravictor.invest_view.util.setEditTextLongNumberFormatMask
import com.omouravictor.invest_view.util.setupToolbarCenterText
import com.omouravictor.invest_view.util.setupYieldTextColor
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SaveAssetFragment : Fragment() {

    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var assetUiModelArg: AssetUiModel
    private val walletViewModel: WalletViewModel by activityViewModels()

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
        observeAssetUiState()
    }

    override fun onStop() {
        super.onStop()
        walletViewModel.resetUiState()
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

        binding.etSymbol.setText(assetUiModelArg.getFormattedSymbol())
        binding.etLocation.setText(assetUiModelArg.region)
        binding.incItemListAsset.color.setBackgroundColor(context.getColor(assetUiModelArg.assetType.colorResId))
        binding.incItemListAsset.tvSymbolAmount.text = assetUiModelArg.getFormattedSymbolAndAmount()
        binding.incItemListAsset.tvName.text = assetUiModelArg.name
        binding.incItemListAsset.tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
        setupAmountAndTotalInvested()
    }

    private fun setupAmountAndTotalInvested() {
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

    private fun updateCurrentPosition() {
        binding.incItemListAsset.apply {
            val amount = getAmount()
            if (amount != 0L) {
                assetUiModelArg.amount = amount
                assetUiModelArg.totalInvested = getTotalInvested()

                tvSymbolAmount.text = assetUiModelArg.getFormattedSymbolAndAmount()
                tvTotalPrice.text = assetUiModelArg.getFormattedTotalPrice()
                val yield = assetUiModelArg.getYield()
                tvYield.text = assetUiModelArg.getFormattedYield(yield)
                setupYieldTextColor(yield)

                tableLayout.visibility = View.VISIBLE
                tvInfoMessage.visibility = View.INVISIBLE
                binding.incBtnSave.root.isEnabled = true

            } else {
                tableLayout.visibility = View.INVISIBLE
                tvInfoMessage.visibility = View.VISIBLE
                binding.incBtnSave.root.isEnabled = false
            }
        }
    }

    private fun observeAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.assetUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> {
                            binding.saveLayout.visibility = View.INVISIBLE
                            binding.incProgressBar.root.visibility = View.VISIBLE
                        }

                        is UiState.Success -> {
                            val navController = findNavController()
                            val previousBackStackEntry = navController.previousBackStackEntry
                            val previousDestinationId = previousBackStackEntry?.destination?.id ?: -1
                            if (previousDestinationId == R.id.fragmentAssetSearch) {
                                navController.clearPileAndNavigateToStart()
                            } else if (previousDestinationId == R.id.fragmentAssetDetail) {
                                val updatedAssetUiModel = it.data
                                previousBackStackEntry?.savedStateHandle?.set(
                                    ConstantUtil.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL, updatedAssetUiModel
                                )
                                navController.popBackStack()
                            }
                        }

                        is UiState.Error -> {
                            val activity = requireActivity()
                            binding.saveLayout.visibility = View.VISIBLE
                            binding.incProgressBar.root.visibility = View.GONE
                            activity.showErrorSnackBar(activity.getGenericErrorMessage(it.e))
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            setOnClickListener {
                walletViewModel.saveAsset(assetUiModelArg)
            }
        }
    }

}