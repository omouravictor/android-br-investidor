package com.omouravictor.invest_view.presenter.wallet.transaction

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentTransactionBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.Transaction
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbolAndAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.clearPileAndNavigateToStart
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.getLongValue
import com.omouravictor.invest_view.util.getMonetaryValueDouble
import com.omouravictor.invest_view.util.getRoundedDouble
import com.omouravictor.invest_view.util.setEditTextCurrencyFormatMask
import com.omouravictor.invest_view.util.setEditTextLongNumberFormatMask
import com.omouravictor.invest_view.util.setupToolbarCenterText
import com.omouravictor.invest_view.util.setupVariation
import com.omouravictor.invest_view.util.setupYieldForAsset
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionFragment : Fragment(R.layout.fragment_transaction) {

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var assetUiModel: AssetUiModel
    private lateinit var navController: NavController
    private val args by navArgs<TransactionFragmentArgs>()
    private val walletViewModel: WalletViewModel by activityViewModels()
    private var transaction = Transaction.BUY

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assetUiModel = args.assetUiModel
        navController = findNavController()
        binding = FragmentTransactionBinding.bind(view)
        requireActivity().setupToolbarCenterText(getString(R.string.newTransaction))
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
        binding.ietAmount.apply {
            doAfterTextChanged { updateNewPosition() }
            setEditTextLongNumberFormatMask()
            hint = LocaleUtil.getFormattedLong(0)
        }

        binding.ietValuePerUnit.apply {
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
        val amount = binding.ietAmount.getLongValue()
        val valuePerUnit = binding.ietValuePerUnit.getMonetaryValueDouble()

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
            setPositiveButton(getString(R.string.yes)) { _, _ -> walletViewModel.deleteAsset(assetUiModel) }
            setNegativeButton(getString(R.string.not)) { dialog, _ -> dialog.dismiss() }
            setIcon(icon)
        }.show()
    }

    private fun setupSaveButton() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            setOnClickListener {
                val isBuy = transaction == Transaction.BUY
                val amount = binding.ietAmount.getLongValue()
                val totalInvested = binding.ietValuePerUnit.getMonetaryValueDouble() * amount
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
                    walletViewModel.saveAsset(assetUiModel)
                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mainLayout.visibility = View.INVISIBLE
            binding.incProgressBar.root.visibility = View.VISIBLE
        } else {
            binding.mainLayout.visibility = View.VISIBLE
            binding.incProgressBar.root.visibility = View.GONE
        }
    }

    private fun handleSaveAssetSuccess(asset: AssetUiModel) {
        navController
            .previousBackStackEntry!!
            .savedStateHandle[ConstantUtil.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL] = asset
        navController.popBackStack()
    }

    private fun handleError(e: Exception) {
        handleLoading(false)
        with(requireActivity()) { showErrorSnackBar(getGenericErrorMessage(e)) }
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