package com.omouravictor.invest_view.presenter.wallet.asset_types

import com.omouravictor.invest_view.R

enum class AssetTypes(val nameResId: Int, val descriptionResId: Int, val colorResId: Int) {

    BRAZILIAN_STOCK(R.string.brazilianStock, R.string.brazilianStockDescription, R.color.brazilianStock),
    BDR(R.string.localDepositaryReceipts, R.string.bdrDescription, R.color.bdr),
    FI(R.string.fi, R.string.fiDescription, R.color.fi),
    ETF(R.string.etf, R.string.etfDescription, R.color.etf),
    STOCK(R.string.stock, R.string.stockDescription, R.color.stock),
    OTHER(R.string.other, R.string.otherDescription, R.color.other);

}