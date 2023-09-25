package com.omouravictor.invest_view.ui.wallet.new_asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.invest_view.databinding.FragmentSelectAssetBinding

class SelectAssetFragment : Fragment() {

    private var _binding: FragmentSelectAssetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            "KGWD11",
            "JFUH11",
            "ASLK11",
            "ASLK11",
            "ASLK11",
            "ASLK11",
            "ASLK11",
            "ASLK11",
            "ASLK11",
            "ASLK11",
            "ASLK11",
            "ASLK11",
            "ASLK11",
        )

        binding.recyclerView.apply {
            adapter = AssetAdapter(
                assets = items,
                onClickItem = {}
            )
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}