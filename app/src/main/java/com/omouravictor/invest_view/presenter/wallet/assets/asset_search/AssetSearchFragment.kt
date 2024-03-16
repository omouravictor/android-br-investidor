package com.omouravictor.invest_view.presenter.wallet.assets.asset_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.databinding.FragmentAssetSearchBinding
import com.omouravictor.invest_view.presenter.base.UiResultState
import com.omouravictor.invest_view.presenter.wallet.assets.asset_search.model.AssetBySearchUiModel

class AssetSearchFragment : Fragment() {

    private var _binding: FragmentAssetSearchBinding? = null
    private val binding get() = _binding!!
    private val assetBySearchViewModel: AssetBySearchViewModel by activityViewModels()
    private val assetBySearchAdapter = AssetBySearchAdapter()
    private val assetTypeUiArg by lazy {
        AssetSearchFragmentArgs.fromBundle(requireArguments()).assetTypeUi
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssetSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity)
            .supportActionBar?.title = assetTypeUiArg.description

        setupAssetSearchView()
        setupRecyclerView()
        observeAssetSearchResults()
    }

    private fun setupAssetSearchView() {
        binding.svAssetSearch.postDelayed({ binding.svAssetSearch.onActionViewExpanded() }, 50)

        binding.svAssetSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { assetBySearchViewModel.getAssetsBySearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty())
                    binding.recyclerView.scrollToPosition(0)
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetBySearchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeAssetSearchResults() {
        assetBySearchViewModel.assetsBySearch.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UiResultState.Success -> handleUiResultStateSuccess(result.data)
                is UiResultState.Error -> handleUiResultStateError(result.message)
                is UiResultState.Loading -> handleUiResultStateLoading()
            }
        }
    }

    private fun handleUiResultStateSuccess(assetsBySearchList: List<AssetBySearchUiModel>) {
        assetBySearchAdapter.updateItemsList(assetsBySearchList)
    }

    private fun handleUiResultStateError(message: String) {

    }

    private fun handleUiResultStateLoading() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}