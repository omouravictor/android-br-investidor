package com.omouravictor.invest_view.presenter.wallet.assets

import android.graphics.Color.TRANSPARENT
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
import com.omouravictor.invest_view.R
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
        val context = requireContext()
        val pieEntries = arrayListOf<PieEntry>()
        val colors = arrayListOf<Int>()

        assetsViewModel.currentAssetTypes.forEach { assetType ->
            val count = assetsViewModel.currentAssets.count { it.assetType.name == assetType.name }
            pieEntries.add(PieEntry(count.toFloat(), assetType.getName(context)))
            colors.add(assetType.getColor(context).defaultColor)
        }

        val dataSet = PieDataSet(pieEntries, "Tipos de Ativos")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

        val data = PieData(dataSet)
        val whiteColor = context.getColor(R.color.white)
        val textSize12 = 12f
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(textSize12)
        data.setValueTextColor(whiteColor)

        val assetsSize = assetsViewModel.currentAssets.size
        val appWindowBackColor = context.getColor(R.color.appWindowBackColor)
        val greenColor = context.getColor(R.color.green)
        val appTextColor = context.getColor(R.color.appTextColor)
        val pieChart = binding.pieChart

        pieChart.data = data
        pieChart.setUsePercentValues(true)
        pieChart.description.text = "Resumo de ativos"
        pieChart.description.textColor = appTextColor
        pieChart.description.textSize = textSize12
        pieChart.description.xOffset = 0f
        pieChart.description.yOffset = 0f

        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(TRANSPARENT)
        pieChart.setTransparentCircleAlpha(100)
        pieChart.setTransparentCircleColor(appWindowBackColor)
        pieChart.centerText = if (assetsSize == 1) "$assetsSize\nativo" else "$assetsSize\nativos"
        pieChart.setEntryLabelTextSize(textSize12)
        pieChart.setCenterTextColor(greenColor)
        pieChart.setCenterTextSize(16f)
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.legend.isEnabled = false
        pieChart.isRotationEnabled = false
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