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
import com.omouravictor.invest_view.presenter.base.model.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_search.model.AssetBySearchUiModel
import com.omouravictor.invest_view.util.SystemServiceUtil

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
        observeAssetsBySearchState()
    }

    override fun onStop() {
        super.onStop()
        assetBySearchViewModel.clearAssetsBySearch()
        SystemServiceUtil.hideKeyboard(requireActivity(), requireView())
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
        val queryTextListener = object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { assetBySearchViewModel.getAssetsBySearch(it) }
                SystemServiceUtil.hideKeyboard(requireActivity(), searchView)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) searchView.inputType = capCharactersInputType
                binding.recyclerView.scrollToPosition(0)
                return true
            }
        }

        searchView.onActionViewExpanded()
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.search_your_asset)
        searchView.inputType = capCharactersInputType
        searchView.setOnQueryTextListener(queryTextListener)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetBySearchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeAssetsBySearchState() {
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
        setupViews(shimmerLayoutVisible = true)
    }

    private fun handleAssetsBySearchSuccess(assetsBySearchList: List<AssetBySearchUiModel>) {
        setupViews(recyclerViewVisible = true)
        assetBySearchAdapter.updateItemsList(assetsBySearchList)
    }

    private fun handleAssetsBySearchError(message: String) {

    }

    private fun setupShimmer(
        shimmerLayoutVisible: Boolean = false,
    ) {
        if (shimmerLayoutVisible) {
            binding.shimmerLayout.isVisible = true
            binding.shimmerLayout.startShimmer()
        } else {
            binding.shimmerLayout.isVisible = false
            binding.shimmerLayout.stopShimmer()
        }
    }

    private fun setupViews(
        shimmerLayoutVisible: Boolean = false,
        recyclerViewVisible: Boolean = false,
    ) {
        setupShimmer(shimmerLayoutVisible)
        binding.recyclerView.isVisible = recyclerViewVisible
    }

}