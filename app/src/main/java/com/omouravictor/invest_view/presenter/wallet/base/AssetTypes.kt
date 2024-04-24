package com.omouravictor.invest_view.presenter.wallet.base

import android.content.Context
import android.content.res.ColorStateList
import com.omouravictor.invest_view.R

enum class AssetTypes(private val nameResId: Int, private val colorResId: Int) {

    OTHER(R.string.others, R.color.gray),
    STOCK(R.string.stock, R.color.stock),
    INVESTMENT_FUND(R.string.investmentFund, R.color.investmentFund),
    BDR(R.string.bdr, R.color.bdr);

    fun getName(context: Context): String {
        return context.getString(nameResId)
    }

    fun getColor(context: Context): ColorStateList {
        return context.getColorStateList(colorResId)
    }
}