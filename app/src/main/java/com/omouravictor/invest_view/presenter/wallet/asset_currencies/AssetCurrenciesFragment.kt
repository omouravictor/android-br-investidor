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
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getYield
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.showPieChart

class AssetCurrenciesFragment : Fragment(), OnChartValueSelectedListener {

    private lateinit var binding: FragmentAssetsBinding
    private lateinit var pieChart: PieChart
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val originalAssetList by lazy { walletViewModel.assetList.value }
    private val assetCurrenciesAdapter = AssetCurrenciesAdapter()

    companion object {
        private const val SPINNER_ITEM_YIELD = 0
        private const val SPINNER_ITEM_AMOUNT = 1
        private const val SPINNER_ITEM_SYMBOL = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetCurrenciesAdapter.apply {
            setList(originalAssetList)
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
        val filteredList = originalAssetList.filter { it.currency == currency }

        updatePieChartCenterText(filteredList.size)
        assetCurrenciesAdapter.setList(filteredList)
        filterAssetListBySpinnerItem(binding.spinner.selectedItemPosition, filteredList)
    }

    override fun onNothingSelected() {
        updatePieChartCenterText(originalAssetList.size)
        assetCurrenciesAdapter.setList(originalAssetList)
        filterAssetListBySpinnerItem(binding.spinner.selectedItemPosition, originalAssetList)
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

        context.showPieChart(pieChart, pieDataSet)
        updatePieChartCenterText(originalAssetList.size)
    }

    private fun filterAssetListBySpinnerItem(itemPosition: Int, assetList: List<AssetUiModel>) {
        val filteredList = when (itemPosition) {
            SPINNER_ITEM_YIELD -> assetList.sortedByDescending { it.getYield() }
            SPINNER_ITEM_AMOUNT -> assetList.sortedByDescending { it.amount }
            SPINNER_ITEM_SYMBOL -> assetList.sortedBy { it.symbol }
            else -> assetList
        }

        assetCurrenciesAdapter.setList(filteredList)
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
                    filterAssetListBySpinnerItem(position, assetCurrenciesAdapter.getList())
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