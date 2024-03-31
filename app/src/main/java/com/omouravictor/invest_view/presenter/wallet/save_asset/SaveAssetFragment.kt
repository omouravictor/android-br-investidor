package com.omouravictor.invest_view.presenter.wallet.save_asset

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
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
import com.omouravictor.invest_view.util.NumberUtil.getRoundedDouble
import com.omouravictor.invest_view.util.StringUtil.getOnlyNumbers

class SaveAssetFragment : Fragment() {

    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var assetBySearchDTO: AssetBySearchUiModel

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
            assetBySearchDTO.getAssetType().getDescription(requireContext())
    }

    private fun initEssentialVars() {
        assetBySearchDTO = SaveAssetFragmentArgs.fromBundle(requireArguments()).assetBySearchDTO
    }

    private fun setupViews() {
        val assetTypeColor = assetBySearchDTO.getAssetType().getColor(requireContext())
        val etQuantity = binding.etQuantity
        val etTotalInvested = binding.etTotalInvested
        val etSymbol = binding.etSymbol

        setEditTextsAfterTextChanged({ updateCurrentPosition() }, etSymbol, etQuantity, etTotalInvested)
        setEditTextsHighLightColor(assetTypeColor.defaultColor, etQuantity, etTotalInvested)
        setEditTextsFocusChange(assetTypeColor, etQuantity, etTotalInvested)
        setEditTextCursorColor(etQuantity, assetTypeColor.defaultColor)
        setEditTextIntNumberFormatMask(etQuantity)
        setEditTextCurrencyFormatMask(etTotalInvested, assetBySearchDTO.currency)
        etSymbol.setText(assetBySearchDTO.getDisplaySymbol())
        etTotalInvested.hint = getFormattedValueForCurrency(assetBySearchDTO.currency, 100)
        binding.etLocation.setText(assetBySearchDTO.region)
        binding.incCurrentPosition.assetColor.backgroundTintList = assetTypeColor
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

    private fun updateCurrentPosition() {
        val incCurrentPosition = binding.incCurrentPosition
        val etSymbolText = binding.etSymbol.text.toString()
        val etQuantityText = binding.etQuantity.text.toString()
        val tvInfoMessage = incCurrentPosition.tvInfoMessage
        val layoutAssetInfo = incCurrentPosition.layoutAssetInfo
        val etTotalInvestedText = binding.etTotalInvested.text.toString()
        val btnSave = binding.btnSave

        if (requiredFieldsNotEmpty()) {
            val total = assetBySearchDTO.price * etQuantityText.replace(".", "").toInt()
            val totalInvested = getOnlyNumbers(etTotalInvestedText)

            if (totalInvested.isNotEmpty()) {
                val totalInvestedValue = totalInvested.toDouble() / 100
                val appreciation = getRoundedDouble(total - totalInvestedValue)
                val appreciationFtd = getFormattedValueForCurrency(assetBySearchDTO.currency, appreciation)
                val percentFtd = getFormattedValueForPercent(appreciation / totalInvestedValue)

                incCurrentPosition.tvAppreciationAndDepreciation.text =
                    getString(R.string.placeholderAppreciationAndDepreciation, appreciationFtd, percentFtd)
            } else {
                incCurrentPosition.tvAppreciationAndDepreciation.text = ""
            }

            incCurrentPosition.tvTotal.text = getFormattedValueForCurrency(assetBySearchDTO.currency, total)
            tvInfoMessage.visibility = View.INVISIBLE
            layoutAssetInfo.visibility = View.VISIBLE
            incCurrentPosition.tvSymbolAndQuantity.text = getString(
                R.string.placeholderSymbolAndQuantity, etSymbolText, etQuantityText
            )
            incCurrentPosition.tvName.text = assetBySearchDTO.name
            btnSave.isEnabled = true

        } else {
            tvInfoMessage.visibility = View.VISIBLE
            layoutAssetInfo.visibility = View.INVISIBLE
            btnSave.isEnabled = false
        }
    }
}