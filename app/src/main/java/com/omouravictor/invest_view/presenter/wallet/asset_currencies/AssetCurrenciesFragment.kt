package com.omouravictor.invest_view.presenter.wallet.asset_currencies

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.model.getTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getYield
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.showPieChart

class AssetCurrenciesFragment : Fragment(), OnChartValueSelectedListener {

    private lateinit var binding: FragmentAssetsBinding
    private lateinit var pieChart: PieChart
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val assetCurrenciesAdapter = AssetCurrenciesAdapter()

    companion object {
        private const val FILTER_BY_TOTAL_PRICE = 0
        private const val FILTER_BY_YIELD = 1
        private const val FILTER_BY_AMOUNT = 2
        private const val FILTER_BY_SYMBOL = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetCurrenciesAdapter.apply {
            updateItemsList(walletViewModel.assetList.value)
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
        setupSpinner()
        setupRecyclerView()
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val currency = (e as PieEntry).label
        val filteredList = walletViewModel.assetList.value.filter { it.currency == currency }

        updatePieChartCenterText(filteredList.size)
        assetCurrenciesAdapter.updateItemsList(filteredList)
    }

    override fun onNothingSelected() {
        val assetList = walletViewModel.assetList.value
        updatePieChartCenterText(assetList.size)
        assetCurrenciesAdapter.updateItemsList(assetList)
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
        val assetList = walletViewModel.assetList.value
        val currencyList = assetList.map { it.currency }.distinct()
        val colorList = arrayListOf<Int>()
        val pieEntryList = currencyList.map { currency ->
            colorList.add(AssetUtil.getCurrencyResColor(currency))
            val count = assetList.count { it.currency == currency }
            PieEntry(count.toFloat(), currency)
        }
        val pieDataSet = PieDataSet(pieEntryList, getString(R.string.currencies)).apply {
            colors = colorList.map { ContextCompat.getColor(context, it) }
            sliceSpace = 3f
            setDrawIcons(false)
        }

        context.showPieChart(pieChart, pieDataSet)
        updatePieChartCenterText(assetList.size)
    }

    private fun setupSpinner() {
        val context = binding.spinner.context
        val spinnerItems = context.resources.getStringArray(R.array.assetFilterOptions)
        val spinnerAdapter = ArrayAdapter(context, R.layout.spinner_text_view, spinnerItems)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.spinner.apply {
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    walletViewModel.assetList.value.let { assetList ->
                        val filteredList = when (position) {
                            FILTER_BY_TOTAL_PRICE -> assetList.sortedByDescending { it.getTotalPrice() }
                            FILTER_BY_YIELD -> assetList.sortedByDescending { it.getYield() }
                            FILTER_BY_AMOUNT -> assetList.sortedByDescending { it.amount }
                            FILTER_BY_SYMBOL -> assetList.sortedBy { it.symbol }
                            else -> assetList
                        }

                        assetCurrenciesAdapter.updateItemsList(filteredList)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetCurrenciesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

}