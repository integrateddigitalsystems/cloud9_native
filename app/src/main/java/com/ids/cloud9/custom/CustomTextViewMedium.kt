package com.ids.cloud9.custom


import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.ids.cloud9.utils.AppHelper
import me.grantland.widget.AutofitHelper


class CustomTextViewMedium : AppCompatTextView {
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
            typeface = AppHelper.getTypeFace(context)
        AutofitHelper.create(this)

    }
}
