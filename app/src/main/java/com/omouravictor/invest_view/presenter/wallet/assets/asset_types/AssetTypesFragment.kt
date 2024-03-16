package com.omouravictor.invest_view.presenter.wallet.assets.asset_types

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.databinding.FragmentAssetTypesBinding
import com.omouravictor.invest_view.presenter.wallet.assets.asset_types.model.AssetTypeUiModel
import com.omouravictor.invest_view.util.AssetTypeUtil.getAssetTypeList

class AssetTypesFragment : Fragment() {

    private var _binding: FragmentAssetTypesBinding? = null
    private val binding get() = _binding!!
    private val assetTypeAdapter = AssetTypeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssetTypesBinding.inflate(inflater, container, false)
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
        assetTypeAdapter.updateItemsList(assetTypeList)
        assetTypeAdapter.updateOnClickItem(::navigateToSaveAssetFragment)

        binding.recyclerView.apply {
            adapter = assetTypeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun navigateToSaveAssetFragment(assetType: AssetTypeUiModel) {
        findNavController().navigate(
            AssetTypesFragmentDirections.navToSaveAssetFragment(assetType)
        )
    }

}