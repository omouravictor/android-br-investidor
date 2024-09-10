package com.omouravictor.invest_view.presenter.wallet.asset_search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetSearchBinding
import com.omouravictor.invest_view.presenter.model.AssetUiModel
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.hideKeyboard
import com.omouravictor.invest_view.util.setupRecyclerViewWithLinearLayout
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AssetSearchFragment : Fragment(R.layout.fragment_asset_search) {

    private lateinit var binding: FragmentAssetSearchBinding
    private lateinit var searchView: SearchView
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val assetSearchViewModel: AssetSearchViewModel by activityViewModels()
    private val assetBySearchAdapter = AssetBySearchAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssetSearchBinding.bind(view)
        addMenuProvider()
        setupAdapterAndRecyclerView()
        observeGetAssetListUiState()
        observeGetAssetUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().hideKeyboard(searchView)
        assetSearchViewModel.resetGetQuoteUiState()
        assetSearchViewModel.resetGetAssetUiState()
    }

    private fun addMenuProvider() {
        val activity = requireActivity()

        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_search, menu)
                searchView = menu.findItem(R.id.searchAsset).actionView as SearchView
                setupSearchView(searchView)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
        }, viewLifecycleOwner)

        binding.incLayoutError.incBtnTryAgain.root.apply {
            text = getString(R.string.tryAgain)
            setOnClickListener {
                val query = searchView.query.toString()
                if (query.isNotEmpty()) {
                    assetSearchViewModel.loadAssetsBySearch(query)
                } else {
                    activity.showErrorSnackBar(message = getString(R.string.enterAssetSymbolForTryAgain))
                }
            }
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { assetSearchViewModel.loadAssetsBySearch(it) }
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
    }

    private fun setupAdapterAndRecyclerView() {
        assetBySearchAdapter.updateOnClickItem { assetUiModel ->
            val existingAsset = walletViewModel.assetList.value.find { it.symbol == assetUiModel.symbol }
            if (existingAsset == null) {
                assetSearchViewModel.loadQuoteFor(assetUiModel)
            } else {
                requireActivity().showErrorSnackBar(getString(R.string.assetAlreadyExists))
            }
        }

        binding.recyclerView.setupRecyclerViewWithLinearLayout(assetBySearchAdapter)
    }

    private fun observeGetAssetListUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetSearchViewModel.getAssetListUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleSearchLoading()
                        is UiState.Success -> handleSearchSuccess(it.data)
                        is UiState.Error -> handleSearchError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun handleSearchLoading() {
        binding.shimmerLayout.isVisible = true
        binding.shimmerLayout.startShimmer()
        binding.recyclerView.isVisible = false
        binding.incLayoutError.root.isVisible = false
    }

    private fun handleSearchSuccess(results: List<AssetUiModel>) {
        binding.shimmerLayout.isVisible = false
        binding.shimmerLayout.stopShimmer()

        val isResultsEmpty = results.isEmpty()

        binding.recyclerView.isVisible = !isResultsEmpty

        if (isResultsEmpty) {
            binding.incLayoutError.root.isVisible = true
            binding.incLayoutError.tvInfoMessage.text = getString(R.string.noResultsFound)
            return
        }

        assetBySearchAdapter.setList(results)
    }

    private fun handleSearchError(e: Exception) {
        binding.shimmerLayout.isVisible = false
        binding.shimmerLayout.stopShimmer()
        binding.recyclerView.isVisible = false
        binding.incLayoutError.root.isVisible = true
        binding.incLayoutError.tvInfoMessage.text = requireContext().getGenericErrorMessage(e)
    }

    private fun observeGetAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetSearchViewModel.getAssetUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleQuoteLoading()
                        is UiState.Success -> handleQuoteSuccess(it.data)
                        is UiState.Error -> handleQuoteError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun handleQuoteLoading() {
        binding.recyclerView.isVisible = false
        binding.incProgressBar.root.isVisible = true
    }

    private fun handleQuoteSuccess(assetUiModel: AssetUiModel) {
        findNavController().navigate(AssetSearchFragmentDirections.navToSaveAssetFragment(assetUiModel))
    }

    private fun handleQuoteError(e: Exception) {
        binding.recyclerView.isVisible = false
        binding.incProgressBar.root.isVisible = false
        binding.incLayoutError.root.isVisible = true
        binding.incLayoutError.tvInfoMessage.text = e.message.toString()
    }

}