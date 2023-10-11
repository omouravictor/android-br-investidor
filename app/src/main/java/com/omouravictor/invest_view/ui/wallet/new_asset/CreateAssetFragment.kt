package com.omouravictor.invest_view.ui.wallet.new_asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.invest_view.MainActivity
import com.omouravictor.invest_view.databinding.FragmentCreateAssetBinding
import com.omouravictor.invest_view.ui.wallet.assets.AssetsViewModel

class CreateAssetFragment : Fragment() {

    private var _binding: FragmentCreateAssetBinding? = null
    private val binding get() = _binding!!
    private val assetsViewModel: AssetsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val assetTypeUiModel =
            CreateAssetFragmentArgs.fromBundle(requireArguments()).assetTypeUiModel

        (activity as AppCompatActivity).supportActionBar?.title = assetTypeUiModel.description

        when (activity) {
            is MainActivity -> handleMainActivity(activity as MainActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun areRequiredFieldsNotEmpty() =
        binding.acAssetSymbol.text.isNotEmpty() && binding.etQuantity.text.isNotEmpty()

    private fun handleMainActivity(mainActivity: MainActivity) {
        mainActivity.saveItemClickAction = {
            Toast.makeText(activity, "Save item", Toast.LENGTH_SHORT).show()
        }

        binding.acAssetSymbol.doAfterTextChanged {
            mainActivity.setupSaveItemMenu(areRequiredFieldsNotEmpty())
        }

        binding.etQuantity.doAfterTextChanged {
            mainActivity.setupSaveItemMenu(areRequiredFieldsNotEmpty())
        }
    }

}