package com.omouravictor.invest_view.ui.wallet.assets

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.invest_view.MainActivity
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentCreateAssetBinding

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

        setCursorColor(binding.acAssetSymbol, assetTypeUiModelArg.color)

        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            assetTypeUiModelArg.description

        if (requireActivity() is MainActivity) {
            handleMainActivity(requireActivity() as MainActivity)
        }
    }

    private fun setCursorColor(editText: EditText, colorResId: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            editText.textCursorDrawable?.apply { setTintList(colorResId) }

        } else {
            val field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true

            val cursorDrawableField = TextView::class.java.getDeclaredField("mEditor")
            cursorDrawableField.isAccessible = true
            val editor = cursorDrawableField.get(editText)

            val cursorDrawable = getDrawable(editText.context, field.getInt(editText))
            cursorDrawable?.setTintList(colorResId)

            val cursorDrawableField2 = editor.javaClass.getDeclaredField("mCursorDrawable")
            cursorDrawableField2.isAccessible = true
            cursorDrawableField2.set(editor, arrayOf(cursorDrawable, cursorDrawable))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleMainActivity(mainActivity: MainActivity) {
        setupAfterTextChangedOnFields(
            { mainActivity.setupSaveItemMenu(areRequiredFieldsNotEmpty()) },
            binding.acAssetSymbol,
            binding.etQuantity
        )

        setupFocusChangeOnFields(binding.acAssetSymbol, binding.etQuantity, binding.etTotal)
    }

    private fun setupAfterTextChangedOnFields(
        doAfterTextChangedFunction: () -> Unit,
        vararg editTexts: EditText
    ) {
        editTexts.forEach { editText ->
            editText.doAfterTextChanged {
                doAfterTextChangedFunction()
            }
        }
    }

    private fun areRequiredFieldsNotEmpty() =
        binding.acAssetSymbol.text.isNotEmpty() && binding.etQuantity.text.isNotEmpty()

    private fun setupFocusChangeOnFields(vararg editTexts: EditText) {
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { v, hasFocus ->
                v.backgroundTintList = if (hasFocus)
                    assetTypeUiModelArg.color
                else
                    getColorStateList(requireContext(), R.color.gray)
            }
        }
    }

}