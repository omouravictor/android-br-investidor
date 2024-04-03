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
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getAssetType
import com.omouravictor.invest_view.presenter.wallet.model.getDisplaySymbol
import com.omouravictor.invest_view.util.EditTextUtil
import com.omouravictor.invest_view.util.LocaleUtil

class SaveAssetFragment : Fragment() {

    private val saveAssetViewModel: SaveAssetViewModel by activityViewModels()
    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var assetDTO: AssetBySearchUiModel

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
    }

    private fun setupSupportActionBarTitle() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            assetDTO.getAssetType().getDescription(requireContext())
    }

    private fun initEssentialVars() {
        assetDTO = SaveAssetFragmentArgs.fromBundle(requireArguments()).assetDTO
    }

    private fun setupViews() {
        val assetTypeColor = assetDTO.getAssetType().getColor(requireContext())
        val ietQuantity = binding.ietQuantity
        val ietTotalInvested = binding.ietTotalInvested
        val currency = assetDTO.currency

        binding.etSymbol.setText(assetDTO.getDisplaySymbol())
        binding.etLocation.setText(assetDTO.region)
        ietTotalInvested.hint = LocaleUtil.getFormattedValueForCurrency(currency, 0.0)
        EditTextUtil.setEditTextsAfterTextChanged({ updateCurrentPosition() }, ietQuantity, ietTotalInvested)
        EditTextUtil.setEditTextsHighLightColor(assetTypeColor.defaultColor, ietQuantity, ietTotalInvested)
        EditTextUtil.setEditTextCursorColor(ietQuantity, assetTypeColor.defaultColor)
        EditTextUtil.setEditTextIntNumberFormatMask(ietQuantity)
        EditTextUtil.setEditTextCurrencyFormatMask(ietTotalInvested, currency)
        setEditTextsFocusChange(assetTypeColor, ietQuantity, ietTotalInvested)
        ietQuantity.setText("1")
        binding.incItemListAsset.color.backgroundTintList = assetTypeColor
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
        val ietQuantityText = binding.ietQuantity.text.toString()
        return ietQuantityText.isNotEmpty() && ietQuantityText != "0"
    }

    private fun updateVariation(itemListAssetBinding: ItemListAssetBinding, totalAssetPrice: Double) {
        val ietTotalInvestedText = binding.ietTotalInvested.text.toString()

        if (ietTotalInvestedText.isNotEmpty()) {
            val totalInvested = saveAssetViewModel.getTotalInvested(ietTotalInvestedText)
            val (variation, percent) = saveAssetViewModel.getVariation(totalAssetPrice, totalInvested)

            when {
                variation > 0 -> {
                    itemListAssetBinding.ivArrow.isVisible = true
                    itemListAssetBinding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
                    itemListAssetBinding.tvVariation.setTextColor(getColor(requireContext(), R.color.green))
                }
                variation < 0 -> {
                    itemListAssetBinding.ivArrow.isVisible = true
                    itemListAssetBinding.ivArrow.setImageResource(R.drawable.ic_arrow_down)
                    itemListAssetBinding.tvVariation.setTextColor(getColor(requireContext(), R.color.red))
                }
                else -> {
                    itemListAssetBinding.ivArrow.isVisible = false
                    itemListAssetBinding.tvVariation.setTextColor(getColor(requireContext(), R.color.gray))
                }
            }

            itemListAssetBinding.tvVariation.text = getString(
                R.string.placeholderVariation,
                LocaleUtil.getFormattedValueForCurrency(assetDTO.currency, variation),
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
                val ietQuantityText = binding.ietQuantity.text.toString()
                val totalAssetPrice = saveAssetViewModel.getTotalAssetPrice(assetDTO.price, ietQuantityText)

                tvSymbolAndQuantity.text =
                    getString(R.string.placeholderSymbolAndQuantity, binding.etSymbol.text.toString(), ietQuantityText)
                tvName.text = assetDTO.name
                tvTotal.text = LocaleUtil.getFormattedValueForCurrency(assetDTO.currency, totalAssetPrice)
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
}