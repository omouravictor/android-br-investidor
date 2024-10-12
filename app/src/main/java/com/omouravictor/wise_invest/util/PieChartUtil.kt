package com.omouravictor.wise_invest.util

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import com.omouravictor.wise_invest.R

fun PieChart.setupPieChart(
    pieDataSet: PieDataSet
) {
    val textSize12 = 12f
    val whiteColor = ContextCompat.getColor(this.context, R.color.white)
    val appWindowBackColor = ContextCompat.getColor(this.context, R.color.appWindowBackColor)
    val appTextColor = ContextCompat.getColor(this.context, R.color.appTextColor)
    val boldTypeface = Typeface.DEFAULT_BOLD
    val pieData = PieData(pieDataSet).apply {
        setValueFormatter(PercentFormatter(this@setupPieChart))
        setValueTextSize(textSize12)
        setValueTextColor(whiteColor)
    }

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
    setCenterTextSize(20f)
    animateY(1400, Easing.EaseInOutQuad)
}

fun PieChart.updateCenterText(
    assetSize: Int
) {
    val spannableString = SpannableString(assetSize.toString()).apply {
        setSpan(ForegroundColorSpan(context.getColor(R.color.defaultButtonBackgroundColor)), 0, length, 0)
        setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
    }

    centerText = spannableString
}