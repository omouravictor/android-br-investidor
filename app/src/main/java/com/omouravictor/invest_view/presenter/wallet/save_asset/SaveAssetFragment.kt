package com.omouravictor.invest_view.presenter.wallet.save_asset

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.presenter.wallet.asset_search.model.AssetBySearchUiModel
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCurrencyFormat
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCursorColor
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsAfterTextChanged
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsHighLightColor

class SaveAssetFragment : Fragment() {

    private lateinit var binding: FragmentSaveAssetBinding
    private val assetBySearchDTO: AssetBySearchUiModel by lazy {
        SaveAssetFragmentArgs.fromBundle(
            requireArguments()
        ).assetBySearchDTO
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEditTexts()

        binding.etAssetSymbol.setText(assetBySearchDTO.symbol)

        binding.incAssetPreview.vAssetColor.backgroundTintList =
            assetBySearchDTO.assetType.getColor(requireContext())
    }

    private fun setupEditTexts() {
        val colorStateList = assetBySearchDTO.assetType.getColor(requireContext())
        val defaultColor = colorStateList.defaultColor

        TextViewCompat.setCompoundDrawableTintList(binding.etAssetSymbol, colorStateList)
        setEditTextCursorColor(binding.etQuantity, defaultColor)
        setEditTextsHighLightColor(defaultColor, binding.etQuantity, binding.etTotalInvested)
        setEditTextsFocusChange(binding.etQuantity, binding.etTotalInvested)
        setEditTextsAfterTextChanged(
            { updateAssetPreview() },
            binding.etAssetSymbol,
            binding.etQuantity,
            binding.etTotalInvested
        )
        binding.etAssetSymbol.setOnClickListener { findNavController().popBackStack() }
        setEditTextCurrencyFormat(binding.etTotalInvested)
        binding.etTotalInvested.hint = if (Build.VERSION.SDK_INT >= 28) "R$ 100,00" else "R$100,00"
    }

    private fun setEditTextsFocusChange(vararg editTexts: EditText) {
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { v, hasFocus ->
                v.backgroundTintList =
                    if (hasFocus) assetBySearchDTO.assetType.getColor(requireContext())
                    else getColorStateList(requireContext(), R.color.gray)
            }
        }
    }

    private fun requiredFieldsNotEmpty() =
        binding.etAssetSymbol.text.isNotEmpty() && binding.etQuantity.text.isNotEmpty()

    private fun updateAssetPreview() {
        if (requiredFieldsNotEmpty()) {
            binding.incAssetPreview.tvInfo.visibility = View.INVISIBLE
            binding.incAssetPreview.clAssetInfo.visibility = View.VISIBLE
            binding.incAssetPreview.tvAssetName.text =
                "${binding.etAssetSymbol.text} (${binding.etQuantity.text})"
            binding.incAssetPreview.tvCompanyName.text = assetBySearchDTO.name
            binding.incAssetPreview.tvCurrentTotalValue.text =
                "R$ ${assetBySearchDTO.price * binding.etQuantity.text.toString().toInt()}"

            binding.btnSave.isEnabled = true

        } else {
            binding.incAssetPreview.tvInfo.visibility = View.VISIBLE
            binding.incAssetPreview.clAssetInfo.visibility = View.INVISIBLE

            binding.btnSave.isEnabled = false
        }
    }
}