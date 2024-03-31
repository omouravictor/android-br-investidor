package com.omouravictor.invest_view.presenter.wallet.save_asset

import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.util.NumberUtil
import com.omouravictor.invest_view.util.StringUtil

class SaveViewModel() : ViewModel() {

    fun getTotalAssetPrice(price: Double, quantity: String): Double {
        return price * StringUtil.getOnlyNumbers(quantity).toInt()
    }

    fun getTotalInvested(totalInvested: String): Double {
        return StringUtil.getOnlyNumbers(totalInvested).toDouble() / 100
    }

    fun getAppreciation(totalAssetPrice: Double, totalInvested: Double): Pair<Double, Double> {
        val appreciation = NumberUtil.getRoundedDouble(totalAssetPrice - totalInvested)
        val appreciationPercent = appreciation / totalInvested
        return Pair(appreciation, appreciationPercent)
    }

}