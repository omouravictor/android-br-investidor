package com.omouravictor.invest_view.presenter.wallet.asset_search

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetSearchBinding
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_search.model.AssetBySearchUiModel

class AssetSearchFragment : Fragment() {

    private lateinit var binding: FragmentAssetSearchBinding
    private val assetBySearchViewModel: AssetBySearchViewModel by activityViewModels()
    private val assetBySearchAdapter = AssetBySearchAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMenuProvider()
        setupRecyclerView()
        observeAssetsBySearch()
    }

    override fun onStop() {
        super.onStop()
        assetBySearchViewModel.clearAssetsBySearch()
    }

    private fun addMenuProvider() {
        requireActivity().addMenuProvider(
            object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.options_menu_search, menu)
                    val searchMenuItem = menu.findItem(R.id.searchAsset)
                    val searchView = searchMenuItem.actionView as SearchView
                    setupSearchView(searchView)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false

            }, viewLifecycleOwner
        )
    }

    private fun setupSearchView(searchView: SearchView) {
        val capCharactersInputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { assetBySearchViewModel.getAssetsBySearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) searchView.inputType = capCharactersInputType
                binding.tvResultsInfo.isVisible = false
                binding.recyclerView.scrollToPosition(0)
                return true
            }
        }

        searchView.onActionViewExpanded()
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.searchYourAsset)
        searchView.inputType = capCharactersInputType
        searchView.setOnQueryTextListener(queryTextListener)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetBySearchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeAssetsBySearch() {
        assetBySearchViewModel.assetsBySearch.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Empty -> Unit
                is UiState.Loading -> handleAssetsBySearchLoading()
                is UiState.Success -> handleAssetsBySearchSuccess(it.data)
                is UiState.Error -> handleAssetsBySearchError(it.message)
            }
        }
    }

    private fun handleAssetsBySearchLoading() {
        setupViews(isLoading = true)
    }

    private fun handleAssetsBySearchSuccess(assetsBySearchList: List<AssetBySearchUiModel>) {
        setupViews(resultsVisible = assetsBySearchList.isNotEmpty())
        assetBySearchAdapter.updateItemsList(assetsBySearchList)
    }

    private fun handleAssetsBySearchError(message: String) {
        setupViews(resultsVisible = false)
        binding.tvResultsInfo.text = message
    }

    private fun setupViews(
        isLoading: Boolean = false,
        resultsVisible: Boolean = false,
    ) {
        if (isLoading) {
            setupLoading()
        } else {
            setupNoLoading()
            if (resultsVisible) {
                setupResults()
            } else {
                setupNoResults()
            }
        }
    }

    private fun setupLoading() {
        binding.recyclerView.isVisible = false
        binding.tvResultsInfo.isVisible = false
        binding.shimmerLayout.isVisible = true
        binding.shimmerLayout.startShimmer()
    }

    private fun setupNoLoading() {
        binding.shimmerLayout.isVisible = false
        binding.shimmerLayout.stopShimmer()
    }

    private fun setupResults() {
        binding.recyclerView.isVisible = true
        binding.tvResultsInfo.isVisible = false
    }

    private fun setupNoResults() {
        binding.recyclerView.isVisible = false
        binding.tvResultsInfo.isVisible = true
        binding.tvResultsInfo.text = getString(R.string.noResultsFound)
    }

}