package com.omouravictor.invest_view.presenter.wallet.base

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getYield

class SpinnerForAssetSort(private val spinner: Spinner) {

    private var onItemSelected: (Int) -> Unit = {}

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
                onItemSelected(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    fun setOnItemSelected(action: (Int) -> Unit) {
        onItemSelected = action
    }

    fun getFilteredAssetList(assetList: List<AssetUiModel>): List<AssetUiModel> {
        return when (spinner.selectedItemPosition) {
            SORT_BY_HIGHEST_YIELD -> assetList.sortedByDescending { it.getYield() }
            SORT_BY_HIGHEST_AMOUNT -> assetList.sortedByDescending { it.amount }
            SORT_BY_LOWEST_YIELD -> assetList.sortedBy { it.getYield() }
            SORT_BY_LOWEST_AMOUNT -> assetList.sortedBy { it.amount }
            SORT_BY_SYMBOL -> assetList.sortedBy { it.symbol }
            else -> assetList
        }
    }
}