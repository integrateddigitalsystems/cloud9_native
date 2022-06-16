package utils




import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.*
import com.ids.librascan.controller.MyApplication


fun CompoundButton.setCustomChecked(
    value: Boolean,
    listener: CompoundButton.OnCheckedChangeListener
) {
    setOnCheckedChangeListener(null)
    isChecked = value
    setOnCheckedChangeListener(listener)
}
fun Any.wtf(message: String) {
    if (MyApplication.showLogs)
        Log.wtf(this::class.java.simpleName, message)
}
fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.show() {
    try{ visibility = View.VISIBLE}catch (e:Exception){}
}

fun View.hide() {
    try{visibility = View.GONE}catch (e:Exception){}
}
fun String.occurrencesOf(sub: String): Int {
    var count = 0
    var last = 0
    while (last != -1) {
        last = this.indexOf(sub, last)
        if (last != -1) {
            count++
            last += sub.length
        }
    }
    return count
}



fun TextView.textRemote(key: String, con:Context) {
    if (MyApplication.localizeArray != null) {
        try {
            this.text = MyApplication.localizeArray!!.messages!!.find { it.localize_Key == key }!!
                .getMessage()
        } catch (e: Exception) {
            try {
                val resId = resources.getIdentifier(key, "string", con.packageName)
                this.text = resources.getString(resId)
            } catch (e: Exception) {
            }
        }

    }

}


