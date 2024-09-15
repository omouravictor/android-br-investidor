package com.omouravictor.invest_view.presenter.wallet.asset_types

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetsBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.WalletFragmentDirections
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetFilterHelper
import com.omouravictor.invest_view.util.setupPieChart
import com.omouravictor.invest_view.util.setupRecyclerViewWithLinearLayout
import com.omouravictor.invest_view.util.updateCenterText

class AssetTypesFragment : Fragment(R.layout.fragment_assets), OnChartValueSelectedListener {

    private lateinit var originalAssetList: List<AssetUiModel>
    private lateinit var binding: FragmentAssetsBinding
    private lateinit var pieChart: PieChart
    private lateinit var assetFilterHelper: AssetFilterHelper
    private val assetTypesAdapter = AssetTypesAdapter()
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        originalAssetList = walletViewModel.assetList.value
        assetTypesAdapter.setList(originalAssetList)
        assetTypesAdapter.updateOnClickItem {
            findNavController().navigate(WalletFragmentDirections.navToAssetDetailFragment(it))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssetsBinding.bind(view)
        assetFilterHelper = AssetFilterHelper(binding.spinnerFilter, assetTypesAdapter)
        setupPieChart()
        binding.recyclerView.setupRecyclerViewWithLinearLayout(assetTypesAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        assetFilterHelper.updateAdapterAndPieChart(originalAssetList, pieChart)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val assetType = (e as PieEntry).label
        val filteredList = originalAssetList.filter { getString(it.type.nameResId) == assetType }

        assetFilterHelper.updateAdapterAndPieChart(filteredList, pieChart)
    }

    override fun onNothingSelected() {
        assetFilterHelper.updateAdapterAndPieChart(originalAssetList, pieChart)
    }

    private fun setupPieChart() {
        pieChart = binding.pieChart.also { it.setOnChartValueSelectedListener(this) }

        val context = requireContext()
        val assetTypeList = originalAssetList.map { it.type }.distinct()
        val colorList = arrayListOf<Int>()
        val pieEntryList = assetTypeList.map { assetType ->
            colorList.add(assetType.colorResId)
            val count = originalAssetList.count { it.type == assetType }
            PieEntry(count.toFloat(), getString(assetType.nameResId))
        }
        val pieDataSet = PieDataSet(pieEntryList, getString(R.string.assetTypes)).apply {
            colors = colorList.map { ContextCompat.getColor(context, it) }
            sliceSpace = 3f
            setDrawIcons(false)
        }

        pieChart.setupPieChart(pieDataSet)
        pieChart.updateCenterText(originalAssetList.size)
    }

}