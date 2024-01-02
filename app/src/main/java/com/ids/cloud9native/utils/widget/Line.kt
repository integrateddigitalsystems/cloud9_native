package com.ids.cloud9native.widget

import android.graphics.Path
import com.ids.cloud9native.utils.widget.Action

import java.io.Writer

class Line(val x: Float, val y: Float) : Action {

    override fun perform(path: Path) {
        path.lineTo(x, y)
    }

    override fun perform(writer: Writer) {
        writer.write("L$x,$y")
    }
}