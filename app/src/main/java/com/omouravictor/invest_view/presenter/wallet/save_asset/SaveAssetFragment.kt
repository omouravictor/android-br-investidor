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
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsAfterTextChanged
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsHighLightColor
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForCurrencyLocale

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
        setEditTextCurrencyFormatMask(etTotalInvested, assetBySearchDTO.currency)
        etSymbol.setText(assetBySearchDTO.getDisplaySymbol())
        etTotalInvested.hint = getFormattedValueForCurrencyLocale(assetBySearchDTO.currency, 100)
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
        val btnSave = binding.btnSave

        if (requiredFieldsNotEmpty()) {
            tvInfoMessage.visibility = View.INVISIBLE
            layoutAssetInfo.visibility = View.VISIBLE
            incCurrentPosition.tvSymbolAndQuantity.text = getString(
                R.string.placeholderSymbolAndQuantity, etSymbolText, etQuantityText
            )
            val total = assetBySearchDTO.price * etQuantityText.toInt()
            incCurrentPosition.tvTotal.text =
                getFormattedValueForCurrencyLocale(assetBySearchDTO.currency, total)
            incCurrentPosition.tvName.text = assetBySearchDTO.name
            btnSave.isEnabled = true

        } else {
            tvInfoMessage.visibility = View.VISIBLE
            layoutAssetInfo.visibility = View.INVISIBLE
            btnSave.isEnabled = false
        }
    }
}