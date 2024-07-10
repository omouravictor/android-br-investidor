package com.omouravictor.invest_view.presenter.wallet.asset_types

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.omouravictor.invest_view.presenter.wallet.WalletFragmentDirections
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.base.SpinnerForAssetSort
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.util.setupPieChart
import com.omouravictor.invest_view.util.setupRecyclerViewWithLinearLayout
import com.omouravictor.invest_view.util.updateCenterText

class AssetTypesFragment : Fragment(), OnChartValueSelectedListener {

    private lateinit var originalAssetList: List<AssetUiModel>
    private lateinit var binding: FragmentAssetsBinding
    private lateinit var pieChart: PieChart
    private lateinit var spinnerForAssetSort: SpinnerForAssetSort
    private val assetTypesAdapter = AssetTypesAdapter()
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        originalAssetList = walletViewModel.assetList.value
        assetTypesAdapter.updateOnClickItem {
            findNavController().navigate(WalletFragmentDirections.navToAssetDetailFragment(it))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetsBinding.inflate(inflater, container, false)
        pieChart = binding.pieChart.also { it.setOnChartValueSelectedListener(this) }
        spinnerForAssetSort = SpinnerForAssetSort(binding.spinnerFilter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPieChart()
        setupSpinnerFilter()
        binding.recyclerView.setupRecyclerViewWithLinearLayout(assetTypesAdapter)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val assetType = (e as PieEntry).label
        val filteredList = originalAssetList.filter { getString(it.assetType.nameResId) == assetType }

        filterAssetListBySpinner(filteredList)
    }

    override fun onNothingSelected() {
        filterAssetListBySpinner(originalAssetList)
    }

    private fun setupPieChart() {
        val context = requireContext()
        val assetTypeList = originalAssetList.map { it.assetType }.distinct()
        val colorList = arrayListOf<Int>()
        val pieEntryList = assetTypeList.map { assetType ->
            colorList.add(assetType.colorResId)
            val count = originalAssetList.count { it.assetType == assetType }
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

    private fun setupSpinnerFilter() {
        spinnerForAssetSort.setOnItemSelected {
            val filteredList = spinnerForAssetSort.getFilteredAssetList(
                assetTypesAdapter.getList().ifEmpty { originalAssetList }
            )
            assetTypesAdapter.setList(filteredList)
        }
    }

    private fun filterAssetListBySpinner(assetList: List<AssetUiModel>) {
        val filteredList = spinnerForAssetSort.getFilteredAssetList(assetList)
        assetTypesAdapter.setList(filteredList)
        pieChart.updateCenterText(filteredList.size)
    }

}