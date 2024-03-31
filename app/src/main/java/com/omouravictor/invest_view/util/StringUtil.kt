package com.omouravictor.invest_view.util

object StringUtil {

    fun getOnlyNumbers(string: String) = string.replace("\\D".toRegex(), "")

}