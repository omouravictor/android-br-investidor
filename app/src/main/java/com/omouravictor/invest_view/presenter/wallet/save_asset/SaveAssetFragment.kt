package com.omouravictor.invest_view.presenter.wallet.save_asset

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCurrencyFormatMask
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCursorColor
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsAfterTextChanged
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsHighLightColor
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForCurrencyLocale
import java.util.Locale

class SaveAssetFragment : Fragment() {

    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var locale: Locale
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
        setupEditTexts()
        setupIncCurrentPosition()
    }

    private fun initEssentialVars() {
        assetBySearchDTO = SaveAssetFragmentArgs.fromBundle(requireArguments()).assetBySearchDTO
        locale = Locale("pt", "BR")
    }

    private fun setupEditTexts() {
        val assetTypeColor = assetBySearchDTO.assetType.getColor(requireContext())
        val assetTypeDefaultColor = assetTypeColor.defaultColor
        val etQuantity = binding.etQuantity
        val etTotalInvested = binding.etTotalInvested
        val etSymbol = binding.etSymbol

        setEditTextCursorColor(etQuantity, assetTypeDefaultColor)
        setEditTextsHighLightColor(assetTypeDefaultColor, etQuantity, etTotalInvested)
        setEditTextsFocusChange(etQuantity, etTotalInvested)
        setEditTextsAfterTextChanged(
            { updateCurrentPosition() }, etSymbol, etQuantity, etTotalInvested
        )
        setEditTextCurrencyFormatMask(etTotalInvested, locale)
        etSymbol.setText(assetBySearchDTO.symbol.substringBeforeLast("."))
        etTotalInvested.hint = if (Build.VERSION.SDK_INT >= 28) "R$ 100,00" else "R$100,00"
    }

    private fun setupIncCurrentPosition() {
        binding.incCurrentPosition.assetColor.backgroundTintList =
            assetBySearchDTO.assetType.getColor(requireContext())
    }

    private fun setEditTextsFocusChange(vararg editTexts: EditText) {
        val context = requireContext()
        val assetTypeColor = assetBySearchDTO.assetType.getColor(context)
        val grayColor = context.getColorStateList(R.color.gray)
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
        val btnSave = binding.btnSave

        if (requiredFieldsNotEmpty()) {
            tvInfoMessage.visibility = View.INVISIBLE
            layoutAssetInfo.visibility = View.VISIBLE
            incCurrentPosition.tvSymbolAndQuantity.text = getString(
                R.string.placeholderSymbolAndQuantity, etSymbolText, etQuantityText
            )
            val total = assetBySearchDTO.price * etQuantityText.toInt()
            incCurrentPosition.tvTotal.text = getFormattedValueForCurrencyLocale(locale, total)
            incCurrentPosition.tvName.text = assetBySearchDTO.name
            btnSave.isEnabled = true

        } else {
            tvInfoMessage.visibility = View.VISIBLE
            layoutAssetInfo.visibility = View.INVISIBLE
            btnSave.isEnabled = false
        }
    }
}