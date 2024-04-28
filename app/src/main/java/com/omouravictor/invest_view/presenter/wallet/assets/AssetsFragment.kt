package com.omouravictor.invest_view.presenter.wallet.assets

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        val assets = assetsViewModel.currentAssets
        val assetTypes = assetsViewModel.currentAssetTypes
        val assetsSize = assets.size
        val whiteColor = ContextCompat.getColor(context, R.color.white)
        val textSize12 = 12f
        val appWindowBackColor = ContextCompat.getColor(context, R.color.appWindowBackColor)
        val greenColor = ContextCompat.getColor(context, R.color.green)

        val pieEntries = assetTypes.map { assetType ->
            val count = assets.count { it.assetType.name == assetType.name }
            PieEntry(count.toFloat(), assetType.getName(context))
        }

        val colors = assetTypes.map { it.getColor(context) }

        val pieDataSet = PieDataSet(pieEntries, "Tipos de Ativos").apply {
            this.colors = colors
            sliceSpace = 3f
            xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        }

        val pieData = PieData(pieDataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(textSize12)
            setValueTextColor(whiteColor)
        }

        binding.pieChart.apply {
            data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(TRANSPARENT)
            setTransparentCircleAlpha(100)
            setTransparentCircleColor(appWindowBackColor)
            centerText = if (assetsSize == 1) "$assetsSize\nativo" else "$assetsSize\nativos"
            setEntryLabelTextSize(textSize12)
            setCenterTextColor(greenColor)
            setCenterTextSize(16f)
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = false
            isRotationEnabled = false
        }
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