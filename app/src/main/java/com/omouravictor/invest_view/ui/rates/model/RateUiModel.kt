package com.omouravictor.invest_view.ui.rates.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class RateUiModel(
    val currencyName: String,
    val currencyTerm: String,
    val unitaryRate: Double,
    val variation: Double,
    val rateDate: Date
) : Parcelable
