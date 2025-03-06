package com.omouravictor.br_investidor.presenter.wallet.save_asset

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
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.omouravictor.br_investidor.R
import com.omouravictor.br_investidor.databinding.FragmentSaveAssetBinding
import com.omouravictor.br_investidor.presenter.model.UiState
import com.omouravictor.br_investidor.presenter.user.UserViewModel
import com.omouravictor.br_investidor.presenter.wallet.WalletViewModel
import com.omouravictor.br_investidor.presenter.wallet.asset.AssetUiModel
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedSymbol
import com.omouravictor.br_investidor.presenter.wallet.asset_types.AssetType
import com.omouravictor.br_investidor.util.AppConstants.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL
import com.omouravictor.br_investidor.util.LocaleUtil
import com.omouravictor.br_investidor.util.clearPileAndNavigateToStart
import com.omouravictor.br_investidor.util.getErrorMessage
import com.omouravictor.br_investidor.util.getLongValue
import com.omouravictor.br_investidor.util.getMonetaryValueInDouble
import com.omouravictor.br_investidor.util.getRoundedDouble
import com.omouravictor.br_investidor.util.hideKeyboard
import com.omouravictor.br_investidor.util.setEditTextCurrencyFormatMask
import com.omouravictor.br_investidor.util.setEditTextLongNumberFormatMask
import com.omouravictor.br_investidor.util.setupToolbarCenterText
import com.omouravictor.br_investidor.util.setupVariation
import com.omouravictor.br_investidor.util.showErrorSnackBar
import com.omouravictor.br_investidor.util.showSuccessSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SaveAssetFragment : Fragment(R.layout.fragment_save_asset) {

    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var assetUiModel: AssetUiModel
    private lateinit var activity: FragmentActivity
    private lateinit var navController: NavController
    private val args by navArgs<SaveAssetFragmentArgs>()
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
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
        activity.hideKeyboard(binding.root)
        walletViewModel.resetSaveAssetUiState()
    }

    private fun setupToolbar() {
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
        with(BottomSheetDialog(context, R.style.Theme_BrInvestidor_OverlayBottomSheetDialog)) {
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
                walletViewModel.saveAsset(assetUiModel, userViewModel.user.value.uid)
            }
        }
    }

    private fun handleSaveAssetLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                saveLayout.visibility = View.INVISIBLE
                incBtnSave.root.visibility = View.INVISIBLE
                incProgressBar.root.visibility = View.VISIBLE
            } else {
                saveLayout.visibility = View.VISIBLE
                incBtnSave.root.visibility = View.VISIBLE
                incProgressBar.root.visibility = View.GONE
            }
        }
    }

    private fun handleSaveAssetSuccess(asset: AssetUiModel) {
        val previousBackStackEntry = navController.previousBackStackEntry!!

        when (previousBackStackEntry.destination.id) {
            R.id.fragmentAssetSearch -> navController.clearPileAndNavigateToStart()

            R.id.fragmentAssetDetail -> {
                previousBackStackEntry.savedStateHandle[SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL] = asset
                handleSaveAssetLoading(false)

                activity.showSuccessSnackBar(
                    getString(R.string.assetUpdatedSuccessfully),
                    anchorResView = binding.incBtnSave.root.id
                )

                navController.popBackStack()
            }
        }
    }

    private fun handleSaveAssetError(e: Exception) {
        handleSaveAssetLoading(false)
        activity.showErrorSnackBar(activity.getErrorMessage(e))
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