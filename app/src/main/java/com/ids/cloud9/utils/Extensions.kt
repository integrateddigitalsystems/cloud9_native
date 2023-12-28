package com.ids.cloud9.utils




import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.ids.cloud9.BuildConfig
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import java.util.regex.Matcher
import java.util.regex.Pattern


fun Any.wtf(message: String) {
    debugOnly {
        Log.wtf(this::class.java.simpleName, message)
    }

}
fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
inline fun debugOnly(block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block()
    }
}
fun Activity.createActionDialog(positiveButton: String, message: String, cancelable:Boolean?=true, doAction: () -> Unit){
    val builder = AlertDialog.Builder(this)
    builder
        .setMessage(message)
        .setCancelable(cancelable!!)
        .setPositiveButton(positiveButton) { dialog, _ ->
            doAction()
        }
    val alert = builder.create()
    alert.show()
}

fun Location.toText():String{
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}
fun String.checkIfNull():String{
    if(this.isNullOrEmpty())
        return ""
    else
        return this
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

fun TextView.toHTML(string: String){
    this.text = Html.fromHtml(string,Html.FROM_HTML_MODE_LEGACY)
}
fun Activity.createDialog( message: String) {

    val builder = AlertDialog.Builder(this)
    builder
        .setMessage(message)
        .setCancelable(true)
        .setNegativeButton(this.getString(R.string.ok)) { dialog, _ -> dialog.cancel() }
    val alert = builder.create()
    alert.show()
}
fun Context.createRetryDialog(message: String,doAction: () -> Unit){
    val builder = AlertDialog.Builder(this)
    builder
        .setMessage(message)
        .setCancelable(false)
        .setNegativeButton(this.getString(R.string.ok)) { dialog, _ ->
           doAction()
        }
    val alert = builder.create()
    alert.show()
}
fun Context.createReverseDialog(message: String,posButton : String ,position: Int, doAction: (position: Int) -> Unit) {

    val builder = AlertDialog.Builder(this)
    builder
        .setMessage(message)
        .setCancelable(true)
        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            doAction(position)
        }
        .setPositiveButton(posButton) { dialog, _ ->
            dialog.cancel()
        }
    val alert = builder.create()
    alert.show()
}
fun Context.createActionDialog(message: String,posButton : String ,position: Int, doAction: (position: Int) -> Unit) {

    val builder = AlertDialog.Builder(this)
    builder
        .setMessage(message)
        .setCancelable(true)
        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        .setPositiveButton(posButton) { dialog, _ ->
            doAction(position)
        }
    val alert = builder.create()
    alert.show()
}
fun Context.createActionDialog(message: String,position: Int, doAction: (position: Int) -> Unit) {

    val builder = AlertDialog.Builder(this)
    builder
        .setMessage(message)
        .setCancelable(true)
        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            doAction(position)
        }
    val alert = builder.create()
    alert.show()
}
fun Context.createActionDialogCancel(message: String,position: Int, doAction: (position: Int) -> Unit, doActionCancel: (position: Int) -> Unit) {

    val builder = AlertDialog.Builder(this)
    builder
        .setMessage(message)
        .setCancelable(true)
        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            doActionCancel(position)
        }
        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            doAction(position)
        }
    val alert = builder.create()
    alert.show()
}
fun Fragment.createDialog(message: String) {

    val builder = AlertDialog.Builder(requireActivity())
    builder
        .setMessage(message)
        .setCancelable(true)
        .setNegativeButton(this.getString(R.string.ok)) { dialog, _ -> dialog.cancel() }
    val alert = builder.create()
    alert.show()
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

fun View.invisible() {
    try{ visibility = View.INVISIBLE}catch (e:Exception){}
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

inline fun <R> safeCall(loading : View ,call: () -> R):Result<R>{
    try {
        return Result.success(call())
    } catch (e: Exception) {
        loading.hide()
        FirebaseCrashlytics.getInstance().log("Try/Catch exception")
        FirebaseCrashlytics.getInstance().recordException(RuntimeException("Try/Catch exception"+"\n"+e.toString()))
        return Result.failure(e)
    }
}
inline fun <R> safeCall(call: () -> R): Result<R> {
    try {
        return Result.success(call())
    } catch (e: Exception) {
        Log.wtf("TAG_JAD_WEB",e.toString())
        return Result.failure(e)
    }
}

inline fun releaseOnly(block: () -> Unit) {
    if (!BuildConfig.DEBUG) {
        block()
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


