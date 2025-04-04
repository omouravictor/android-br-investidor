package com.omouravictor.br_investidor.presenter.news

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.omouravictor.br_investidor.R
import com.omouravictor.br_investidor.databinding.FragmentNewsBinding
import com.omouravictor.br_investidor.presenter.model.UiState
import com.omouravictor.br_investidor.presenter.news.article.ArticleAdapter
import com.omouravictor.br_investidor.presenter.news.article.ArticleUiModel
import com.omouravictor.br_investidor.util.getErrorMessage
import com.omouravictor.br_investidor.util.hideKeyboard
import com.omouravictor.br_investidor.util.setupRecyclerViewWithLinearLayout
import com.omouravictor.br_investidor.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsFragment : Fragment(R.layout.fragment_news) {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var searchView: SearchView
    private lateinit var activity: FragmentActivity
    private val newsViewModel: NewsViewModel by activityViewModels()
    private val articleAdapter = ArticleAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewsBinding.bind(view)
        addMenuProvider()
        setupAdapterAndRecyclerView()
        observeNewsListUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity.hideKeyboard(searchView)
    }

    private fun addMenuProvider() {
        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_news_search, menu)
                searchView = menu.findItem(R.id.searchNews).actionView as SearchView
                setupSearchView(searchView, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.resetSearchNews -> newsViewModel.getNewsList(getString(R.string.defaultNewsSearch))
                }
                return true
            }
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

    private fun setupSearchView(searchView: SearchView, menu: Menu) {
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Avoid duplicate requests when user clicks search button for the first time
                if (newsViewModel.getNewsListUiState.value is UiState.Loading) return false
                query?.let { newsViewModel.getNewsList(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.recyclerView.scrollToPosition(0)
                return true
            }
        }

        val resetSearchMenuItem = menu.findItem(R.id.resetSearchNews)

        searchView.apply {
            setOnCloseListener {
                resetSearchMenuItem.isVisible = true
                false
            }

            setOnSearchClickListener {
                resetSearchMenuItem.isVisible = false
            }

            maxWidth = Int.MAX_VALUE
            queryHint = getString(R.string.searchASubject)
            setOnQueryTextListener(queryTextListener)
        }
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
        binding.apply {
            shimmerLayout.isVisible = true
            shimmerLayout.startShimmer()
            recyclerView.isVisible = false
            incLayoutError.root.isVisible = false
        }
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
        binding.apply {
            shimmerLayout.isVisible = false
            shimmerLayout.stopShimmer()
            recyclerView.isVisible = false
            incLayoutError.root.isVisible = true
            incLayoutError.tvInfoMessage.text = requireContext().getErrorMessage(e)
        }
    }

}