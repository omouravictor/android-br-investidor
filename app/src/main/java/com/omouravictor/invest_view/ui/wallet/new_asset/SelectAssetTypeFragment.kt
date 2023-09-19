package com.omouravictor.invest_view.ui.wallet.new_asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSelectAssetTypeBinding
import com.omouravictor.invest_view.ui.base.ItemListUiModel

class SelectAssetTypeFragment : Fragment() {

    private var _binding: FragmentSelectAssetTypeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectAssetTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            ItemListUiModel("Ações", R.drawable.ic_stock),
            ItemListUiModel("Fundos Imobiliários", R.drawable.ic_stock),
        )

        binding.recyclerView.apply {
            adapter = AssetTypeAdapter(items)
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}