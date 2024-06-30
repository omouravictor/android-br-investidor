package com.omouravictor.invest_view.presenter.wallet.new_addition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentNewAdditionBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.save_asset.SaveViewModel
import com.omouravictor.invest_view.util.BindingUtil
import com.omouravictor.invest_view.util.EditTextUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.StringUtil

class NewAdditionFragment : Fragment() {

    private lateinit var binding: FragmentNewAdditionBinding
    private lateinit var assetUiModel: AssetUiModel
    private val saveViewModel: SaveViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEssentialVars()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewAdditionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupViews()
        setupBtnSave()
    }

    private fun setupToolbar() {
        requireActivity().findViewById<Toolbar>(R.id.toolbar).apply {
            title = ""
            findViewById<TextView>(R.id.tvToolbarCenterText).text = getString(R.string.newAddition)
        }
    }

    private fun initEssentialVars() {
        val assetUiModelArg = NewAdditionFragmentArgs.fromBundle(requireArguments()).assetUiModel
        assetUiModel = AssetUiModel(
            symbol = assetUiModelArg.symbol,
            name = assetUiModelArg.name,
            originalType = assetUiModelArg.originalType,
            assetType = assetUiModelArg.assetType,
            region = assetUiModelArg.region,
            currency = assetUiModelArg.currency,
            price = assetUiModelArg.price,
            amount = assetUiModelArg.amount,
            totalInvested = assetUiModelArg.totalInvested
        )
    }

    private fun setupViews() {
        val context = requireContext()

        binding.incItemListAsset.color.setBackgroundColor(context.getColor(assetUiModel.assetType.colorResId))
        setupAmountAndTotalInvestedViews()
    }

    private fun setupBtnSave() {
        binding.incBtnSave.root.apply {
            text = getString(R.string.save)
            isEnabled = false
            setOnClickListener {
//                assetUiModel.amount = getAmount()
//                assetUiModel.totalInvested = getTotalInvested()
//                saveViewModel.saveAsset(assetUiModel)
            }
        }
    }

    private fun setupAmountAndTotalInvestedViews() {
        val currency = assetUiModel.currency
        val ietAmount = binding.ietAmount
        val ietUnitValuePerUnit = binding.ietUnitValuePerUnit

        EditTextUtil.setEditTextsAfterTextChanged({ updateCurrentPosition() }, ietAmount, ietUnitValuePerUnit)
        EditTextUtil.setEditTextLongNumberFormatMask(ietAmount)
        EditTextUtil.setEditTextCurrencyFormatMask(ietUnitValuePerUnit, currency)

        ietAmount.hint = LocaleUtil.getFormattedValueForLongNumber(0)
        ietUnitValuePerUnit.hint = LocaleUtil.getFormattedCurrencyValue(currency, 0.0)
    }

    private fun requiredFieldsNotEmpty(): Boolean {
        val ietAmountText = binding.ietAmount.text.toString()
        val ietTotalInvestedText = binding.ietUnitValuePerUnit.text.toString()
        return ietAmountText.isNotEmpty() && ietAmountText != "0" &&
                ietTotalInvestedText.isNotEmpty() && ietTotalInvestedText != "0"
    }

    private fun getAmount(): Long {
        val amount = binding.ietAmount.text.toString()
        return if (amount.isNotEmpty()) {
            StringUtil.getOnlyNumbers(amount).toLong()
        } else {
            0
        }
    }

    private fun getTotalInvested(): Double {
        val totalInvested = binding.ietUnitValuePerUnit.text.toString()
        return if (totalInvested.isNotEmpty()) {
            StringUtil.getOnlyNumbers(totalInvested).toDouble() / 100
        } else {
            0.0
        }
    }

    private fun updateCurrentPosition() {
        binding.incItemListAsset.apply {
            if (requiredFieldsNotEmpty()) {
                val priceCurrentPosition = assetUiModel.price * getAmount()
                val totalInvested = getTotalInvested()
                val currency = assetUiModel.currency

                tvSymbol.text = assetUiModel.getFormattedSymbol()
                tvAmount.text = getString(R.string.placeholderAssetAmount, binding.ietAmount.text.toString())
                tvName.text = assetUiModel.name
                tvTotal.text = LocaleUtil.getFormattedCurrencyValue(currency, priceCurrentPosition)
                tvInfoMessage.visibility = View.INVISIBLE
                layoutAssetInfo.visibility = View.VISIBLE
                binding.incBtnSave.root.isEnabled = true
                BindingUtil.calculateAndSetupVariationLayout(
                    binding = this.incLayoutVariation,
                    textSize = 12f,
                    currency = currency,
                    reference = priceCurrentPosition,
                    totalReference = totalInvested
                )

            } else {
                tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
                tvInfoMessage.visibility = View.VISIBLE
                layoutAssetInfo.visibility = View.INVISIBLE
                binding.incBtnSave.root.isEnabled = false
            }
        }
    }

}