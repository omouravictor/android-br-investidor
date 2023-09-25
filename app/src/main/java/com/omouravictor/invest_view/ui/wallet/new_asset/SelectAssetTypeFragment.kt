package com.omouravictor.invest_view.ui.wallet.new_asset

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSelectAssetTypeBinding

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
            AssetTypeUiModel(
                name = "Fundos Imobiliários",
                circleColor = R.color.green
            ),
            AssetTypeUiModel(
                name = "Ações",
                circleColor = R.color.red
            ),
            AssetTypeUiModel(
                name = "ETFs",
                circleColor = R.color.yellow
            ),
        )

        binding.recyclerView.apply {
            adapter = AssetTypeAdapter(
                assetTypes = items,
                onClickItem = {
                    findNavController()
                        .navigate(R.id.action_fragment_select_asset_type_to_fragment_select_asset)
                }
            )
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}