package com.omouravictor.invest_view.util

fun String.getOnlyNumbers() = this.replace("\\D".toRegex(), "")