package com.omouravictor.invest_view.presenter.wallet.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.databinding.FragmentAssetsBinding
import com.omouravictor.invest_view.presenter.base.UiState

class AssetsFragment : Fragment() {

    private val assetsViewModel: AssetsViewModel by activityViewModels()
    private val assetsAdapter = AssetsAdapter()
    private lateinit var binding: FragmentAssetsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeAssets()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeAssets() {
        assetsViewModel.assets.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Empty -> {}
                is UiState.Loading -> {}
                is UiState.Success -> {}
                is UiState.Error -> {}
            }
        }
    }

}