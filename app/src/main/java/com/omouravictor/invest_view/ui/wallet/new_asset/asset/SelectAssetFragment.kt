package com.omouravictor.invest_view.ui.wallet.new_asset.asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.databinding.FragmentSelectAssetBinding

class SelectAssetFragment : Fragment() {

    private var _binding: FragmentSelectAssetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSupportActionBar()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSupportActionBar() {
        val assetType = arguments?.getString("assetTypeName")!!
        (requireActivity() as AppCompatActivity).supportActionBar?.title = assetType
    }

    private fun setupRecyclerView() {
        val assets = listOf(
            AssetUiModel(name = "PETR4", companyName = "Petrobras", cost = 25.0F),
            AssetUiModel(name = "VALE3", companyName = "Vale", cost = 100.0F)
        )

        binding.recyclerView.apply {
            adapter = AssetAdapter(
                items = assets,
                onClickItem = { assetAdapterOnClickItem(it) }
            )
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun assetAdapterOnClickItem(asset: AssetUiModel) {

    }

}