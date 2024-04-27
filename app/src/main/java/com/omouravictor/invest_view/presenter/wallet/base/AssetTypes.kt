package com.omouravictor.invest_view.presenter.wallet.base

import android.content.Context
import android.content.res.ColorStateList
import com.omouravictor.invest_view.R

enum class AssetTypes(private val nameResId: Int, private val colorResId: Int) {

    BRAZILIAN_STOCK(R.string.brazilianStock, R.color.brazilianStock),
    BDR(R.string.bdr, R.color.bdr),
    INVESTMENT_FUND(R.string.investmentFund, R.color.investmentFund),
    ETF(R.string.etf, R.color.etf),
    FOREIGN_STOCK(R.string.foreignStock, R.color.foreignStock),
    OTHER(R.string.other, R.color.other);

    fun getName(context: Context): String {
        return context.getString(nameResId)
    }

    fun getColor(context: Context): ColorStateList {
        return context.getColorStateList(colorResId)
    }
}