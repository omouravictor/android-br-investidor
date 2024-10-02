package com.omouravictor.invest_view.presenter.wallet.save_asset

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.asset.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.asset.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetType
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.clearPileAndNavigateToStart
import com.omouravictor.invest_view.util.getErrorMessage
import com.omouravictor.invest_view.util.getLongValue
import com.omouravictor.invest_view.util.getMonetaryValueInDouble
import com.omouravictor.invest_view.util.getRoundedDouble
import com.omouravictor.invest_view.util.setEditTextCurrencyFormatMask
import com.omouravictor.invest_view.util.setEditTextLongNumberFormatMask
import com.omouravictor.invest_view.util.setupToolbarCenterText
import com.omouravictor.invest_view.util.setupVariation
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SaveAssetFragment : Fragment(R.layout.fragment_save_asset) {

    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var assetUiModel: AssetUiModel
    private lateinit var navController: NavController
    private val args by navArgs<SaveAssetFragmentArgs>()
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetUiModel = args.assetUiModel
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSaveAssetBinding.bind(view)
        setupToolbar()
        setupViews()
        observeSaveAssetUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walletViewModel.resetSaveAssetUiState()
    }

    private fun setupToolbar() {
        val activity = requireActivity()
        val assetType = assetUiModel.type

        activity.setupToolbarCenterText(getString(assetType.nameResId))

        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_info, menu)
                menu.findItem(R.id.infoMenuItem).icon?.setTint(ContextCompat.getColor(activity, assetType.colorResId))
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                showInfoBottomSheetDialog(assetType)
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setupViews() {
        binding.apply {
            val context = root.context
            etSymbol.setText(assetUiModel.getFormattedSymbol())
            etCurrentPrice.setText(LocaleUtil.getFormattedCurrencyValue(assetUiModel.currency, assetUiModel.price))
            incItemListAsset.color.setBackgroundColor(context.getColor(assetUiModel.type.colorResId))
            incItemListAsset.tvName.text = assetUiModel.name
            incItemListAsset.tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
        }
        setupAmountAndTotalInvested()
        setupButtons()
    }

    private fun setupAmountAndTotalInvested() {
        val previousIsAssetSearch =
            navController.previousBackStackEntry!!.destination.id == R.id.fragmentAssetSearch

        binding.ietAmount.apply {
            doAfterTextChanged { setupCurrentPosition() }
            setEditTextLongNumberFormatMask()
            val amount = if (previousIsAssetSearch) 0L else assetUiModel.amount
            setText(if (amount != 0L) LocaleUtil.getFormattedLong(amount) else "1")
        }

        binding.ietTotalInvested.apply {
            val currency = assetUiModel.currency
            doAfterTextChanged { setupCurrentPosition() }
            setEditTextCurrencyFormatMask(currency)
            hint = LocaleUtil.getFormattedCurrencyValue(currency, 0.0)
            val totalInvested = if (previousIsAssetSearch) 0.0 else assetUiModel.totalInvested
            setText(if (totalInvested != 0.0) LocaleUtil.getFormattedCurrencyValue(currency, totalInvested) else "")
        }
    }

    private fun showInfoBottomSheetDialog(assetType: AssetType) {
        val context = requireContext()
        with(BottomSheetDialog(context, R.style.AppOverlayBottomSheetDialogStyle)) {
            setContentView(R.layout.bottom_sheet_dialog_info)
            findViewById<ImageView>(R.id.ivTitle)!!.imageTintList =
                ContextCompat.getColorStateList(context, assetType.colorResId)
            findViewById<TextView>(R.id.tvTitle)!!.text = getString(assetType.nameResId)
            findViewById<TextView>(R.id.tvInfo)!!.text = getString(assetType.descriptionResId)
            show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupCurrentPosition() {
        binding.incItemListAsset.apply {
            val amount = binding.ietAmount.getLongValue()
            if (amount != 0L) {
                val currency = assetUiModel.currency
                val totalPrice = amount * assetUiModel.price
                val totalInvested = binding.ietTotalInvested.text.toString().getMonetaryValueInDouble()
                val yield = (totalPrice - totalInvested).getRoundedDouble()
                val yieldPercent = yield / totalInvested

                tvSymbolAmount.text = "${assetUiModel.getFormattedSymbol()} (${LocaleUtil.getFormattedLong(amount)})"
                tvTotalPrice.text = LocaleUtil.getFormattedCurrencyValue(currency, totalPrice)
                tvYield.setupVariation(currency, yield, yieldPercent)

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

    private fun setupButtons() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            setOnClickListener {
                assetUiModel.amount = binding.ietAmount.getLongValue()
                assetUiModel.totalInvested = binding.ietTotalInvested.text.toString().getMonetaryValueInDouble()
                walletViewModel.saveAsset(assetUiModel)
            }
        }
    }

    private fun handleSaveAssetLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.saveLayout.visibility = View.INVISIBLE
            binding.incProgressBar.root.visibility = View.VISIBLE
        } else {
            binding.saveLayout.visibility = View.VISIBLE
            binding.incProgressBar.root.visibility = View.GONE
        }
    }

    private fun handleSaveAssetSuccess(asset: AssetUiModel) {
        val previousBackStackEntry = navController.previousBackStackEntry!!

        when (previousBackStackEntry.destination.id) {
            R.id.fragmentAssetSearch -> {
                navController.clearPileAndNavigateToStart()
            }

            R.id.fragmentAssetDetail -> {
                previousBackStackEntry
                    .savedStateHandle[ConstantUtil.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL] = asset
                navController.popBackStack()
            }
        }
    }

    private fun handleSaveAssetError(e: Exception) {
        handleSaveAssetLoading(false)
        with(requireActivity()) { showErrorSnackBar(getErrorMessage(e)) }
    }

    private fun observeSaveAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.saveAssetUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleSaveAssetLoading(true)
                        is UiState.Success -> handleSaveAssetSuccess(it.data)
                        is UiState.Error -> handleSaveAssetError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

}