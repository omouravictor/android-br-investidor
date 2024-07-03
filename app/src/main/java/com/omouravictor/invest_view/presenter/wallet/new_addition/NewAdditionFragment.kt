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
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.AssetsListViewModel
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getTotalPrice
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.calculateAndSetupVariationLayout
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.getOnlyNumbers
import com.omouravictor.invest_view.util.setEditTextCurrencyFormatMask
import com.omouravictor.invest_view.util.setEditTextLongNumberFormatMask
import com.omouravictor.invest_view.util.setupToolbarCenterText
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewAdditionFragment : Fragment() {

    private lateinit var binding: FragmentNewAdditionBinding
    private lateinit var assetUiModelArg: AssetUiModel
    private val assetsListViewModel: AssetsListViewModel by activityViewModels()
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
        observeSaveUiState()
    }

    override fun onStop() {
        super.onStop()
        walletViewModel.resetUiState()
    }

    private fun setupViews() {
        val context = requireContext()

        binding.incUpdatedItemListAsset.color.setBackgroundColor(context.getColor(assetUiModelArg.assetType.colorResId))
        binding.incUpdatedItemListAsset.tvSymbol.text = assetUiModelArg.getFormattedSymbol()
        binding.incUpdatedItemListAsset.tvName.text = assetUiModelArg.name
        binding.incUpdatedItemListAsset.tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
        setupAmountAndValuePerUnit()
        setupCurrentPosition()
        setupInitialUpdatedPositionLayout()
    }

    private fun setupAmountAndValuePerUnit() {
        val currency = assetUiModelArg.currency
        val ietAmount = binding.ietAmount
        val ietValuePerUnit = binding.ietValuePerUnit

        ietAmount.doAfterTextChanged { updateUpdatedPosition() }
        ietValuePerUnit.doAfterTextChanged { updateUpdatedPosition() }
        ietAmount.setEditTextLongNumberFormatMask()
        ietValuePerUnit.setEditTextCurrencyFormatMask(currency)

        ietAmount.hint = LocaleUtil.getFormattedValueForLongNumber(0)
        ietValuePerUnit.hint = LocaleUtil.getFormattedCurrencyValue(currency, 0.0)
    }

    @SuppressLint("SetTextI18n")
    private fun setupCurrentPosition() {
        binding.incItemListAsset.apply {
            color.setBackgroundColor(root.context.getColor(assetUiModelArg.assetType.colorResId))
            tvSymbol.text = assetUiModelArg.getFormattedSymbol()
            tvAmount.text = "(${assetUiModelArg.getFormattedAmount()})"
            tvName.text = assetUiModelArg.name
            tvTotalPrice.text = assetUiModelArg.getFormattedTotalPrice()
            incLayoutVariation.calculateAndSetupVariationLayout(
                textSize = 12f,
                currency = assetUiModelArg.currency,
                reference = assetUiModelArg.getTotalPrice(),
                totalReference = assetUiModelArg.totalInvested
            )
        }
    }

    private fun setupInitialUpdatedPositionLayout() {
        binding.incUpdatedItemListAsset.apply {
            tvInfoMessage.visibility = View.VISIBLE
            layoutAssetInfo.visibility = View.INVISIBLE
            binding.incBtnSave.root.isEnabled = false
        }
    }

    private fun getAmount(): Long {
        val amountText = binding.ietAmount.text.toString()
        return if (amountText.isNotEmpty()) amountText.getOnlyNumbers().toLong() else 0
    }

    private fun getValuePerUnit(): Double {
        val valuePerUnitText = binding.ietValuePerUnit.text.toString()
        return if (valuePerUnitText.isNotEmpty()) valuePerUnitText.getOnlyNumbers().toDouble() / 100 else 0.0
    }

    @SuppressLint("SetTextI18n")
    private fun updateUpdatedPosition() {
        binding.incUpdatedItemListAsset.apply {
            val amount = getAmount()
            val valuePerUnit = getValuePerUnit()
            if (amount != 0L && valuePerUnit != 0.0) {
                val currency = assetUiModelArg.currency
                val updatedAmount = amount + assetUiModelArg.amount
                val updatedTotalPrice = (assetUiModelArg.price * amount) + assetUiModelArg.getTotalPrice()
                val updatedTotalInvested = (valuePerUnit * amount) + assetUiModelArg.totalInvested

                tvAmount.text = "(${LocaleUtil.getFormattedValueForLongNumber(updatedAmount)})"
                tvTotalPrice.text = LocaleUtil.getFormattedCurrencyValue(currency, updatedTotalPrice)
                incLayoutVariation.calculateAndSetupVariationLayout(
                    textSize = 12f,
                    currency = currency,
                    reference = updatedTotalPrice,
                    totalReference = updatedTotalInvested
                )
                layoutAssetInfo.visibility = View.VISIBLE
                tvInfoMessage.visibility = View.INVISIBLE
                binding.incBtnSave.root.isEnabled = true

            } else
                setupInitialUpdatedPositionLayout()
        }
    }

    private fun setupButtons() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            setOnClickListener {
                val amount = getAmount()
                val totalInvested = getValuePerUnit() * amount
                assetUiModelArg.amount += amount
                assetUiModelArg.totalInvested += totalInvested
                walletViewModel.saveAsset(assetUiModelArg)
            }
        }
    }

    private fun observeSaveUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.uiStateFlow.collectLatest {
                    when (it) {
                        is UiState.Initial -> Unit
                        is UiState.Loading -> {
                            binding.newAdditionLayout.visibility = View.INVISIBLE
                            binding.incProgressBar.root.visibility = View.VISIBLE
                        }

                        is UiState.Success -> {
                            val navController = findNavController()
                            val previousBackStackEntry = navController.previousBackStackEntry
                            val updatedAssetUiModel = it.data
                            assetsListViewModel.updateAsset(updatedAssetUiModel)
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
                    }
                }
            }
        }
    }

}