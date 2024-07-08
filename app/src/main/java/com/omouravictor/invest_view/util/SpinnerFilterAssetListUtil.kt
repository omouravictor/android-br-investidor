package com.omouravictor.invest_view.util

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getYield

object SpinnerFilterAssetListUtil {
    private const val HIGHER_YIELD_FILTER = 0
    private const val HIGHER_AMOUNT_FILTER = 1
    private const val LOWER_YIELD_FILTER = 2
    private const val LOWER_AMOUNT_FILTER = 3
    private const val SYMBOL_FILTER = 4

    fun setupSpinnerFilterForAssetList(spinner: Spinner, onItemSelected: (Int) -> Unit) {
        val context = spinner.context
        val spinnerItems = context.resources.getStringArray(R.array.spinnerAssetFilterOptions)
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

    fun getFilteredAssetList(spinner: Spinner, assetList: List<AssetUiModel>): List<AssetUiModel> {
        return getFilteredAssetList(spinner.selectedItemPosition, assetList)
    }

    fun getFilteredAssetList(itemPosition: Int, assetList: List<AssetUiModel>): List<AssetUiModel> {
        return when (itemPosition) {
            HIGHER_YIELD_FILTER -> assetList.sortedByDescending { it.getYield() }
            HIGHER_AMOUNT_FILTER -> assetList.sortedByDescending { it.amount }
            LOWER_YIELD_FILTER -> assetList.sortedBy { it.getYield() }
            LOWER_AMOUNT_FILTER -> assetList.sortedBy { it.amount }
            SYMBOL_FILTER -> assetList.sortedBy { it.symbol }
            else -> assetList
        }
    }
}