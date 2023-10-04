package com.omouravictor.invest_view.ui.wallet.new_asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.omouravictor.invest_view.MainActivity
import com.omouravictor.invest_view.databinding.FragmentCreateAssetBinding

class CreateAssetFragment : Fragment() {

    private var _binding: FragmentCreateAssetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val assetType = arguments?.getString("assetTypeName")!!
        (requireActivity() as AppCompatActivity).supportActionBar?.title = assetType

        binding.etQuantity.doAfterTextChanged { checkAndEnableSaveItem() }
        binding.acAssetSymbol.doAfterTextChanged { checkAndEnableSaveItem() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkAndEnableSaveItem() {
        val activity = requireActivity()
        val isSymbolNotEmpty = binding.acAssetSymbol.text.isNotEmpty()
        val isQuantityNotEmpty = binding.etQuantity.text.isNotEmpty()

        if (activity is MainActivity)
            activity.setupSaveItemMenu(isSymbolNotEmpty && isQuantityNotEmpty)
    }

}