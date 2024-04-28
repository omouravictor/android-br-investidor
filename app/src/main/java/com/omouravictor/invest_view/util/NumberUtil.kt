package com.omouravictor.invest_view.util

import kotlin.math.round

object NumberUtil {

    fun getRoundedDouble(value: Double) = round(value * 100) / 100

}