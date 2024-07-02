package com.omouravictor.invest_view.util

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import com.omouravictor.invest_view.R

fun Context.getGenericErrorMessage(e: Exception): String {
    return when (e) {
        is java.net.UnknownHostException -> this.getString(R.string.noInternetConnection)
        is java.net.SocketTimeoutException -> this.getString(R.string.checkInternetConnection)
        else -> this.getString(R.string.somethingWentWrong)
    }
}

fun Context.showPieChart(
    pieChart: PieChart,
    pieDataSet: PieDataSet
) {
    val textSize12 = 12f
    val whiteColor = ContextCompat.getColor(this, R.color.white)
    val appWindowBackColor = ContextCompat.getColor(this, R.color.appWindowBackColor)
    val appTextColor = ContextCompat.getColor(this, R.color.appTextColor)
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