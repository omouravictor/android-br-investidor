package com.omouravictor.invest_view.presenter.wallet.assets.asset_search

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetSearchBinding
import com.omouravictor.invest_view.presenter.base.UiResultState
import com.omouravictor.invest_view.presenter.wallet.assets.asset_search.model.AssetBySearchUiModel
import com.omouravictor.invest_view.util.SystemServiceUtil

class AssetSearchFragment : Fragment() {

    private var _binding: FragmentAssetSearchBinding? = null
    private val binding get() = _binding!!
    private val assetBySearchViewModel: AssetBySearchViewModel by activityViewModels()
    private val assetBySearchAdapter = AssetBySearchAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssetSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSearchViewOptionMenu()
        setupRecyclerView()
        observeAssetSearchResults()
    }

    override fun onStop() {
        super.onStop()
        SystemServiceUtil.hideKeyboard(requireActivity(), requireView())
    }

    private fun addSearchViewOptionMenu() {
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
        searchView.onActionViewExpanded()
        searchView.queryHint = getString(R.string.search_your_asset)
        searchView.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { assetBySearchViewModel.getAssetsBySearch(it) }
                SystemServiceUtil.hideKeyboard(requireActivity(), searchView)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
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