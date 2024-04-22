package com.omouravictor.invest_view.util

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.invest_view.R

object AppUtil {

    fun getGenericNetworkErrorMessage(context: Context, e: Exception): String {
        return when (e) {
            is java.net.UnknownHostException -> context.getString(R.string.noInternetConnection)
            is java.net.SocketTimeoutException -> context.getString(R.string.checkInternetConnection)
            else -> context.getString(R.string.somethingWentWrong)
        }
    }

    fun showSnackBar(
        view: View,
        message: String,
        isSuccess: Boolean = false,
        isError: Boolean = false
    ) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        val context = view.context
        val backgroundTintColor = when {
            isSuccess -> context.getColor(R.color.green)
            isError -> context.getColor(R.color.red)
            else -> context.getColor(R.color.appPrimary)
        }
        val textColor = when {
            isSuccess || isError -> context.getColor(R.color.white)
            else -> context.getColor(R.color.appTextColor)
        }

        snackbar.setBackgroundTint(backgroundTintColor)
        snackbar.setTextColor(textColor)
        snackbar.show()
    }

}