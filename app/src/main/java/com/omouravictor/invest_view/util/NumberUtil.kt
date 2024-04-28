package com.omouravictor.invest_view.util

import kotlin.math.round

object NumberUtil {

    fun getRoundedFloat(value: Float) = round(value * 100) / 100

}