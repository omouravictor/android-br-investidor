package com.omouravictor.invest_view.presenter.wallet.new_addition

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentNewAdditionBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbolAndAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedYield
import com.omouravictor.invest_view.presenter.wallet.model.getTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getYield
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.getOnlyNumbers
import com.omouravictor.invest_view.util.getRoundedDouble
import com.omouravictor.invest_view.util.setEditTextCurrencyFormatMask
import com.omouravictor.invest_view.util.setEditTextLongNumberFormatMask
import com.omouravictor.invest_view.util.setupToolbarCenterText
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewAdditionFragment : Fragment() {

    private lateinit var binding: FragmentNewAdditionBinding
    private lateinit var assetUiModelArg: AssetUiModel
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetUiModelArg = NewAdditionFragmentArgs.fromBundle(requireArguments()).assetUiModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewAdditionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setupToolbarCenterText(getString(R.string.newAddition))
        setupViews()
        setupButtons()
        observeAssetUiState()
    }

    override fun onStop() {
        super.onStop()
        walletViewModel.resetAssetUiState()
    }

    private fun setupViews() {
        binding.apply {
            val context = root.context
            val color = context.getColor(assetUiModelArg.assetType.colorResId)
            val formattedSymbolAndAmount = assetUiModelArg.getFormattedSymbolAndAmount()

            incCurrentPosition.color.setBackgroundColor(color)
            incCurrentPosition.tvSymbolAmount.text = formattedSymbolAndAmount
            incCurrentPosition.tvName.text = assetUiModelArg.name
            incCurrentPosition.tvTotalPrice.text = assetUiModelArg.getFormattedTotalPrice()
            incCurrentPosition.tvYield.text = assetUiModelArg.getFormattedYield(assetUiModelArg.getYield())

            incUpdatedPosition.color.setBackgroundColor(color)
            incUpdatedPosition.tvSymbolAmount.text = formattedSymbolAndAmount
            incUpdatedPosition.tvName.text = assetUiModelArg.name
            incUpdatedPosition.tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
        }
        setupAmountAndValuePerUnit()
        setupInitialUpdatedPositionLayout()
    }

    private fun setupAmountAndValuePerUnit() {
        binding.ietAmount.apply {
            doAfterTextChanged { updatePosition() }
            setEditTextLongNumberFormatMask()
            hint = LocaleUtil.getFormattedLong(0)
        }

        binding.ietValuePerUnit.apply {
            val currency = assetUiModelArg.currency
            doAfterTextChanged { updatePosition() }
            setEditTextCurrencyFormatMask(currency)
            hint = LocaleUtil.getFormattedCurrencyValue(currency, 0.0)
        }
    }

    private fun setupInitialUpdatedPositionLayout() {
        binding.incUpdatedPosition.apply {
            tvInfoMessage.visibility = View.VISIBLE
            tableLayout.visibility = View.INVISIBLE
            binding.incBtnSave.root.isEnabled = false
        }
    }

    private fun getAdditionAmount(): Long {
        val amountText = binding.ietAmount.text.toString()
        return if (amountText.isNotEmpty()) amountText.getOnlyNumbers().toLong() else 0
    }

    private fun getValuePerUnit(): Double {
        val valuePerUnitText = binding.ietValuePerUnit.text.toString()
        return if (valuePerUnitText.isNotEmpty()) valuePerUnitText.getOnlyNumbers().toDouble() / 100 else 0.0
    }

    @SuppressLint("SetTextI18n")
    private fun updatePosition() {
        binding.incUpdatedPosition.apply {
            val additionAmount = getAdditionAmount()
            val valuePerUnit = getValuePerUnit()
            if (additionAmount != 0L && valuePerUnit != 0.0) {
                val currency = assetUiModelArg.currency
                val updatedAmount = additionAmount + assetUiModelArg.amount
                val updatedTotalPrice = (assetUiModelArg.price * additionAmount) + assetUiModelArg.getTotalPrice()
                val updatedTotalInvested = (valuePerUnit * additionAmount) + assetUiModelArg.totalInvested

                tvSymbolAmount.text =
                    "${assetUiModelArg.getFormattedSymbol()} (${LocaleUtil.getFormattedLong(updatedAmount)})"
                tvTotalPrice.text = LocaleUtil.getFormattedCurrencyValue(currency, updatedTotalPrice)
                tvYield.text = getFormattedYield(currency, updatedTotalPrice, updatedTotalInvested)

                tvYield.visibility = View.VISIBLE
                tvInfoMessage.visibility = View.INVISIBLE
                binding.incBtnSave.root.isEnabled = true

            } else
                setupInitialUpdatedPositionLayout()
        }
    }

    private fun getFormattedYield(currency: String, totalPrice: Double, totalInvested: Double): String {
        val yield = (totalPrice - totalInvested).getRoundedDouble()
        val yieldPercent = yield / totalInvested

        return "${LocaleUtil.getFormattedCurrencyValue(currency, yield)} (${
            LocaleUtil.getFormattedPercent(
                yieldPercent
            )
        })"
    }

    private fun setupButtons() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            setOnClickListener {
                val additionAmount = getAdditionAmount()
                val additionTotalInvested = getValuePerUnit() * additionAmount
                assetUiModelArg.amount += additionAmount
                assetUiModelArg.totalInvested += additionTotalInvested
                walletViewModel.saveAsset(assetUiModelArg)
            }
        }
    }

    private fun observeAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.assetUiState.collectLatest {
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