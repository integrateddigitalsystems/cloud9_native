package com.ids.cloud9.utils




import android.app.Activity
import android.content.Context
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import com.ids.cloud9.controller.MyApplication


fun Any.wtf(message: String) {
    if (MyApplication.showLogs)
        Log.wtf(this::class.java.simpleName, message)
}
fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun TextView.setCheckText(message: String){
   try{
       if(!message.isNullOrEmpty())
           this.text = message
       else
           this.text = ""
   }catch (ex:Exception){
       this.text = ""
   }
}

fun TextView.setTextLang(messageEn:String , messageAr:String){
    if(MyApplication.languageCode.equals("en")){
        this.text = messageEn
    }else{
        this.text = messageAr
    }
}

fun View.show() {
    try{ visibility = View.VISIBLE}catch (e:Exception){}
}

fun View.hide() {
    try{visibility = View.GONE}catch (e:Exception){}
}

inline fun <R> safeCall(call: () -> R): Result<R> {
    try {
        return Result.success(call())
    } catch (e: Exception) {
        return Result.failure(e)
    }
}

fun String.toEditable(): Editable {
    return Editable.Factory.getInstance().newEditable(this)
}

fun TextView.textRemote(key: String, con:Context) {
    if (MyApplication.locMessages != null) {
        try {
            this.text = MyApplication.locMessages!!.find { it.key == key }!!
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


