package com.omouravictor.invest_view.presenter.wallet.assets

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.invest_view.databinding.FragmentAssetsBinding

class AssetsFragment : Fragment() {

    private lateinit var binding: FragmentAssetsBinding
    private val assetsViewModel: AssetsViewModel by activityViewModels()
    private val assetsAdapter = AssetsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        setupAdapter()
        setupRecyclerView()
        observeAssets()
    }

    private fun setupChart() {
        val chart = binding.pieChart
        val context = requireContext()
        val pieEntries = arrayListOf<PieEntry>()
        val colors = arrayListOf<Int>()

        assetsViewModel.currentAssetTypes.forEach { assetType ->
            val count = assetsViewModel.currentAssets.count { it.assetType.name == assetType.name }
            pieEntries.add(PieEntry(count.toFloat(), assetType.getName(context)))
            colors.add(assetType.getColor(context).defaultColor)
        }

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f

        val assetsSize = assetsViewModel.currentAssets.size
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.TRANSPARENT)
        chart.setTransparentCircleAlpha(100)
        chart.centerText = if (assetsSize == 1) "$assetsSize \nativo" else "$assetsSize \nativos"
        chart.animateY(1400, Easing.EaseInOutQuad)
        chart.legend.isEnabled = false
        chart.data = data
    }

    private fun setupAdapter() {
        assetsAdapter.updateOnClickItem {
            Snackbar.make(binding.root, "Item clicked: $it", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeAssets() {
        assetsViewModel.assetsList.observe(viewLifecycleOwner) {
            assetsAdapter.updateItemsList(it)
        }
    }

}