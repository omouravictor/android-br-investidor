package com.omouravictor.invest_view.ui.wallet.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColorStateList
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentCreateAssetBinding
import com.omouravictor.invest_view.util.EditTextUtil.setupEditTextCursorColor
import com.omouravictor.invest_view.util.EditTextUtil.setupEditTextsAfterTextChanged
import com.omouravictor.invest_view.util.EditTextUtil.setupEditTextsHighLightColor

class CreateAssetFragment : Fragment() {

    private var _binding: FragmentCreateAssetBinding? = null
    private val binding get() = _binding!!
    private val assetsViewModel: AssetsViewModel by activityViewModels()
    private val assetTypeUiModelArg by lazy {
        CreateAssetFragmentArgs.fromBundle(requireArguments()).assetTypeUiModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity)
            .supportActionBar?.title = assetTypeUiModelArg.description

        setupEditTexts()
        binding.incAssetPreview.vAssetColor.backgroundTintList = assetTypeUiModelArg.color
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupEditTexts() {
        binding.etAssetSymbol.setOnClickListener {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }

        setupEditTextCursorColor(binding.etAssetSymbol, assetTypeUiModelArg.color.defaultColor)
        setupEditTextsHighLightColor(
            assetTypeUiModelArg.color.defaultColor,
            binding.etAssetSymbol,
            binding.etQuantity,
            binding.etTotal
        )
        setupEditTextsFocusChange(binding.etQuantity, binding.etTotal)
        setupEditTextsAfterTextChanged(
            { updateAssetPreview() },
            binding.etAssetSymbol,
            binding.etQuantity
        )
    }

    private fun setupEditTextsFocusChange(vararg editTexts: EditText) {
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { v, hasFocus ->
                v.backgroundTintList = if (hasFocus) assetTypeUiModelArg.color
                else getColorStateList(requireContext(), R.color.gray)
            }
        }
    }

    private fun areRequiredFieldsNotEmpty() =
        binding.etAssetSymbol.text.isNotEmpty() && binding.etQuantity.text.isNotEmpty()

    private fun updateAssetPreview() {
        val fieldsNotEmpty = areRequiredFieldsNotEmpty()

        if (fieldsNotEmpty) {
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