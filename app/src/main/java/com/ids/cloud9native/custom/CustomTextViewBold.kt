package com.ids.cloud9native.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.ids.cloud9native.utils.AppHelper


class CustomTextViewBold : AppCompatTextView {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context) : super(context) {
        init()
    }
    private fun init() {
        this.includeFontPadding = false
        if (!isInEditMode)
            typeface = AppHelper.getTypeFaceBold(context)
    }
}
