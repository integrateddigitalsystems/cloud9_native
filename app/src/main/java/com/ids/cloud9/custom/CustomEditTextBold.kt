package com.ids.molilo.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.ids.cloud9.utils.AppHelper


class CustomEditTextBold : AppCompatEditText {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }
    constructor(context: Context?) : super(context!!) {
        init()
    }
    private fun init() {
        this.includeFontPadding = false
        if (!isInEditMode) setTypeface(AppHelper.getTypeFaceExtraBold(context))
    }
}