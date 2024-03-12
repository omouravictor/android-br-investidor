package com.omouravictor.invest_view.util

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.omouravictor.invest_view.util.FormatUtil.BrazilianFormats.brCurrencyFormat

object EditTextUtil {

    fun setupEditTextCursorColor(
        editText: EditText,
        color: Int
    ) {
        if (Build.VERSION.SDK_INT >= 29) {
            editText.textCursorDrawable?.setTint(color)

        } else {
            try {
                val field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                field.isAccessible = true

                val cursorDrawableField = TextView::class.java.getDeclaredField("mEditor")
                cursorDrawableField.isAccessible = true
                val editor = cursorDrawableField.get(editText)

                val cursorDrawable =
                    ContextCompat.getDrawable(editText.context, field.getInt(editText))
                cursorDrawable?.setTint(color)

                val cursorDrawableField2 = editor.javaClass.getDeclaredField("mCursorDrawable")
                cursorDrawableField2.isAccessible = true
                cursorDrawableField2.set(editor, arrayOf(cursorDrawable, cursorDrawable))

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setupEditTextsHighLightColor(
        color: Int,
        vararg editTexts: EditText
    ) {
        editTexts.forEach { editText ->
            editText.highlightColor = color
        }
    }

    fun setupEditTextsAfterTextChanged(
        doAfterTextChangedFunction: () -> Unit,
        vararg editTexts: EditText
    ) {
        editTexts.forEach { editText ->
            editText.doAfterTextChanged {
                doAfterTextChangedFunction()
            }
        }
    }

    fun setupEditTextCurrencyFormat(editText: EditText) {
        with(editText) {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(cs: CharSequence?, s: Int, c: Int, a: Int) {}

                override fun onTextChanged(
                    text: CharSequence, start: Int, before: Int, count: Int
                ) {
                    if (text.isEmpty()) return

                    val cleanText = text.filter { it.isDigit() }.toString()

                    if (cleanText == "00") {
                        setText("")
                        return
                    }

                    removeTextChangedListener(this)

                    val amount = cleanText.toDouble() / 100
                    val formattedAmount = brCurrencyFormat.format(amount)
                    setText(formattedAmount)
                    setSelection(formattedAmount.length)

                    addTextChangedListener(this)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

}