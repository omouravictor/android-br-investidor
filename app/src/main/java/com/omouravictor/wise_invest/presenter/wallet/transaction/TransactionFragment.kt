package com.omouravictor.wise_invest.presenter.wallet.transaction

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
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
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.FragmentTransactionBinding
import com.omouravictor.wise_invest.presenter.model.UiState
import com.omouravictor.wise_invest.presenter.user.UserViewModel
import com.omouravictor.wise_invest.presenter.wallet.WalletViewModel
import com.omouravictor.wise_invest.presenter.wallet.asset.AssetUiModel
import com.omouravictor.wise_invest.presenter.wallet.asset.getFormattedSymbol
import com.omouravictor.wise_invest.presenter.wallet.asset.getFormattedSymbolAndAmount
import com.omouravictor.wise_invest.presenter.wallet.asset.getFormattedTotalPrice
import com.omouravictor.wise_invest.util.AppConstants.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL
import com.omouravictor.wise_invest.util.LocaleUtil
import com.omouravictor.wise_invest.util.clearPileAndNavigateToStart
import com.omouravictor.wise_invest.util.getErrorMessage
import com.omouravictor.wise_invest.util.getLongValue
import com.omouravictor.wise_invest.util.getMonetaryValueInDouble
import com.omouravictor.wise_invest.util.getRoundedDouble
import com.omouravictor.wise_invest.util.setEditTextCurrencyFormatMask
import com.omouravictor.wise_invest.util.setEditTextLongNumberFormatMask
import com.omouravictor.wise_invest.util.setupToolbarCenterText
import com.omouravictor.wise_invest.util.setupVariation
import com.omouravictor.wise_invest.util.setupYieldForAsset
import com.omouravictor.wise_invest.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionFragment : Fragment(R.layout.fragment_transaction) {

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
        activity.setupToolbarCenterText(getString(R.string.newTransaction))
        setupViews()
        observeSaveAssetUiState()
        observeDeleteAssetUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walletViewModel.resetSaveAssetUiState()
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
            incNewPosition.tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
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
                tvBuy.background = AppCompatResources.getDrawable(it.context, R.drawable.rectangle_green_stroke)
                tvSale.typeface = null
                tvSale.background = AppCompatResources.getDrawable(it.context, R.drawable.rectangle_gray_stroke)
                updateNewPosition()
            }
        }
    }

    private fun setupTextViewSale() {
        binding.apply {
            tvSale.setOnClickListener {
                transaction = Transaction.SALE
                tvSale.typeface = Typeface.DEFAULT_BOLD
                tvSale.background = AppCompatResources.getDrawable(it.context, R.drawable.rectangle_green_stroke)
                tvBuy.typeface = null
                tvBuy.background = AppCompatResources.getDrawable(it.context, R.drawable.rectangle_gray_stroke)
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
            tvSymbolAmount.text = "${assetUiModel.getFormattedSymbol()} (${LocaleUtil.getFormattedLong(updatedAmount)})"
            tvTotalPrice.text = LocaleUtil.getFormattedCurrencyValue(currency, updatedTotalPrice)
            tvYield.setupVariation(currency, updatedYield, updatedYieldPercent)

            tableLayout.visibility = View.VISIBLE
            tvInfoMessage.visibility = View.INVISIBLE
        }

        binding.incBtnSave.root.isEnabled = true
    }

    private fun updateNewPosition() {
        val amount = binding.ietTransactionAmount.getLongValue()
        val valuePerUnit = binding.ietTransactionValuePerUnit.text.toString().getMonetaryValueInDouble()

        if (amount == 0L || valuePerUnit == 0.0) {
            showInitialUpdatedPositionLayout()
            return
        }

        val isBuy = transaction == Transaction.BUY
        val updatedAmount = if (isBuy) assetUiModel.amount + amount else assetUiModel.amount - amount
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
            showUpdatedPositionLayout(updatedAmount, updatedTotalPrice, updatedYield, updatedYieldPercent)
        }
    }

    private fun showAlertDialogForSale(context: Context) {
        val icon = AppCompatResources.getDrawable(context, R.drawable.ic_info)?.apply {
            setTint(context.getColor(assetUiModel.type.colorResId))
        }

        AlertDialog.Builder(context).apply {
            setTitle(assetUiModel.getFormattedSymbol())
            setMessage(getString(R.string.saleAssetAlertMessage))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                walletViewModel.deleteAsset(
                    assetUiModel,
                    userViewModel.user.value.uid
                )
            }
            setNegativeButton(getString(R.string.not)) { dialog, _ -> dialog.dismiss() }
            setIcon(icon)
        }.show()
    }

    private fun setupSaveButton() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            setOnClickListener {
                val isBuy = transaction == Transaction.BUY
                val amount = binding.ietTransactionAmount.getLongValue()
                val totalInvested = binding.ietTransactionValuePerUnit.text.toString().getMonetaryValueInDouble() * amount
                val updatedAmount = if (isBuy) assetUiModel.amount + amount else assetUiModel.amount - amount

                if (updatedAmount < 1) {
                    showAlertDialogForSale(context)
                } else {
                    assetUiModel.amount = updatedAmount
                    assetUiModel.totalInvested = if (isBuy) {
                        assetUiModel.totalInvested + totalInvested
                    } else {
                        assetUiModel.totalInvested - totalInvested
                    }
                    walletViewModel.saveAsset(assetUiModel, userViewModel.user.value.uid)
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
        navController.popBackStack()
    }

    private fun handleError(e: Exception) {
        handleLoading(false)
        activity.showErrorSnackBar(activity.getErrorMessage(e))
    }

    private fun observeSaveAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.saveAssetUiState.collectLatest {
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