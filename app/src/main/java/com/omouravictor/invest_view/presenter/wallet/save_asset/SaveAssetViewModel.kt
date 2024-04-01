package com.omouravictor.invest_view.presenter.wallet.save_asset

import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.util.NumberUtil
import com.omouravictor.invest_view.util.StringUtil

class SaveAssetViewModel : ViewModel() {

    fun getTotalAssetPrice(price: Double, quantity: String): Double {
        return price * StringUtil.getOnlyNumbers(quantity).toInt()
    }

    fun getTotalInvested(totalInvested: String): Double {
        return StringUtil.getOnlyNumbers(totalInvested).toDouble() / 100
    }

    fun getVariation(totalAssetPrice: Double, totalInvested: Double): Pair<Double, Double> {
        val variation = NumberUtil.getRoundedDouble(totalAssetPrice - totalInvested)
        val percent = variation / totalInvested
        return Pair(variation, percent)
    }

}