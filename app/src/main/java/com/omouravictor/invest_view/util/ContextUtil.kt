package com.omouravictor.invest_view.util

import android.content.Context
import com.google.firebase.FirebaseNetworkException
import com.omouravictor.invest_view.R
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Context.getGenericErrorMessage(e: Exception?): String {
    return when (e) {
        is UnknownHostException -> getString(R.string.noInternetConnection)
        is FirebaseNetworkException -> getString(R.string.noInternetConnection)
        is SocketTimeoutException -> getString(R.string.checkInternetConnection)
        is HttpException -> {
            when (e.code()) {
                429 -> getString(R.string.rateLimitPerMinuteExceeded)
                else -> getString(R.string.somethingWentWrong)
            }
        }

        else -> getString(R.string.somethingWentWrong)
    }
}