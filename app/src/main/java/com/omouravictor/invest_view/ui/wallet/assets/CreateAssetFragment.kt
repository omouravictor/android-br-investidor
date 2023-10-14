package com.omouravictor.invest_view.ui.wallet.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColorStateList
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.invest_view.MainActivity
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
        setupEditTexts()

        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            assetTypeUiModelArg.description

        if (requireActivity() is MainActivity) {
            handleMainActivity(requireActivity() as MainActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupEditTexts() {
        binding.acAssetSymbol.requestFocus()
        setupEditTextCursorColor(binding.acAssetSymbol, assetTypeUiModelArg.color.defaultColor)
        setupEditTextsHighLightColor(
            assetTypeUiModelArg.color.defaultColor,
            binding.acAssetSymbol,
            binding.etQuantity,
            binding.etTotal
        )
        setupEditTextsFocusChange(binding.acAssetSymbol, binding.etQuantity, binding.etTotal)
    }

    private fun areRequiredFieldsNotEmpty() =
        binding.acAssetSymbol.text.isNotEmpty() && binding.etQuantity.text.isNotEmpty()

    private fun setupEditTextsFocusChange(vararg editTexts: EditText) {
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { v, hasFocus ->
                v.backgroundTintList = if (hasFocus) assetTypeUiModelArg.color
                else getColorStateList(requireContext(), R.color.gray)
            }
        }
    }

    private fun handleMainActivity(mainActivity: MainActivity) {
        setupEditTextsAfterTextChanged(
            { mainActivity.setupSaveItemMenu(areRequiredFieldsNotEmpty()) },
            binding.acAssetSymbol,
            binding.etQuantity
        )
    }

}