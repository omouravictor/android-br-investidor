package com.omouravictor.br_investidor.presenter.wallet.transaction

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
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
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.br_investidor.R
import com.omouravictor.br_investidor.R.*
import com.omouravictor.br_investidor.databinding.FragmentTransactionBinding
import com.omouravictor.br_investidor.presenter.model.UiState
import com.omouravictor.br_investidor.presenter.user.UserViewModel
import com.omouravictor.br_investidor.presenter.wallet.WalletViewModel
import com.omouravictor.br_investidor.presenter.wallet.asset.AssetUiModel
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedSymbol
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedSymbolAndAmount
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedTotalInvested
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedTotalPrice
import com.omouravictor.br_investidor.util.AppConstants.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL
import com.omouravictor.br_investidor.util.LocaleUtil
import com.omouravictor.br_investidor.util.clearPileAndNavigateToStart
import com.omouravictor.br_investidor.util.getErrorMessage
import com.omouravictor.br_investidor.util.getLongValue
import com.omouravictor.br_investidor.util.getMonetaryValueInDouble
import com.omouravictor.br_investidor.util.getRoundedDouble
import com.omouravictor.br_investidor.util.setEditTextCurrencyFormatMask
import com.omouravictor.br_investidor.util.setEditTextLongNumberFormatMask
import com.omouravictor.br_investidor.util.setupToolbarCenterText
import com.omouravictor.br_investidor.util.setupVariation
import com.omouravictor.br_investidor.util.setupYieldForAsset
import com.omouravictor.br_investidor.util.showErrorSnackBar
import com.omouravictor.br_investidor.util.showSuccessSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionFragment : Fragment(layout.fragment_transaction) {

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var activity: FragmentActivity
    private lateinit var assetUiModel: AssetUiModel
    private lateinit var navController: NavController
    private val args by navArgs<TransactionFragmentArgs>()
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var transaction = Transaction.BUY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
        assetUiModel = args.assetUiModel
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionBinding.bind(view)
        setupToolbar()
        setupViews()
        observeUpdateAssetUiState()
        observeDeleteAssetUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walletViewModel.resetUpdateAssetUiState()
    }

    private fun setupToolbar() {
        activity.setupToolbarCenterText(getString(string.newTransaction))

        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_info, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                showInfoBottomSheetDialog()
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun showInfoBottomSheetDialog() {
        val context = requireContext()
        with(BottomSheetDialog(context, style.Theme_BrInvestidor_OverlayBottomSheetDialog)) {
            setContentView(layout.bottom_sheet_dialog_asset_info)
            findViewById<View>(R.id.vColorAssetInfo)!!.backgroundTintList =
                ContextCompat.getColorStateList(context, assetUiModel.type.colorResId)
            findViewById<TextView>(R.id.tvTitleAssetInfo)!!.text =
                assetUiModel.getFormattedSymbolAndAmount()
            findViewById<TextView>(R.id.tvCurrentPriceAssetInfo)!!.text =
                LocaleUtil.getFormattedCurrencyValue(assetUiModel.currency, assetUiModel.price)
            findViewById<TextView>(R.id.tvCurrentPositionAssetInfo)!!.text =
                assetUiModel.getFormattedTotalPrice()
            findViewById<TextView>(R.id.tvTotalInvestedAssetInfo)!!.text =
                assetUiModel.getFormattedTotalInvested()
            show()
        }
    }

    private fun setupViews() {
        setupIncCurrentPositionAndIncNewPosition()
        setupAmountAndValuePerUnit()
        setupSaveButton()
        setupTextViewBuy()
        setupTextViewSale()
        showInitialUpdatedPositionLayout()
    }

    private fun setupIncCurrentPositionAndIncNewPosition() {
        binding.apply {
            val context = root.context
            val color = context.getColor(assetUiModel.type.colorResId)
            val formattedSymbolAndAmount = assetUiModel.getFormattedSymbolAndAmount()

            incCurrentPosition.color.setBackgroundColor(color)
            incCurrentPosition.tvSymbolAmount.text = formattedSymbolAndAmount
            incCurrentPosition.tvName.text = assetUiModel.name
            incCurrentPosition.tvTotalPrice.text = assetUiModel.getFormattedTotalPrice()
            incCurrentPosition.tvYield.setupYieldForAsset(assetUiModel)

            incNewPosition.color.setBackgroundColor(color)
            incNewPosition.tvSymbolAmount.text = formattedSymbolAndAmount
            incNewPosition.tvName.text = assetUiModel.name
            incNewPosition.tvInfoMessage.hint = getString(string.fillTheFieldsToView)
        }
    }

    private fun setupAmountAndValuePerUnit() {
        binding.ietTransactionAmount.apply {
            doAfterTextChanged { updateNewPosition() }
            setEditTextLongNumberFormatMask()
            hint = LocaleUtil.getFormattedLong(0)
        }

        binding.ietTransactionValuePerUnit.apply {
            val currency = assetUiModel.currency
            doAfterTextChanged { updateNewPosition() }
            setEditTextCurrencyFormatMask(currency)
            hint = LocaleUtil.getFormattedCurrencyValue(currency, 0.0)
        }
    }

    private fun setupTextViewBuy() {
        binding.apply {
            tvBuy.setOnClickListener {
                transaction = Transaction.BUY
                tvBuy.typeface = Typeface.DEFAULT_BOLD
                tvBuy.background =
                    AppCompatResources.getDrawable(it.context, drawable.rectangle_green_stroke)
                tvSale.typeface = null
                tvSale.background =
                    AppCompatResources.getDrawable(it.context, drawable.rectangle_gray_stroke)
                updateNewPosition()
            }
        }
    }

    private fun setupTextViewSale() {
        binding.apply {
            tvSale.setOnClickListener {
                transaction = Transaction.SALE
                tvSale.typeface = Typeface.DEFAULT_BOLD
                tvSale.background =
                    AppCompatResources.getDrawable(it.context, drawable.rectangle_green_stroke)
                tvBuy.typeface = null
                tvBuy.background =
                    AppCompatResources.getDrawable(it.context, drawable.rectangle_gray_stroke)
                updateNewPosition()
            }
        }
    }

    private fun showInitialUpdatedPositionLayout() {
        binding.incNewPosition.apply {
            tableLayout.visibility = View.INVISIBLE
            tvInfoMessage.visibility = View.VISIBLE
            binding.incBtnSave.root.isEnabled = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showUpdatedPositionLayout(
        updatedAmount: Long,
        updatedTotalPrice: Double,
        updatedYield: Double,
        updatedYieldPercent: Double
    ) {
        val currency = assetUiModel.currency
        binding.incNewPosition.apply {
            tvSymbolAmount.text =
                "${assetUiModel.getFormattedSymbol()} (${LocaleUtil.getFormattedLong(updatedAmount)})"
            tvTotalPrice.text = LocaleUtil.getFormattedCurrencyValue(currency, updatedTotalPrice)
            tvYield.setupVariation(currency, updatedYield, updatedYieldPercent)

            tableLayout.visibility = View.VISIBLE
            tvInfoMessage.visibility = View.INVISIBLE
        }

        binding.incBtnSave.root.isEnabled = true
    }

    private fun updateNewPosition() {
        val amount = binding.ietTransactionAmount.getLongValue()
        val valuePerUnit =
            binding.ietTransactionValuePerUnit.text.toString().getMonetaryValueInDouble()

        if (amount == 0L || valuePerUnit == 0.0) {
            showInitialUpdatedPositionLayout()
            return
        }

        val isBuy = transaction == Transaction.BUY
        val updatedAmount =
            if (isBuy) assetUiModel.amount + amount else assetUiModel.amount - amount
        val updatedTotalPrice = assetUiModel.price * updatedAmount
        val updatedTotalInvested = if (isBuy) {
            assetUiModel.totalInvested + (valuePerUnit * amount)
        } else {
            assetUiModel.totalInvested - (valuePerUnit * amount)
        }
        val updatedYield = (updatedTotalPrice - updatedTotalInvested).getRoundedDouble()
        var updatedYieldPercent = updatedYield / updatedTotalInvested

        if (updatedAmount < 1) {
            updatedYieldPercent = updatedYield / assetUiModel.totalInvested
            showUpdatedPositionLayout(0, 0.0, updatedYield, updatedYieldPercent)
        } else {
            showUpdatedPositionLayout(
                updatedAmount,
                updatedTotalPrice,
                updatedYield,
                updatedYieldPercent
            )
        }
    }

    private fun showAlertDialogForSale(context: Context) {
        val icon = AppCompatResources.getDrawable(context, drawable.ic_info)?.apply {
            setTint(context.getColor(assetUiModel.type.colorResId))
        }

        AlertDialog.Builder(context).apply {
            setTitle(assetUiModel.getFormattedSymbol())
            setMessage(getString(string.saleAssetAlertMessage))
            setPositiveButton(getString(string.yes)) { _, _ ->
                walletViewModel.deleteAsset(
                    assetUiModel,
                    userViewModel.user.value.uid
                )
            }
            setNegativeButton(getString(string.not)) { dialog, _ -> dialog.dismiss() }
            setIcon(icon)
        }.show()
    }

    private fun setupSaveButton() {
        binding.incBtnSave.root.apply {
            text = getString(string.save)
            setOnClickListener {
                val isBuy = transaction == Transaction.BUY
                val amount = binding.ietTransactionAmount.getLongValue()
                val totalInvested =
                    binding.ietTransactionValuePerUnit.text.toString()
                        .getMonetaryValueInDouble() * amount
                val updatedAmount =
                    if (isBuy) assetUiModel.amount + amount else assetUiModel.amount - amount

                if (updatedAmount < 1) {
                    showAlertDialogForSale(context)
                } else {
                    assetUiModel.amount = updatedAmount
                    assetUiModel.totalInvested = if (isBuy) {
                        assetUiModel.totalInvested + totalInvested
                    } else {
                        assetUiModel.totalInvested - totalInvested
                    }
                    walletViewModel.updateAsset(assetUiModel, userViewModel.user.value.uid)
                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                mainLayout.visibility = View.INVISIBLE
                incBtnSave.root.visibility = View.INVISIBLE
                incProgressBar.root.visibility = View.VISIBLE
            } else {
                mainLayout.visibility = View.VISIBLE
                incBtnSave.root.visibility = View.VISIBLE
                incProgressBar.root.visibility = View.GONE
            }
        }
    }

    private fun handleSaveAssetSuccess(asset: AssetUiModel) {
        navController
            .previousBackStackEntry!!
            .savedStateHandle[SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL] = asset

        activity.showSuccessSnackBar(
            message = getString(string.transactionSuccessfully),
            duration = Snackbar.LENGTH_SHORT,
            anchorResView = binding.incBtnSave.root.id
        )

        navController.popBackStack()
    }

    private fun handleError(e: Exception) {
        handleLoading(false)
        activity.showErrorSnackBar(activity.getErrorMessage(e))
    }

    private fun observeUpdateAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.updateAssetUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleLoading(true)
                        is UiState.Success -> handleSaveAssetSuccess(it.data)
                        is UiState.Error -> handleError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeDeleteAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.deleteAssetUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleLoading(true)
                        is UiState.Success -> navController.clearPileAndNavigateToStart()
                        is UiState.Error -> handleError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

}