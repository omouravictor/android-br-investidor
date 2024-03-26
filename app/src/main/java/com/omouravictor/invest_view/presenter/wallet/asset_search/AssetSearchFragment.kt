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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetSearchBinding
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.AssetDTO
import com.omouravictor.invest_view.presenter.wallet.asset_quote.AssetQuoteViewModel
import com.omouravictor.invest_view.presenter.wallet.asset_search.model.AssetBySearchUiModel

class AssetSearchFragment : Fragment() {

    private lateinit var binding: FragmentAssetSearchBinding
    private lateinit var searchView: SearchView
    private val assetSearchViewModel: AssetSearchViewModel by activityViewModels()
    private val assetQuoteViewModel: AssetQuoteViewModel by activityViewModels()
    private val assetBySearchAdapter = AssetBySearchAdapter()
    private val assetDTO = AssetDTO()

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
        setupAdapter()
        setupRecyclerView()
        observeAssetsBySearch()
        observeAssetQuote()
    }

    override fun onStop() {
        super.onStop()
        assetSearchViewModel.clearAssetsBySearch()
        assetQuoteViewModel.clearAssetQuote()
    }

    private fun addMenuProvider() {
        requireActivity().addMenuProvider(
            object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.options_menu_search, menu)
                    searchView = menu.findItem(R.id.searchAsset).actionView as SearchView
                    setupSearchView(searchView)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false

            }, viewLifecycleOwner
        )

        binding.btnTryAgain.setOnClickListener {
            searchView.setQuery(searchView.query, true)
        }
    }

    private fun setupAdapter() {
        assetBySearchAdapter.updateOnClickItem { assetBySearchUiModel ->
            assetDTO.assetBySearchUiModel = assetBySearchUiModel
            assetQuoteViewModel.getAssetQuote(assetBySearchUiModel.symbol)
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        val capCharactersInputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { assetSearchViewModel.getAssetsBySearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty())
                    searchView.inputType = capCharactersInputType
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
        assetSearchViewModel.assetsBySearch.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Empty -> Unit
                is UiState.Loading -> handleAssetsBySearchLoading()
                is UiState.Success -> handleAssetsBySearchSuccess(it.data)
                is UiState.Error -> handleAssetsBySearchError(it.message)
            }
        }
    }

    private fun observeAssetQuote() {
        assetQuoteViewModel.assetQuote.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Empty -> Unit
                is UiState.Loading -> Unit
                is UiState.Success -> {
                    assetDTO.assetQuoteUiModel = it.data
                    findNavController()
                        .navigate(AssetSearchFragmentDirections.navToSaveAssetFragment(assetDTO))
                }

                is UiState.Error -> Unit
            }
        }
    }

    private fun setupViews(
        isLoading: Boolean = false,
        isSuccessResultsEmpty: Boolean = false,
        isError: Boolean = false
    ) {
        binding.shimmerLayout.isVisible = isLoading
        binding.tvResultsInfo.isVisible = isSuccessResultsEmpty || isError
        binding.btnTryAgain.isVisible = isError
        binding.recyclerView.isVisible = !isLoading && !isSuccessResultsEmpty && !isError

        if (isLoading)
            binding.shimmerLayout.startShimmer()
        else
            binding.shimmerLayout.stopShimmer()

        if (isSuccessResultsEmpty)
            binding.tvResultsInfo.text = getString(R.string.noResultsFound)
    }

    private fun handleAssetsBySearchLoading() {
        setupViews(isLoading = true)
    }

    private fun handleAssetsBySearchSuccess(assetsBySearchList: List<AssetBySearchUiModel>) {
        setupViews(isSuccessResultsEmpty = assetsBySearchList.isEmpty())
        assetBySearchAdapter.updateItemsList(assetsBySearchList)
    }

    private fun handleAssetsBySearchError(message: String) {
        setupViews(isError = true)
        binding.tvResultsInfo.text = message
    }

}