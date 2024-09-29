package com.omouravictor.invest_view.util

fun String.getOnlyNumbers() = this.replace("\\D".toRegex(), "")

fun String.getDoubleValue(): Double {
    return if (isNotEmpty())
        getOnlyNumbers().toDouble() / 100
    else
        0.0
}