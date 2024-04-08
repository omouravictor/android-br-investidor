package com.omouravictor.invest_view.presenter.wallet.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omouravictor.invest_view.databinding.FragmentAssetsBinding

class AssetsFragment : Fragment() {

    //    private val assetsViewModel: AssetsViewModel by activityViewModels()
    //    private val assetsAdapter = AssetsAdapter()
    private lateinit var binding: FragmentAssetsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupRecyclerView()
        observeAssets()
    }

    private fun setupAdapter() {
        //        assetBySearchAdapter.updateOnClickItem {
        //            assetBySearchDTO = it
        //            assetSearchViewModel.getAssetQuote(it.symbol)
        //        }
    }

    private fun setupRecyclerView() {
        //        binding.recyclerView.apply {
        //            adapter = assetBySearchAdapter
        //            layoutManager = LinearLayoutManager(context)
        //        }
    }

    private fun observeAssets() {
        //        assetsViewModel.assets.observe(viewLifecycleOwner) { uiState ->
        //            when (uiState) {
        //                is UiState.Empty -> handleAssetsEmpty()
        //                is UiState.Loading -> handleAssetsLoading()
        //                is UiState.Success -> handleAssetsSuccess(uiState.data)
        //                is UiState.Error -> handleAssetsError(uiState.message)
        //            }
        //        }
    }

}