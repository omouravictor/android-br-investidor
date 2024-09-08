package com.omouravictor.invest_view.presenter.news

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
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSearchBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.hideKeyboard
import com.omouravictor.invest_view.util.setupRecyclerViewWithLinearLayout
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchView: SearchView
    private val newsViewModel: NewsViewModel by activityViewModels()
    private val newsAdapter = NewsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMenuProvider()
        setupAdapterAndRecyclerView()
        observeNewsBySearchListUiState()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().hideKeyboard(searchView)
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
                    newsViewModel.loadNewsBySearch(query)
                } else {
                    activity.showErrorSnackBar(message = getString(R.string.enterSubjectForTryAgain))
                }
            }
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { newsViewModel.loadNewsBySearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.recyclerView.scrollToPosition(0)
                return true
            }
        }

        searchView.onActionViewExpanded()
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.searchASubject)
        searchView.setOnQueryTextListener(queryTextListener)
    }

    private fun setupAdapterAndRecyclerView() {
        newsAdapter.updateOnClickItem { articleUiModel ->
            findNavController().navigate(SearchFragmentDirections.navToArticleFragment(articleUiModel))
        }

        binding.recyclerView.setupRecyclerViewWithLinearLayout(newsAdapter)
    }

    private fun observeNewsBySearchListUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                newsViewModel.newsBySearchListUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> setupViewsForAssetsBySearch(isLoading = true)
                        is UiState.Success -> {
                            val newsBySearchList = it.data
                            setupViewsForAssetsBySearch(isSuccessResultsEmpty = newsBySearchList.isEmpty())
                            newsAdapter.setList(newsBySearchList)
                        }

                        is UiState.Error -> handleErrors(it.e)
                        else -> Unit
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
        binding.incProgressBar.root.isVisible = false
        binding.shimmerLayout.isVisible = isLoading
        binding.incLayoutError.tvInfoMessage.isVisible = isSuccessResultsEmpty || isError
        binding.incLayoutError.incBtnTryAgain.root.isVisible = isError
        binding.recyclerView.isVisible = !isLoading && !isSuccessResultsEmpty && !isError

        if (isLoading) binding.shimmerLayout.startShimmer()
        else binding.shimmerLayout.stopShimmer()

        if (isSuccessResultsEmpty) binding.incLayoutError.tvInfoMessage.text = getString(R.string.noResultsFound)
    }

    private fun handleErrors(e: Exception) {
        setupViewsForAssetsBySearch(isError = true)
        binding.incLayoutError.tvInfoMessage.text = requireContext().getGenericErrorMessage(e)
    }

}