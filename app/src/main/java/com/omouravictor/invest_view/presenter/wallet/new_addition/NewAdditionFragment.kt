package com.omouravictor.invest_view.presenter.wallet.new_addition

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentNewAdditionBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbolAndAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getTotalPrice
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil
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

class NewAdditionFragment : Fragment(R.layout.fragment_new_addition) {

    private lateinit var binding: FragmentNewAdditionBinding
    private lateinit var assetUiModel: AssetUiModel
    private val args by navArgs<NewAdditionFragmentArgs>()
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assetUiModel = args.assetUiModel
        binding = FragmentNewAdditionBinding.bind(view)
        requireActivity().setupToolbarCenterText(getString(R.string.newAddition))
        setupViews()
        setupButtons()
        observeSaveAssetUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walletViewModel.resetSaveAssetUiState()
    }

    private fun setupViews() {
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
        setupAmountAndValuePerUnit()
        showInitialUpdatedPositionLayout()
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

    private fun showInitialUpdatedPositionLayout() {
        binding.incNewPosition.apply {
            tableLayout.visibility = View.INVISIBLE
            tvInfoMessage.visibility = View.VISIBLE
            binding.incBtnSave.root.isEnabled = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateNewPosition() {
        binding.incNewPosition.apply {
            val additionAmount = binding.ietAmount.getLongValue()
            val valuePerUnit = binding.ietValuePerUnit.getMonetaryValueDouble()
            if (additionAmount != 0L && valuePerUnit != 0.0) {
                val currency = assetUiModel.currency
                val updatedAmount = additionAmount + assetUiModel.amount
                val updatedTotalPrice = (assetUiModel.price * additionAmount) + assetUiModel.getTotalPrice()
                val updatedTotalInvested = (valuePerUnit * additionAmount) + assetUiModel.totalInvested
                val updatedYield = (updatedTotalPrice - updatedTotalInvested).getRoundedDouble()
                val updatedYieldPercent = updatedYield / updatedTotalInvested

                tvSymbolAmount.text =
                    "${assetUiModel.getFormattedSymbol()} (${LocaleUtil.getFormattedLong(updatedAmount)})"
                tvTotalPrice.text = LocaleUtil.getFormattedCurrencyValue(currency, updatedTotalPrice)
                tvYield.setupVariation(currency, updatedYield, updatedYieldPercent)

                tableLayout.visibility = View.VISIBLE
                tvInfoMessage.visibility = View.INVISIBLE
                binding.incBtnSave.root.isEnabled = true

            } else
                showInitialUpdatedPositionLayout()
        }
    }

    private fun setupButtons() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            setOnClickListener {
                val additionAmount = binding.ietAmount.getLongValue()
                val additionTotalInvested = binding.ietValuePerUnit.getMonetaryValueDouble() * additionAmount
                assetUiModel.amount += additionAmount
                assetUiModel.totalInvested += additionTotalInvested
                walletViewModel.saveAsset(assetUiModel)
            }
        }
    }

    private fun observeSaveAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.saveAssetUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> {
                            binding.newAdditionLayout.visibility = View.INVISIBLE
                            binding.incProgressBar.root.visibility = View.VISIBLE
                        }

                        is UiState.Success -> {
                            val navController = findNavController()
                            val previousBackStackEntry = navController.previousBackStackEntry
                            val updatedAssetUiModel = it.data
                            previousBackStackEntry?.savedStateHandle?.set(
                                ConstantUtil.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL, updatedAssetUiModel
                            )
                            navController.popBackStack()
                        }

                        is UiState.Error -> {
                            val activity = requireActivity()
                            binding.newAdditionLayout.visibility = View.VISIBLE
                            binding.incProgressBar.root.visibility = View.GONE
                            activity.showErrorSnackBar(activity.getGenericErrorMessage(it.e))
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

}