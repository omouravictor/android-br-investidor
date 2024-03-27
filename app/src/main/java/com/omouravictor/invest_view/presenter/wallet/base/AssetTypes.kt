package com.omouravictor.invest_view.presenter.wallet.base

import android.content.Context
import android.content.res.ColorStateList
import com.omouravictor.invest_view.R

enum class AssetTypes(private val descriptionResId: Int, private val colorResId: Int) {

    OTHER(R.string.others, R.color.gray),
    EQUITY(R.string.stocks, R.color.equity),
    MUTUAL_FUND(R.string.investmentFunds, R.color.mutualFund);

    fun getDescription(context: Context): String {
        return context.getString(descriptionResId)
    }

    fun getColor(context: Context): ColorStateList {
        return context.getColorStateList(colorResId)
    }
}