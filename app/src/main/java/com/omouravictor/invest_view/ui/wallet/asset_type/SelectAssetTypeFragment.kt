package com.omouravictor.invest_view.ui.wallet.asset_type

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.databinding.FragmentSelectAssetTypeBinding
import com.omouravictor.invest_view.util.AssetTypeUtil.getAssetTypeList

class SelectAssetTypeFragment : Fragment() {

    private var _binding: FragmentSelectAssetTypeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectAssetTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        val assetTypeList = getAssetTypeList(requireContext())
        val assetTypeAdapter = AssetTypeAdapter(assetTypeList) { navigateToSaveAssetFragment(it) }

        binding.recyclerView.apply {
            adapter = assetTypeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun navigateToSaveAssetFragment(assetType: AssetTypeUiModel) {
        findNavController().navigate(
            SelectAssetTypeFragmentDirections.navToSaveAssetFragment(assetType)
        )
    }

}