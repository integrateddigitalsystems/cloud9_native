package com.ids.cloud9.widget

import android.graphics.Path
import com.ids.cloud9.utils.widget.Action
import java.io.Writer
import java.security.InvalidParameterException

class Move(val x: Float, val y: Float) : Action {

    override fun perform(path: Path) {
        path.moveTo(x, y)
    }

    override fun perform(writer: Writer) {
        writer.write("M$x,$y")
    }
}