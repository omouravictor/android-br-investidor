package com.omouravictor.invest_view.presenter.wallet.save_asset

import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.util.StringUtil

class SaveAssetViewModel : ViewModel() {

    fun getTotalAssetPrice(price: Double, quantity: String): Double {
        return price * StringUtil.getOnlyNumbers(quantity).toLong()
    }

    fun getTotalInvested(totalInvested: String): Double {
        return StringUtil.getOnlyNumbers(totalInvested).toDouble() / 100
    }

}