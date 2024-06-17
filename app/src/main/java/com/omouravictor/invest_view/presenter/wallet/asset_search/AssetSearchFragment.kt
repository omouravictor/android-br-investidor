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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetSearchBinding
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.util.AppUtil
import com.omouravictor.invest_view.util.AppUtil.showErrorSnackBar
import com.omouravictor.invest_view.util.SystemServiceUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AssetSearchFragment : Fragment() {

    private lateinit var binding: FragmentAssetSearchBinding
    private lateinit var searchView: SearchView
    private lateinit var assetBySearchUiModel: AssetBySearchUiModel
    private val walletViewModel: WalletViewModel by activityViewModels()
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
        assetSearchViewModel.resetAssetQuoteLiveData()
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

        binding.incLayoutError.btnTryAgain.setOnClickListener {
            val query = searchView.query.toString()
            if (query.isNotEmpty()) {
                assetSearchViewModel.getAssetsBySearch(query)
            } else {
                showErrorSnackBar(activity, message = getString(R.string.enterAssetSymbolForTryAgain))
            }
        }
    }

    private fun setupAdapter() {
        assetBySearchAdapter.updateOnClickItem { assetBySearchUiModel ->
            val symbol = assetBySearchUiModel.symbol
            val existingAsset = walletViewModel.assetsListStateFlow.value.find { it.symbol == symbol }
            if (existingAsset == null) {
                this.assetBySearchUiModel = assetBySearchUiModel
                assetSearchViewModel.getAssetQuote(symbol)
            } else {
                showErrorSnackBar(requireActivity(), getString(R.string.assetAlreadyExists))
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
        searchView.setQuery("VALE", true) // TODO: Remove this line after testing
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetBySearchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun handleErrors(e: Exception) {
        setupViewsForAssetsBySearch(isError = true)
        binding.incLayoutError.tvInfoMessage.text = AppUtil.getGenericErrorMessage(requireContext(), e)
    }

    private fun observeAssetsBySearch() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetSearchViewModel.assetsBySearchListStateFlow.collectLatest {
                    when (it) {
                        is UiState.Initial -> Unit
                        is UiState.Loading -> setupViewsForAssetsBySearch(isLoading = true)
                        is UiState.Success -> {
                            val assetsBySearchList = it.data
                            setupViewsForAssetsBySearch(isSuccessResultsEmpty = assetsBySearchList.isEmpty())
                            assetBySearchAdapter.updateItemsList(assetsBySearchList)
                        }

                        is UiState.Error -> handleErrors(it.e)
                    }
                }
            }
        }
    }

    private fun observeAssetQuote() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetSearchViewModel.assetQuoteStateFlow.collectLatest {
                    when (it) {
                        is UiState.Initial -> Unit
                        is UiState.Loading -> {
                            binding.recyclerView.isVisible = false
                            binding.progressBar.isVisible = true
                        }

                        is UiState.Success -> {
                            assetBySearchUiModel.price = it.data.price
                            findNavController().navigate(
                                AssetSearchFragmentDirections.navToSaveAssetFragment(assetBySearchUiModel)
                            )
                        }

                        is UiState.Error -> handleErrors(it.e)
                    }
                }
            }
        }
    }

    private fun setupViewsForAssetsBySearch(
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

}