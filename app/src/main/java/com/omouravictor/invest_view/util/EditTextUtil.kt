package com.omouravictor.invest_view.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.setEditTextLongNumberFormatMask() {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(cs: CharSequence?, s: Int, c: Int, a: Int) = Unit

        override fun onTextChanged(
            text: CharSequence, start: Int, before: Int, count: Int
        ) {
            if (text.isEmpty()) return

            removeTextChangedListener(this)

            val amount = text.toString().getOnlyNumbers().toLong()
            val formattedAmount = LocaleUtil.getFormattedValueForLongNumber(amount)
            setText(formattedAmount)
            setSelection(formattedAmount.length)

            addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable?) = Unit
    })
}

fun EditText.setEditTextCurrencyFormatMask(currency: String) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(cs: CharSequence?, s: Int, c: Int, a: Int) = Unit

        override fun onTextChanged(
            text: CharSequence, start: Int, before: Int, count: Int
        ) {
            if (text.isEmpty()) return

            var cleanText = text.toString().getOnlyNumbers()

            if (cleanText == "00") {
                setText("")
                return
            }

            if (cleanText.length == 15) cleanText = cleanText.take(14)

            removeTextChangedListener(this)

            val value = cleanText.toDouble() / 100
            val formattedAmount = LocaleUtil.getFormattedCurrencyValue(currency, value)
            setText(formattedAmount)
            setSelection(formattedAmount.length)

            addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable?) = Unit
    })
}