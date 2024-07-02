package com.omouravictor.invest_view.presenter.wallet.new_addition

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentNewAdditionBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getTotalPrice
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.calculateAndSetupVariationLayout
import com.omouravictor.invest_view.util.getOnlyNumbers
import com.omouravictor.invest_view.util.setEditTextCurrencyFormatMask
import com.omouravictor.invest_view.util.setEditTextLongNumberFormatMask
import com.omouravictor.invest_view.util.setupToolbarCenterText

class NewAdditionFragment : Fragment() {

    private lateinit var binding: FragmentNewAdditionBinding
    private lateinit var assetUiModelArg: AssetUiModel

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
        setupBtnSave()
    }

    private fun setupViews() {
        setupAmountAndValuePerUnit()
        setupCurrentPosition()
        setupInitialUpdatedPositionLayout()
    }

    private fun setupBtnSave() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            isEnabled = false
            setOnClickListener { }
        }
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
            color.setBackgroundColor(root.context.getColor(assetUiModelArg.assetType.colorResId))
            tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
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
                val priceCurrentPosition = assetUiModelArg.price * getAmount()
                val currency = assetUiModelArg.currency

                tvSymbol.text = assetUiModelArg.getFormattedSymbol()
                tvAmount.text = "(${LocaleUtil.getFormattedValueForLongNumber(amount + assetUiModelArg.amount)})"
                tvName.text = assetUiModelArg.name
                tvTotalPrice.text = LocaleUtil.getFormattedCurrencyValue(currency, priceCurrentPosition)
                layoutAssetInfo.visibility = View.VISIBLE
                tvInfoMessage.visibility = View.INVISIBLE
                binding.incBtnSave.root.isEnabled = true
                incLayoutVariation.calculateAndSetupVariationLayout(
                    textSize = 12f,
                    currency = currency,
                    reference = priceCurrentPosition,
                    totalReference = valuePerUnit
                )

            } else
                setupInitialUpdatedPositionLayout()
        }
    }

}