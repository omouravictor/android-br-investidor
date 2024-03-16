package com.omouravictor.invest_view.presenter.wallet.assets.save_asset

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCurrencyFormat
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextCursorColor
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsAfterTextChanged
import com.omouravictor.invest_view.util.EditTextUtil.setEditTextsHighLightColor

class SaveAssetFragment : Fragment() {

    private var _binding: FragmentSaveAssetBinding? = null
    private val binding get() = _binding!!
    private val assetTypeUiArg by lazy {
        SaveAssetFragmentArgs.fromBundle(requireArguments()).assetTypeUi
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity)
            .supportActionBar?.title = assetTypeUiArg.description

        setupEditTexts()
        binding.incAssetPreview.vAssetColor.backgroundTintList = assetTypeUiArg.color
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupEditTexts() {
        TextViewCompat.setCompoundDrawableTintList(binding.etAssetSymbol, assetTypeUiArg.color)
        setEditTextCursorColor(binding.etQuantity, assetTypeUiArg.color.defaultColor)
        setEditTextsHighLightColor(
            assetTypeUiArg.color.defaultColor,
            binding.etQuantity,
            binding.etTotalInvested
        )
        setEditTextsFocusChange(binding.etQuantity, binding.etTotalInvested)
        setEditTextsAfterTextChanged(
            { updateAssetPreview() },
            binding.etAssetSymbol,
            binding.etQuantity,
            binding.etTotalInvested
        )
        binding.etAssetSymbol.setOnClickListener {
            findNavController().navigate(SaveAssetFragmentDirections.navToAssetSearchFragment())
        }
        setEditTextCurrencyFormat(binding.etTotalInvested)
        binding.etTotalInvested.hint = if (Build.VERSION.SDK_INT >= 28) "R$ 100,00" else "R$100,00"
    }

    private fun setEditTextsFocusChange(vararg editTexts: EditText) {
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { v, hasFocus ->
                v.backgroundTintList = if (hasFocus) assetTypeUiArg.color
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
            binding.incAssetPreview.tvWalletPercent.text = "15,55%"
            binding.incAssetPreview.tvCurrentTotalValue.text = "R$ 1.000,00"

            binding.btnSave.isEnabled = true

        } else {
            binding.incAssetPreview.tvInfo.visibility = View.VISIBLE
            binding.incAssetPreview.clAssetInfo.visibility = View.INVISIBLE

            binding.btnSave.isEnabled = false
        }
    }
}