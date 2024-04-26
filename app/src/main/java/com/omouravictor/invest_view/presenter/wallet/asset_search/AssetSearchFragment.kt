package com.omouravictor.invest_view.presenter.wallet.asset_search

import android.os.Bundle
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
import com.omouravictor.invest_view.presenter.wallet.assets.AssetsViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetQuoteUiModel
import com.omouravictor.invest_view.util.AppUtil
import com.omouravictor.invest_view.util.SystemServiceUtil

class AssetSearchFragment : Fragment() {

    private lateinit var binding: FragmentAssetSearchBinding
    private lateinit var searchView: SearchView
    private lateinit var assetBySearchUiModel: AssetBySearchUiModel
    private val assetsViewModel: AssetsViewModel by activityViewModels()
    private val assetSearchViewModel: AssetSearchViewModel by activityViewModels()
    private val assetBySearchAdapter = AssetBySearchAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        SystemServiceUtil.hideKeyboard(requireActivity(), searchView)
        assetSearchViewModel.clearAssetQuoteLiveData()
    }

    private fun addMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_search, menu)
                searchView = menu.findItem(R.id.searchAsset).actionView as SearchView
                setupSearchView(searchView)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
        }, viewLifecycleOwner)

        binding.incLayoutError.btnTryAgain.setOnClickListener {
            val query = searchView.query.toString()
            if (query.isNotEmpty())
                assetSearchViewModel.getAssetsBySearch(query)
            else
                AppUtil.showSnackBar(requireActivity(), getString(R.string.enterAssetSymbolForTryAgain), isError = true)
        }
    }

    private fun setupAdapter() {
        assetBySearchAdapter.updateOnClickItem { assetBySearchUiModel ->
            val symbol = assetBySearchUiModel.symbol
            val existingAsset = assetsViewModel.currentAssetsList.find { it.symbol == symbol }
            if (existingAsset == null) {
                this.assetBySearchUiModel = assetBySearchUiModel
                assetSearchViewModel.getAssetQuote(symbol)
            } else {
                AppUtil.showSnackBar(requireActivity(), getString(R.string.assetAlreadyExists), isError = true)
            }
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { assetSearchViewModel.getAssetsBySearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.recyclerView.scrollToPosition(0)
                return true
            }
        }

        searchView.onActionViewExpanded()
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.searchYourAsset)
        searchView.setOnQueryTextListener(queryTextListener)
        searchView.setQuery("Microsoft", true) // TODO: Remove this line after testing
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
                is UiState.Error -> handleErrors(it.message)
            }
        }
    }

    private fun observeAssetQuote() {
        assetSearchViewModel.assetQuote.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Empty -> Unit
                is UiState.Loading -> handleAssetQuoteLoading()
                is UiState.Success -> handleAssetQuoteSuccess(it.data)
                is UiState.Error -> handleErrors(it.message)
            }
        }
    }

    private fun setupViews(
        isLoading: Boolean = false,
        isSuccessResultsEmpty: Boolean = false,
        isError: Boolean = false,
    ) {
        binding.progressBar.isVisible = false
        binding.shimmerLayout.isVisible = isLoading
        binding.incLayoutError.tvInfoMessage.isVisible = isSuccessResultsEmpty || isError
        binding.incLayoutError.btnTryAgain.isVisible = isError
        binding.recyclerView.isVisible = !isLoading && !isSuccessResultsEmpty && !isError

        if (isLoading) binding.shimmerLayout.startShimmer()
        else binding.shimmerLayout.stopShimmer()

        if (isSuccessResultsEmpty) binding.incLayoutError.tvInfoMessage.text = getString(R.string.noResultsFound)
    }

    private fun handleAssetsBySearchLoading() {
        setupViews(isLoading = true)
    }

    private fun handleAssetsBySearchSuccess(assetsBySearchList: List<AssetBySearchUiModel>) {
        setupViews(isSuccessResultsEmpty = assetsBySearchList.isEmpty())
        assetBySearchAdapter.updateItemsList(assetsBySearchList)
    }

    private fun handleErrors(message: String) {
        setupViews(isError = true)
        binding.incLayoutError.tvInfoMessage.text = message
    }

    private fun handleAssetQuoteLoading() {
        binding.recyclerView.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun handleAssetQuoteSuccess(assetQuote: AssetQuoteUiModel) {
        assetBySearchUiModel.price = assetQuote.price
        findNavController().navigate(AssetSearchFragmentDirections.navToSaveAssetFragment(assetBySearchUiModel))
    }

}