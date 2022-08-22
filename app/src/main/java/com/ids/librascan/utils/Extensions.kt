package com.ids.librascan.utils




import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import com.ids.librascan.controller.MyApplication


fun Any.wtf(message: String) {
    if (MyApplication.showLogs)
        Log.wtf(this::class.java.simpleName, message)
}
fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


fun View.show() {
    try{ visibility = View.VISIBLE}catch (e:Exception){}
}

fun View.hide() {
    try{visibility = View.GONE}catch (e:Exception){}
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


