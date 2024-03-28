package com.omouravictor.invest_view.presenter.wallet.save_asset

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCurrencyFormat
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCursorColor
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsAfterTextChanged
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsHighLightColor

class SaveAssetFragment : Fragment() {

    private lateinit var binding: FragmentSaveAssetBinding
    private val assetBySearchDTO: AssetBySearchUiModel by lazy {
        SaveAssetFragmentArgs.fromBundle(requireArguments()).assetBySearchDTO
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

        binding.incCurrentPosition.assetColor.backgroundTintList =
            assetBySearchDTO.assetType.getColor(requireContext())
    }

    private fun setupEditTexts() {
        val assetTypeColor = assetBySearchDTO.assetType.getColor(requireContext())
        val assetTypeDefaultColor = assetTypeColor.defaultColor

        TextViewCompat.setCompoundDrawableTintList(binding.etSymbol, assetTypeColor)
        setEditTextCursorColor(binding.etQuantity, assetTypeDefaultColor)
        setEditTextsHighLightColor(
            assetTypeDefaultColor,
            binding.etQuantity,
            binding.etTotalInvested
        )
        setEditTextsFocusChange(binding.etQuantity, binding.etTotalInvested)
        setEditTextsAfterTextChanged(
            { updateAssetPreview() },
            binding.etSymbol,
            binding.etQuantity,
            binding.etTotalInvested
        )
        setEditTextCurrencyFormat(binding.etTotalInvested)
        binding.etSymbol.setText(assetBySearchDTO.symbol)
        binding.etSymbol.setOnClickListener { findNavController().popBackStack() }
        binding.etTotalInvested.hint = if (Build.VERSION.SDK_INT >= 28) "R$ 100,00" else "R$100,00"
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

    private fun requiredFieldsNotEmpty() =
        binding.etSymbol.text.isNotEmpty() && binding.etQuantity.text.isNotEmpty()

    private fun updateAssetPreview() {
        if (requiredFieldsNotEmpty()) {
            binding.incCurrentPosition.tvInfoMessage.visibility = View.INVISIBLE
            binding.incCurrentPosition.layoutAssetInfo.visibility = View.VISIBLE
            binding.incCurrentPosition.tvSymbolAndQuantity.text = getString(
                R.string.placeholderSymbolAndQuantity,
                binding.etSymbol.text,
                binding.etQuantity.text
            )

            // adicionar máscara monetária
            binding.incCurrentPosition.tvTotal.text =
                "R$ ${assetBySearchDTO.price * binding.etQuantity.text.toString().toInt()}"
            binding.incCurrentPosition.tvCompanyName.text = assetBySearchDTO.companyName

            binding.btnSave.isEnabled = true

        } else {
            binding.incCurrentPosition.tvInfoMessage.visibility = View.VISIBLE
            binding.incCurrentPosition.layoutAssetInfo.visibility = View.INVISIBLE

            binding.btnSave.isEnabled = false
        }
    }
}