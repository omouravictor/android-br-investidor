package com.omouravictor.br_investidor.util

fun String.getOnlyNumbers() = this.replace("\\D".toRegex(), "")

fun String.getMonetaryValueInDouble(): Double = (this.getOnlyNumbers().toDoubleOrNull() ?: 0.0) / 100
