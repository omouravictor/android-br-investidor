package com.omouravictor.invest_view.presenter.wallet.currencies

import com.omouravictor.invest_view.R

enum class Currencies(val descriptionResId: Int, val colorResId: Int) {
    USD(R.string.usd, R.color.usd),
    BRL(R.string.brl, R.color.brl),
    EUR(R.string.eur, R.color.eur),
    OTHER(R.string.other, R.color.other),
}