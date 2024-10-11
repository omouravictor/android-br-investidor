package com.omouravictor.wise_invest.util

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.wise_invest.R

fun Activity.setupToolbarTitle(tittle: String?) {
    findViewById<Toolbar>(R.id.toolbar).title = tittle
}

fun Activity.setupToolbarCenterText(centerText: String) {
    findViewById<Toolbar>(R.id.toolbar).apply {
        title = null
        findViewById<TextView>(R.id.tvToolbarCenterText).text = centerText
    }
}

fun Activity.showErrorSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    hasCloseAction: Boolean = false,
    anchorResView: Int? = null
) {
    val view = this.findViewById<View>(android.R.id.content)
    val snackbar = Snackbar.make(view, message, duration)

    if (hasCloseAction) {
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).apply {
            isAllCaps = false
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        snackbar.duration = Snackbar.LENGTH_INDEFINITE
        snackbar.setAction(this.getString(R.string.understood)) { snackbar.dismiss() }
    }

    setSnackbarAnchorView(snackbar, view, anchorResView)
    snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.red))
    snackbar.setTextColor(ContextCompat.getColor(this, R.color.white))
    snackbar.show()
}

fun Activity.showSuccessSnackBar(message: String, anchorResView: Int? = null) {
    val view = this.findViewById<View>(android.R.id.content)
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)

    setSnackbarAnchorView(snackbar, view, anchorResView)
    snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.green))
    snackbar.setTextColor(ContextCompat.getColor(this, R.color.white))
    snackbar.show()
}

private fun setSnackbarAnchorView(snackbar: Snackbar, view: View, anchorResView: Int? = null) {
    if (anchorResView != null) {
        snackbar.setAnchorView(anchorResView)
    } else {
        if (view.findViewById<View>(R.id.bottomNav)?.isVisible == true)
            snackbar.setAnchorView(R.id.bottomNav)
    }
}