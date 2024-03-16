package com.omouravictor.invest_view.util

import android.app.Activity
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

object SystemServiceUtil {

    fun hideKeyboard(activity: Activity, view: View) {
        view.clearFocus()
        WindowCompat.getInsetsController(activity.window, view).hide(WindowInsetsCompat.Type.ime())
    }

    fun showKeyboard(activity: Activity, view: View) {
        view.requestFocus()
        WindowCompat.getInsetsController(activity.window, view).show(WindowInsetsCompat.Type.ime())
    }

}