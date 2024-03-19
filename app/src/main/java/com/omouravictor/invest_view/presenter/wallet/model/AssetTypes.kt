package com.omouravictor.invest_view.presenter.wallet.model

import com.omouravictor.invest_view.R

enum class AssetTypes(val description: Int, val color: Int) {
    EQUITY(R.string.stocks, R.color.equity),
    MUTUAL_FUND(R.string.investment_funds, R.color.mutual_fund)
}