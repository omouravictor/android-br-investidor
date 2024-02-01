package com.omouravictor.invest_view.ui.wallet.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omouravictor.invest_view.databinding.FragmentSearchAssetBinding

class SearchAssetFragment : Fragment() {

    private var _binding: FragmentSearchAssetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchAssetBinding.inflate(inflater, container, false)
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
//        val assetTypeList = getAssetTypeList(requireContext())
//        val assetTypeAdapter = AssetTypeAdapter(assetTypeList) { navigateToSaveAssetFragment(it) }
//
//        binding.recyclerView.apply {
//            adapter = assetTypeAdapter
//            layoutManager = LinearLayoutManager(context)
//        }
    }

}