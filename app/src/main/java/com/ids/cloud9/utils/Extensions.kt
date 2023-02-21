package com.ids.cloud9.utils




import android.app.Activity
import android.content.Context
import android.location.Location
import android.text.Editable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.ids.cloud9.BuildConfig
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import java.util.regex.Matcher
import java.util.regex.Pattern


fun Any.wtf(message: String) {
    if (BuildConfig.DEBUG)
        Log.wtf(this::class.java.simpleName, message)
}
fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Location.toText():String{
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
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
fun ImageView.setTintImage(color : Int){
    this.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.MULTIPLY);

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

fun String.isEmailValid(): Boolean {
    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

fun TextView.underline() {
    val spannableString = SpannableString(this.text).apply {
        setSpan(UnderlineSpan(), 0, this@underline.text.length, 0)
    }

    setText(spannableString)
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


