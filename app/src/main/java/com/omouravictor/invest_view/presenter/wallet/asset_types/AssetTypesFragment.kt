package com.omouravictor.invest_view.presenter.wallet.asset_types

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetsBinding
import com.omouravictor.invest_view.presenter.wallet.WalletFragmentDirections
import com.omouravictor.invest_view.presenter.wallet.AssetsListViewModel
import com.omouravictor.invest_view.util.showPieChart

class AssetTypesFragment : Fragment(), OnChartValueSelectedListener {

    private lateinit var binding: FragmentAssetsBinding
    private lateinit var pieChart: PieChart
    private val assetsListViewModel: AssetsListViewModel by activityViewModels()
    private val assetTypesAdapter = AssetTypesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetTypesAdapter.apply {
            updateItemsList(assetsListViewModel.assetsListStateFlow.value)
            updateOnClickItem { findNavController().navigate(WalletFragmentDirections.navToAssetDetailFragment(it)) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetsBinding.inflate(inflater, container, false)
        pieChart = binding.pieChart.also { it.setOnChartValueSelectedListener(this) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPieChart()
        setupRecyclerView()
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val assetType = (e as PieEntry).label
        val filteredAssets =
            assetsListViewModel.assetsListStateFlow.value.filter { getString(it.assetType.nameResId) == assetType }

        updatePieChartCenterText(filteredAssets.size)
        assetTypesAdapter.updateItemsList(filteredAssets)
    }

    override fun onNothingSelected() {
        val assetsList = assetsListViewModel.assetsListStateFlow.value
        updatePieChartCenterText(assetsList.size)
        assetTypesAdapter.updateItemsList(assetsList)
    }

    private fun updatePieChartCenterText(assetSize: Int) {
        val assetText = getString(if (assetSize == 1) R.string.asset else R.string.assets)
        val spannableString = SpannableString("$assetSize\n$assetText").apply {
            setSpan(StyleSpan(Typeface.ITALIC), 0, length, 0)
        }

        pieChart.centerText = spannableString
    }

    private fun setupPieChart() {
        val context = requireContext()
        val assetsList = assetsListViewModel.assetsListStateFlow.value
        val assetTypes = assetsList.map { it.assetType }.distinct()
        val colorsList = arrayListOf<Int>()
        val pieEntries = assetTypes.map { assetType ->
            colorsList.add(assetType.colorResId)
            val count = assetsList.count { it.assetType == assetType }
            PieEntry(count.toFloat(), getString(assetType.nameResId))
        }
        val pieDataSet = PieDataSet(pieEntries, getString(R.string.assetTypes)).apply {
            colors = colorsList.map { ContextCompat.getColor(context, it) }
            sliceSpace = 3f
            setDrawIcons(false)
        }

        context.showPieChart(pieChart, pieDataSet)
        updatePieChartCenterText(assetsList.size)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetTypesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

}