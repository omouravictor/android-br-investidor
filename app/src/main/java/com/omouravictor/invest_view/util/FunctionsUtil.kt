package com.omouravictor.invest_view.util

import android.content.res.ColorStateList
import android.os.Build
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat

object FunctionsUtil {

    fun setupCursorColor(editText: EditText, colorResId: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            editText.textCursorDrawable?.apply { setTintList(colorResId) }

        } else {
            val field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true

            val cursorDrawableField = TextView::class.java.getDeclaredField("mEditor")
            cursorDrawableField.isAccessible = true
            val editor = cursorDrawableField.get(editText)

            val cursorDrawable = ContextCompat.getDrawable(editText.context, field.getInt(editText))
            cursorDrawable?.setTintList(colorResId)

            val cursorDrawableField2 = editor.javaClass.getDeclaredField("mCursorDrawable")
            cursorDrawableField2.isAccessible = true
            cursorDrawableField2.set(editor, arrayOf(cursorDrawable, cursorDrawable))
        }
    }

}