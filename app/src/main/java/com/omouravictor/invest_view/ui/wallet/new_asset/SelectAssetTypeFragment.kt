package com.omouravictor.invest_view.ui.wallet.new_asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSelectAssetTypeBinding

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
        val assetTypes = listOf(
            AssetTypeUiModel(name = "Fundos Imobiliários", color = R.color.green),
            AssetTypeUiModel(name = "Ações", color = R.color.red),
        )

        binding.recyclerView.apply {
            adapter = AssetTypeAdapter(
                items = assetTypes,
                onClickItem = { assetTypeAdapterOnClickItem(it) }
            )
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun assetTypeAdapterOnClickItem(assetType: AssetTypeUiModel) {
        findNavController().navigate(
            SelectAssetTypeFragmentDirections.selectAssetTypeFragmentToSelectAssetFragment(assetType.name)
        )
    }

}