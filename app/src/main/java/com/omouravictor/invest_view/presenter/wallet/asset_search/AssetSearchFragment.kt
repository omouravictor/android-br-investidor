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
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.asset.AssetViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
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
    private val assetViewModel: AssetViewModel by activityViewModels()
    private val assetBySearchAdapter = AssetBySearchAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssetSearchBinding.bind(view)
        addMenuProvider()
        setupAdapterAndRecyclerView()
        observeGetAssetsBySearchListUiState()
        observeGetUpdatedAssetUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().hideKeyboard(searchView)
        assetViewModel.resetGetUpdatedAssetUiState()
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
                    assetViewModel.getAssetsBySearch(query)
                } else {
                    activity.showErrorSnackBar(message = getString(R.string.enterAssetSymbolForTryAgain))
                }
            }
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { assetViewModel.getAssetsBySearch(it) }
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
                assetViewModel.getUpdatedAsset(assetUiModel)
            } else {
                requireActivity().showErrorSnackBar(getString(R.string.assetAlreadyExists))
            }
        }

        binding.recyclerView.setupRecyclerViewWithLinearLayout(assetBySearchAdapter)
    }

    private fun handleAssetsBySearchListLoading() {
        binding.apply {
            shimmerLayout.isVisible = true
            shimmerLayout.startShimmer()
            recyclerView.isVisible = false
            incLayoutError.root.isVisible = false
        }
    }

    private fun handleAssetsBySearchListSuccess(results: List<AssetUiModel>) {
        binding.shimmerLayout.isVisible = false
        binding.shimmerLayout.stopShimmer()

        val isResultsEmpty = results.isEmpty()

        binding.recyclerView.isVisible = !isResultsEmpty

        if (isResultsEmpty) {
            binding.incLayoutError.root.isVisible = true
            binding.incLayoutError.tvInfoMessage.text = getString(R.string.noResultsFound)
        }

        assetBySearchAdapter.setList(results)
    }

    private fun handleAssetsBySearchListError(e: Exception) {
        binding.apply {
            shimmerLayout.isVisible = false
            shimmerLayout.stopShimmer()
            recyclerView.isVisible = false
            incLayoutError.root.isVisible = true
            incLayoutError.tvInfoMessage.text = requireContext().getGenericErrorMessage(e)
        }
    }

    private fun observeGetAssetsBySearchListUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetViewModel.getAssetsBySearchListUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleAssetsBySearchListLoading()
                        is UiState.Success -> handleAssetsBySearchListSuccess(it.data)
                        is UiState.Error -> handleAssetsBySearchListError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun handleUpdatedAssetLoading() {
        binding.apply {
            recyclerView.isVisible = false
            incProgressBar.root.isVisible = true
        }
    }

    private fun handleUpdatedAssetSuccess(assetUiModel: AssetUiModel) {
        findNavController().navigate(AssetSearchFragmentDirections.navToSaveAssetFragment(assetUiModel))
    }

    private fun handleUpdatedAssetError(e: Exception) {
        binding.apply {
            recyclerView.isVisible = false
            incProgressBar.root.isVisible = false
            incLayoutError.root.isVisible = true
            incLayoutError.tvInfoMessage.text = requireContext().getGenericErrorMessage(e)
        }
    }

    private fun observeGetUpdatedAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetViewModel.getUpdatedAssetUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleUpdatedAssetLoading()
                        is UiState.Success -> handleUpdatedAssetSuccess(it.data)
                        is UiState.Error -> handleUpdatedAssetError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

}