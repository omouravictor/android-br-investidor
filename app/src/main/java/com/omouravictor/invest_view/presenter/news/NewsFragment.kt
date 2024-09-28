package com.omouravictor.invest_view.presenter.news

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
import com.omouravictor.invest_view.databinding.FragmentNewsBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.news.article.ArticleAdapter
import com.omouravictor.invest_view.presenter.news.model.ArticleUiModel
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.hideKeyboard
import com.omouravictor.invest_view.util.setupRecyclerViewWithLinearLayout
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsFragment : Fragment(R.layout.fragment_news) {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var searchView: SearchView
    private val newsViewModel: NewsViewModel by activityViewModels()
    private val articleAdapter = ArticleAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewsBinding.bind(view)
        addMenuProvider()
        setupAdapterAndRecyclerView()
        observeNewsListUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
                    newsViewModel.getNewsList(query)
                } else {
                    activity.showErrorSnackBar(message = getString(R.string.enterSubjectForTryAgain))
                }
            }
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { newsViewModel.getNewsList(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.recyclerView.scrollToPosition(0)
                return true
            }
        }

        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.searchASubject)
        searchView.setOnQueryTextListener(queryTextListener)
    }

    private fun setupAdapterAndRecyclerView() {
        val navController = findNavController()
        articleAdapter.updateOnClickItem { articleUiModel ->
            navController.navigate(NewsFragmentDirections.navToArticleFragment(articleUiModel))
        }

        binding.recyclerView.setupRecyclerViewWithLinearLayout(articleAdapter)
    }

    private fun observeNewsListUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                newsViewModel.getNewsListUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> handleNewsListLoading()
                        is UiState.Success -> handleNewsListSuccess(it.data)
                        is UiState.Error -> handleNewsListError(it.e)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun handleNewsListLoading() {
        binding.shimmerLayout.isVisible = true
        binding.shimmerLayout.startShimmer()
        binding.recyclerView.isVisible = false
        binding.incLayoutError.root.isVisible = false
    }

    private fun handleNewsListSuccess(results: List<ArticleUiModel>) {
        binding.shimmerLayout.isVisible = false
        binding.shimmerLayout.stopShimmer()

        val isResultsEmpty = results.isEmpty()

        binding.recyclerView.isVisible = !isResultsEmpty

        if (isResultsEmpty) {
            binding.incLayoutError.root.isVisible = true
            binding.incLayoutError.tvInfoMessage.text = getString(R.string.noResultsFound)
            return
        }

        articleAdapter.setList(results)
    }

    private fun handleNewsListError(e: Exception) {
        binding.shimmerLayout.isVisible = false
        binding.shimmerLayout.stopShimmer()
        binding.recyclerView.isVisible = false
        binding.incLayoutError.root.isVisible = true
        binding.incLayoutError.tvInfoMessage.text = requireContext().getGenericErrorMessage(e)
    }

}