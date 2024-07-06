package com.omouravictor.invest_view.util

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.invest_view.R

fun Activity.toolbarIsVisible(isVisible: Boolean) {
    this.findViewById<Toolbar>(R.id.toolbar).isVisible = isVisible
}

fun Activity.setupToolbarCenterText(centerText: String) {
    this.findViewById<Toolbar>(R.id.toolbar).apply {
        title = ""
        findViewById<TextView>(R.id.tvToolbarCenterText).text = centerText
    }
}

fun Activity.showErrorSnackBar(message: String, hasCloseAction: Boolean = false) {
    val view = this.findViewById<View>(android.R.id.content)
    val context = view.context
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    val isBottomNavVisible = view.findViewById<View>(R.id.bottomNav)?.isVisible ?: false

    if (isBottomNavVisible)
        snackbar.setAnchorView(R.id.bottomNav)

    if (hasCloseAction) {
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).apply {
            isAllCaps = false
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        snackbar.duration = Snackbar.LENGTH_INDEFINITE
        snackbar.setAction(this.getString(R.string.understood)) { snackbar.dismiss() }
    }

    snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.red))
    snackbar.setTextColor(ContextCompat.getColor(context, R.color.white))
    snackbar.show()
}

fun Activity.showSuccessSnackBar(message: String) {
    val view = this.findViewById<View>(android.R.id.content)
    val context = view.context
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
    val isBottomNavVisible = view.findViewById<View>(R.id.bottomNav)?.isVisible ?: false

    if (isBottomNavVisible)
        snackbar.setAnchorView(R.id.bottomNav)

    snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.green))
    snackbar.setTextColor(ContextCompat.getColor(context, R.color.white))
    snackbar.show()
}