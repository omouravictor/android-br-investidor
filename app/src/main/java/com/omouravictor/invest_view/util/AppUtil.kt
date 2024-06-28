package com.omouravictor.invest_view.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.invest_view.R

object AppUtil {

    fun getGenericErrorMessage(context: Context, e: Exception): String {
        return when (e) {
            is java.net.UnknownHostException -> context.getString(R.string.noInternetConnection)
            is java.net.SocketTimeoutException -> context.getString(R.string.checkInternetConnection)
            else -> context.getString(R.string.somethingWentWrong)
        }
    }

    fun showSuccessSnackBar(activity: Activity, message: String) {
        val view = activity.findViewById<View>(android.R.id.content)
        val context = view.context
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        val isBottomNavVisible = view.findViewById<View>(R.id.bottomNav)?.isVisible ?: false

        if (isBottomNavVisible)
            snackbar.setAnchorView(R.id.bottomNav)

        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.green))
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.white))
        snackbar.show()
    }

    fun showErrorSnackBar(activity: Activity, message: String, hasCloseAction: Boolean = false) {
        val view = activity.findViewById<View>(android.R.id.content)
        val context = view.context
        val snackbar = Snackbar.make(view, message, 7000)
        val isBottomNavVisible = view.findViewById<View>(R.id.bottomNav)?.isVisible ?: false

        if (isBottomNavVisible)
            snackbar.setAnchorView(R.id.bottomNav)

        if (hasCloseAction) {
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).apply {
                isAllCaps = false
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            snackbar.duration = Snackbar.LENGTH_INDEFINITE
            snackbar.setAction(activity.getString(R.string.understood)) { snackbar.dismiss() }
        }

        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.red))
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.white))
        snackbar.show()
    }

    fun showPieChart(
        context: Context,
        pieChart: PieChart,
        pieDataSet: PieDataSet
    ) {
        val textSize12 = 12f
        val whiteColor = ContextCompat.getColor(context, R.color.white)
        val appWindowBackColor = ContextCompat.getColor(context, R.color.appWindowBackColor)
        val appTextColor = ContextCompat.getColor(context, R.color.appTextColor)
        val boldTypeface = Typeface.DEFAULT_BOLD
        val pieData = PieData(pieDataSet).apply {
            setValueFormatter(PercentFormatter(pieChart))
            setValueTextSize(textSize12)
            setValueTextColor(whiteColor)
        }

        pieChart.apply {
            data = pieData
            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = false
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleAlpha(100)
            setTransparentCircleColor(appWindowBackColor)
            setEntryLabelTextSize(textSize12)
            setEntryLabelTypeface(boldTypeface)
            setCenterTextColor(appTextColor)
            setCenterTextSize(16f)
            animateY(1400, Easing.EaseInOutQuad)
        }
    }

}