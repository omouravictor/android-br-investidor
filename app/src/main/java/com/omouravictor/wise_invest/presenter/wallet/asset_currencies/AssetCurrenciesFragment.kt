package com.omouravictor.wise_invest.presenter.wallet.asset_currencies

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
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.FragmentAssetsBinding
import com.omouravictor.wise_invest.presenter.wallet.WalletFragmentDirections
import com.omouravictor.wise_invest.presenter.wallet.WalletViewModel
import com.omouravictor.wise_invest.presenter.wallet.asset.AssetUiModel
import com.omouravictor.wise_invest.presenter.wallet.model.AssetFilterHelper
import com.omouravictor.wise_invest.util.AssetUtil
import com.omouravictor.wise_invest.util.setupPieChart
import com.omouravictor.wise_invest.util.setupRecyclerViewWithLinearLayout
import com.omouravictor.wise_invest.util.updateCenterText

class AssetCurrenciesFragment : Fragment(R.layout.fragment_assets), OnChartValueSelectedListener {

    private lateinit var originalAssetList: List<AssetUiModel>
    private lateinit var binding: FragmentAssetsBinding
    private lateinit var pieChart: PieChart
    private lateinit var assetFilterHelper: AssetFilterHelper
    private val assetCurrenciesAdapter = AssetCurrenciesAdapter()
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        originalAssetList = walletViewModel.assetList.value
        assetCurrenciesAdapter.setList(originalAssetList)
        assetCurrenciesAdapter.updateOnClickItem {
            navController.navigate(WalletFragmentDirections.navToAssetDetailFragment(it))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssetsBinding.bind(view)
        assetFilterHelper = AssetFilterHelper(binding.spinnerFilter, assetCurrenciesAdapter)
        setupPieChart()
        binding.recyclerView.setupRecyclerViewWithLinearLayout(assetCurrenciesAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        assetFilterHelper.updateAdapterAndPieChart(originalAssetList, pieChart)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val currency = (e as PieEntry).label
        val filteredList = originalAssetList.filter { it.currency == currency }

        assetFilterHelper.updateAdapterAndPieChart(filteredList, pieChart)
    }

    override fun onNothingSelected() {
        assetFilterHelper.updateAdapterAndPieChart(originalAssetList, pieChart)
    }

    private fun setupPieChart() {
        pieChart = binding.pieChart.also { it.setOnChartValueSelectedListener(this) }

        val context = requireContext()
        val currencyList = originalAssetList.map { it.currency }.distinct()
        val colorList = arrayListOf<Int>()
        val pieEntryList = currencyList.map { currency ->
            colorList.add(AssetUtil.getCurrencyResColor(currency))
            val count = originalAssetList.count { it.currency == currency }
            PieEntry(count.toFloat(), currency)
        }
        val pieDataSet = PieDataSet(pieEntryList, getString(R.string.currencies)).apply {
            colors = colorList.map { ContextCompat.getColor(context, it) }
            sliceSpace = 3f
            setDrawIcons(false)
        }

        pieChart.setupPieChart(pieDataSet)
        pieChart.updateCenterText(originalAssetList.size)
    }

}