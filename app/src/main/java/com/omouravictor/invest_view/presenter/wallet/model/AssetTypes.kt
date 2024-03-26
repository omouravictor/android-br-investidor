package com.omouravictor.invest_view.presenter.wallet.model

import android.content.Context
import android.content.res.ColorStateList
import com.omouravictor.invest_view.R

enum class AssetTypes(private val descriptionResId: Int, private val colorResId: Int) {

    UNKNOWN(R.string.asset, R.color.green),
    EQUITY(R.string.stocks, R.color.equity),
    MUTUAL_FUND(R.string.investmentFunds, R.color.mutualFund);

    fun getDescription(context: Context): String {
        return context.getString(descriptionResId)
    }

    fun getColor(): ColorStateList {
        return ColorStateList.valueOf(colorResId)
    }
}