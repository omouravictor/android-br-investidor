package com.omouravictor.invest_view.presenter.wallet.assets.asset_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.invest_view.databinding.FragmentAssetSearchBinding

class AssetSearchFragment : Fragment() {

    private var _binding: FragmentAssetSearchBinding? = null
    private val binding get() = _binding!!
    private val assetSearchViewModel: AssetSearchViewModel by activityViewModels()
    private val assetTypeUiArg by lazy {
        AssetSearchFragmentArgs.fromBundle(requireArguments()).assetTypeUi
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssetSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.svAssetSearch.postDelayed({ binding.svAssetSearch.onActionViewExpanded() }, 50)
        binding.svAssetSearch.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { assetSearchViewModel.getAssetsBySearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText.let {
                    if (it.isNullOrEmpty())
                        binding.recyclerView.scrollToPosition(0)
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}