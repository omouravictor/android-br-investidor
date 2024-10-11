package com.omouravictor.wise_invest.presenter.wallet.model

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.github.mikephil.charting.charts.PieChart
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.wise_invest.presenter.wallet.asset.AssetUiModel
import com.omouravictor.wise_invest.presenter.wallet.asset.getYield
import com.omouravictor.wise_invest.util.updateCenterText

class AssetFilterHelper(
    private val spinner: Spinner,
    private val adapter: BaseRecyclerViewAdapter<AssetUiModel, *>
) {

    companion object {
        private const val SORT_BY_HIGHEST_YIELD = 0
        private const val SORT_BY_HIGHEST_AMOUNT = 1
        private const val SORT_BY_LOWEST_YIELD = 2
        private const val SORT_BY_LOWEST_AMOUNT = 3
        private const val SORT_BY_SYMBOL = 4
    }

    init {
        val context = spinner.context
        val spinnerItems = context.resources.getStringArray(R.array.assetSortTypesArray)
        val spinnerAdapter = ArrayAdapter(context, R.layout.spinner_text_view, spinnerItems)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val filteredList = getSortedAssetList(selectedItemPosition = position)
                adapter.setList(filteredList)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    fun updateAdapterAndPieChart(assetList: List<AssetUiModel>, pieChart: PieChart) {
        val filteredList = getSortedAssetList(assetList)
        adapter.setList(filteredList)
        pieChart.updateCenterText(filteredList.size)
    }

    private fun getSortedAssetList(
        assetList: List<AssetUiModel> = adapter.getList(),
        selectedItemPosition: Int = spinner.selectedItemPosition
    ): List<AssetUiModel> {
        return when (selectedItemPosition) {
            SORT_BY_HIGHEST_YIELD -> assetList.sortedByDescending { it.getYield() }
            SORT_BY_HIGHEST_AMOUNT -> assetList.sortedByDescending { it.amount }
            SORT_BY_LOWEST_YIELD -> assetList.sortedBy { it.getYield() }
            SORT_BY_LOWEST_AMOUNT -> assetList.sortedBy { it.amount }
            SORT_BY_SYMBOL -> assetList.sortedBy { it.symbol }
            else -> assetList
        }
    }
}