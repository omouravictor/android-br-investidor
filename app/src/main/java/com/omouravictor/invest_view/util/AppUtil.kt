package com.omouravictor.invest_view.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.presenter.base.AssetTypes

object AppUtil {

    fun getGenericNetworkErrorMessage(context: Context, e: Exception): String {
        return when (e) {
            is java.net.UnknownHostException -> context.getString(R.string.noInternetConnection)
            is java.net.SocketTimeoutException -> context.getString(R.string.checkInternetConnection)
            else -> context.getString(R.string.somethingWentWrong)
        }
    }

    fun showSnackBar(
        activity: Activity,
        message: String,
        isSuccess: Boolean = false,
        isError: Boolean = false
    ) {
        val view = activity.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        val backgroundTintColor = when {
            isSuccess -> ContextCompat.getColor(view.context, R.color.green)
            isError -> ContextCompat.getColor(view.context, R.color.red)
            else -> ContextCompat.getColor(view.context, R.color.appPrimaryColor)
        }
        val textColor = when {
            isSuccess || isError -> ContextCompat.getColor(view.context, R.color.white)
            else -> ContextCompat.getColor(view.context, R.color.appTextColor)
        }
        val isBottomNavVisible = view.findViewById<View>(R.id.bottomNav)?.isVisible ?: false

        if (isBottomNavVisible)
            snackbar.setAnchorView(R.id.bottomNav)

        snackbar.setBackgroundTint(backgroundTintColor)
        snackbar.setTextColor(textColor)
        snackbar.show()
    }

    fun showInfoBottomSheetDialog(activity: Activity, assetTypes: AssetTypes) {
        with(BottomSheetDialog(activity, R.style.Theme_App_OverlayBottomSheetDialog)) {
            setContentView(R.layout.bottom_sheet_dialog_info)
            findViewById<ImageView>(R.id.ivTitle)!!.imageTintList =
                ContextCompat.getColorStateList(activity, assetTypes.colorResId)
            findViewById<TextView>(R.id.tvTitle)!!.text = activity.getString(assetTypes.nameResId)
            findViewById<TextView>(R.id.tvInfo)!!.text = activity.getString(assetTypes.descriptionResId)
            show()
        }
    }

}