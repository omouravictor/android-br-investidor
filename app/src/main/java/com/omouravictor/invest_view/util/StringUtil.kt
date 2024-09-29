package com.omouravictor.invest_view.util

fun String.getOnlyNumbers() = this.replace("\\D".toRegex(), "")

fun String.getMonetaryValueInDouble(): Double = (this.getOnlyNumbers().toDoubleOrNull() ?: 0.0) / 100

fun String.getDoubleValue(): Double = this.getOnlyNumbers().toDoubleOrNull() ?: 0.0