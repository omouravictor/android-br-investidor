package com.omouravictor.invest_view.util

import android.content.Context
import com.omouravictor.invest_view.R

object AppUtil {

    fun getGenericNetworkErrorMessage(context: Context, e: Exception): String {
        return when (e) {
            is java.net.UnknownHostException -> context.getString(R.string.noInternetConnection)
            is java.net.SocketTimeoutException -> context.getString(R.string.checkInternetConnection)
            else -> context.getString(R.string.somethingWentWrong)
        }
    }

}