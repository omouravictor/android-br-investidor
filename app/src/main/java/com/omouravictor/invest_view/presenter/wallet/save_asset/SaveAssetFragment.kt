package com.omouravictor.invest_view.presenter.wallet.save_asset

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.wallet.assets.AssetsViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getAssetType
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.EditTextUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.NavigationUtil
import com.omouravictor.invest_view.util.StringUtil

class SaveAssetFragment : Fragment() {

    private val saveAssetViewModel: SaveAssetViewModel by viewModels()
    private val assetsViewModel: AssetsViewModel by activityViewModels()
    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var assetUiModel: AssetUiModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEssentialVars()
        setupSupportActionBarTitle()
        setupViews()
        setupBtnSave()
    }

    private fun initEssentialVars() {
        val assetBySearchUiModel = SaveAssetFragmentArgs.fromBundle(requireArguments()).assetBySearchUiModel
        assetUiModel = AssetUiModel(
            symbol = assetBySearchUiModel.symbol,
            name = assetBySearchUiModel.name,
            type = assetBySearchUiModel.type,
            region = assetBySearchUiModel.region,
            currency = assetBySearchUiModel.currency,
            price = assetBySearchUiModel.price
        )
    }

    private fun setupSupportActionBarTitle() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            assetUiModel.getAssetType().getDescription(requireContext())
    }

    private fun setupViews() {
        val assetTypeColor = assetUiModel.getAssetType().getColor(requireContext())
        val ietAmount = binding.ietAmount
        val ietTotalInvested = binding.ietTotalInvested
        val textInputLayoutAmount = binding.textInputLayoutAmount
        val textInputLayoutTotalInvested = binding.textInputLayoutTotalInvested
        val currency = assetUiModel.currency
        val etSymbol = binding.etSymbol
        val etLocation = binding.etLocation
        val incItemListAsset = binding.incItemListAsset

        etSymbol.setText(AssetUtil.getDisplaySymbol(assetUiModel.symbol))
        etLocation.setText(assetUiModel.region)
        ietTotalInvested.hint = LocaleUtil.getFormattedValueForCurrency(currency, 0.0)
        EditTextUtil.setEditTextsAfterTextChanged({ updateCurrentPosition() }, ietAmount, ietTotalInvested)
        EditTextUtil.setEditTextsHighLightColor(assetTypeColor.defaultColor, ietAmount, ietTotalInvested)
        EditTextUtil.setEditTextIntNumberFormatMask(ietAmount)
        EditTextUtil.setEditTextCurrencyFormatMask(ietTotalInvested, currency)
        setEditTextsFocusChange(assetTypeColor, ietAmount, ietTotalInvested)
        textInputLayoutAmount.setBoxStrokeColorStateList(assetTypeColor)
        textInputLayoutTotalInvested.setBoxStrokeColorStateList(assetTypeColor)
        incItemListAsset.color.backgroundTintList = assetTypeColor
        ietAmount.setText("1")
    }

    private fun setEditTextsFocusChange(assetTypeColor: ColorStateList, vararg editTexts: EditText) {
        val grayColor = requireContext().getColorStateList(R.color.gray)
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { v, hasFocus ->
                v.backgroundTintList = if (hasFocus) assetTypeColor else grayColor
            }
        }
    }

    private fun requiredFieldsNotEmpty(): Boolean {
        val ietAmountText = binding.ietAmount.text.toString()
        return ietAmountText.isNotEmpty() && ietAmountText != "0"
    }

    private fun updateVariation(itemListAssetBinding: ItemListAssetBinding, totalAssetPrice: Double) {
        val ietTotalInvestedText = binding.ietTotalInvested.text.toString()

        if (ietTotalInvestedText.isNotEmpty()) {
            val totalInvested = saveAssetViewModel.getDoubleTotalInvested(ietTotalInvestedText)
            val (variation, percent) = AssetUtil.getVariation(totalAssetPrice, totalInvested)

            if (variation > 0) {
                itemListAssetBinding.ivArrow.isVisible = true
                itemListAssetBinding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
                itemListAssetBinding.tvVariation.setTextColor(getColor(requireContext(), R.color.green))
            } else if (variation < 0) {
                itemListAssetBinding.ivArrow.isVisible = true
                itemListAssetBinding.ivArrow.setImageResource(R.drawable.ic_arrow_down)
                itemListAssetBinding.tvVariation.setTextColor(getColor(requireContext(), R.color.red))
            } else {
                itemListAssetBinding.ivArrow.isVisible = false
                itemListAssetBinding.tvVariation.setTextColor(getColor(requireContext(), R.color.gray))
            }
            itemListAssetBinding.tvVariation.text = getString(
                R.string.placeholderVariation,
                LocaleUtil.getFormattedValueForCurrency(assetUiModel.currency, variation),
                LocaleUtil.getFormattedValueForPercent(percent)
            )

        } else {
            itemListAssetBinding.ivArrow.isVisible = false
            itemListAssetBinding.tvVariation.text = ""
        }
    }

    private fun updateCurrentPosition() {
        binding.incItemListAsset.apply {
            if (requiredFieldsNotEmpty()) {
                val ietAmountText = binding.ietAmount.text.toString()
                val totalAssetPrice =
                    saveAssetViewModel.getDoubleTotalAssetPrice(assetUiModel.price, ietAmountText)

                tvSymbolAndAmount.text =
                    getString(R.string.placeholderSymbolAndAmount, binding.etSymbol.text.toString(), ietAmountText)
                tvName.text = assetUiModel.name
                tvTotal.text = LocaleUtil.getFormattedValueForCurrency(assetUiModel.currency, totalAssetPrice)
                updateVariation(this, totalAssetPrice)
                tvInfoMessage.visibility = View.INVISIBLE
                layoutAssetInfo.visibility = View.VISIBLE
                binding.btnSave.isEnabled = true

            } else {
                tvInfoMessage.visibility = View.VISIBLE
                layoutAssetInfo.visibility = View.INVISIBLE
                binding.btnSave.isEnabled = false
            }
        }
    }

    private fun setupBtnSave() {
        binding.btnSave.setOnClickListener {
            assetUiModel.amount = StringUtil.getOnlyNumbers(binding.ietAmount.text.toString()).toLong()
            assetUiModel.totalInvested =
                saveAssetViewModel.getDoubleTotalInvested(binding.ietTotalInvested.text.toString())

            // handles the save operation with the view model

            NavigationUtil.clearPileAndNavigateToStart(findNavController())
        }
    }

}