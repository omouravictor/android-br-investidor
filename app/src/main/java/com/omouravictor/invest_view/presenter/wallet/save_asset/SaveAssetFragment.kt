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
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCurrencyFormatMask
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCursorColor
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextIntNumberFormatMask
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsAfterTextChanged
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsHighLightColor
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForCurrency
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForPercent

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
        val etQuantity = binding.etQuantity
        val etTotalInvested = binding.etTotalInvested
        val etSymbol = binding.etSymbol

        setEditTextsAfterTextChanged({ updateCurrentPosition() }, etSymbol, etQuantity, etTotalInvested)
        setEditTextsHighLightColor(assetTypeColor.defaultColor, etQuantity, etTotalInvested)
        setEditTextsFocusChange(assetTypeColor, etQuantity, etTotalInvested)
        setEditTextCursorColor(etQuantity, assetTypeColor.defaultColor)
        setEditTextIntNumberFormatMask(etQuantity)
        setEditTextCurrencyFormatMask(etTotalInvested, assetDTO.currency)
        etSymbol.setText(assetDTO.getDisplaySymbol())
        binding.etLocation.setText(assetDTO.region)
        binding.incItemListAsset.assetColor.backgroundTintList = assetTypeColor
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
        return binding.etSymbol.text.isNotEmpty() && binding.etQuantity.text.isNotEmpty()
    }

    private fun updateVariation(itemListAssetBinding: ItemListAssetBinding, totalAssetPrice: Double) {
        val etTotalInvestedText = binding.etTotalInvested.text.toString()

        if (etTotalInvestedText.isNotEmpty()) {
            val totalInvested = saveAssetViewModel.getTotalInvested(etTotalInvestedText)
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
                getFormattedValueForCurrency(assetDTO.currency, variation),
                getFormattedValueForPercent(percent)
            )
        } else {
            itemListAssetBinding.ivArrow.isVisible = false
            itemListAssetBinding.tvVariation.text = ""
        }
    }

    private fun updateCurrentPosition() {
        binding.incItemListAsset.apply {
            if (requiredFieldsNotEmpty()) {
                val etQuantityText = binding.etQuantity.text.toString()
                val totalAssetPrice = saveAssetViewModel.getTotalAssetPrice(assetDTO.price, etQuantityText)

                tvSymbolAndQuantity.text =
                    getString(R.string.placeholderSymbolAndQuantity, binding.etSymbol.text.toString(), etQuantityText)
                tvName.text = assetDTO.name
                tvTotal.text = getFormattedValueForCurrency(assetDTO.currency, totalAssetPrice)
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