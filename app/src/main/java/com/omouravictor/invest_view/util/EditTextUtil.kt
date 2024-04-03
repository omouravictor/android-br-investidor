package com.omouravictor.invest_view.util

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged

object EditTextUtil {

    fun setEditTextsHighLightColor(
        color: Int, vararg editTexts: EditText
    ) {
        editTexts.forEach { editText ->
            editText.highlightColor = color
        }
    }

    fun setEditTextsAfterTextChanged(
        doAfterTextChangedFunction: () -> Unit, vararg editTexts: EditText
    ) {
        editTexts.forEach { editText ->
            editText.doAfterTextChanged {
                doAfterTextChangedFunction()
            }
        }
    }

    fun setEditTextCurrencyFormatMask(editText: EditText, currency: String) {
        editText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(cs: CharSequence?, s: Int, c: Int, a: Int) {}

                override fun onTextChanged(
                    text: CharSequence, start: Int, before: Int, count: Int
                ) {
                    if (text.isEmpty()) return

                    val cleanText = StringUtil.getOnlyNumbers(text.toString())

                    if (cleanText == "00") {
                        setText("")
                        return
                    }

                    removeTextChangedListener(this)

                    val amount = cleanText.toDouble() / 100
                    val formattedAmount = LocaleUtil.getFormattedValueForCurrency(currency, amount)
                    setText(formattedAmount)
                    setSelection(formattedAmount.length)

                    addTextChangedListener(this)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    fun setEditTextIntNumberFormatMask(editText: EditText) {
        editText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(cs: CharSequence?, s: Int, c: Int, a: Int) {}

                override fun onTextChanged(
                    text: CharSequence, start: Int, before: Int, count: Int
                ) {
                    if (text.isEmpty()) return

                    removeTextChangedListener(this)

                    val amount = StringUtil.getOnlyNumbers(text.toString()).toLong()
                    val formattedAmount = LocaleUtil.getFormattedValueForLongNumber(amount)
                    setText(formattedAmount)
                    setSelection(formattedAmount.length)

                    addTextChangedListener(this)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

}