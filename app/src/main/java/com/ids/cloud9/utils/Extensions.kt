package com.ids.cloud9.utils


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DimenRes
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ids.cloud9.R
import com.ids.mercury.controller.MyApplication


/**
 * Removes the listener of a checkbox temporarily to restore the chosen choice without calling the on Text change
 */
fun CompoundButton.setCustomChecked(value: Boolean, listener: CompoundButton.OnCheckedChangeListener) {
    setOnCheckedChangeListener(null)
    isChecked = value
    setOnCheckedChangeListener(listener)
}



/**
 * Calculate the occurrences of a certain string
 */
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

fun Any.wtf(message: String) {
    if (MyApplication.showLogs)
        Log.wtf(this::class.java.simpleName, message)
}

fun ImageView.setTint(color:Int){
    this.setColorFilter(getColor(this.context, color), android.graphics.PorterDuff.Mode.SRC_IN)
}
fun ImageButton.setBackgroundTint(color:Int){
    this.background.setTint(getColor(context, color))
}


fun ImageView.loadImagesUrlResize(url: String) {
    val options: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.rectangular_gray)
        .error(R.drawable.rectangular_gray)
        .override(500, 200)
         Glide.with(this).load(url).apply(options)
        .into(this)

}
fun ImageView.loadImagesUrl(url: String) {
    val options: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.rectangular_gray)
        .error(R.drawable.rectangular_gray)
         Glide.with(this).load(url).apply(options)
        .into(this)

}

fun Context.px(@DimenRes dimen: Int): Int = resources.getDimension(dimen).toInt()

fun Context.dp(@DimenRes dimen: Int): Float = px(dimen) / resources.displayMetrics.density

fun Int.toPx(context: Context) = this * context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT

fun Any.addFragment(
    container: Int,
    fragmentManager: FragmentManager,
    myFragment: Fragment,
    myTag: String
) {
    MyApplication.selectedFragmentTag = myTag
    fragmentManager.beginTransaction()
        .add(container, myFragment, myTag)
        .addToBackStack(null)
        .commit()
}
fun Any.replaceFragment(
    container: Int,
    fragmentManager: FragmentManager,
    myFragment: Fragment,
    myTag: String
) {
    for (i in 0 until fragmentManager.backStackEntryCount) {
        fragmentManager.popBackStack()
    }
    MyApplication.selectedFragmentTag = myTag
    MyApplication.selectedFragment = myFragment
    fragmentManager.beginTransaction()
        .replace(container, myFragment, myTag)
        .commit()
}


/**
 * Set the first character as upper case
 */
fun String.upperCaseFirstLetter(): String {
    return this.substring(0, 1).toUpperCase().plus(this.substring(1))
}

/**
 * Checks if String is numeric
 */
fun String.isNumeric(): Boolean {
    return this.matches("\\d+".toRegex())
}

/**
 * Used for simpler logging
 */

/**
 * Used for Images Loading
 */


/**
 * Used for Showing toasts
 */

fun Context.logw(key:String,value: String){
    try{
    if(MyApplication.showLogs)
        Log.wtf(key,value)}catch (e:java.lang.Exception){}
}


/*@SuppressLint("RestrictedApi")
fun AppCompatCheckBox.setCheckBoxColor(uncheckedColor: Int, checkedColor: Int) {
    val colorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-R.attr.state_checked), // unchecked
            intArrayOf(R.attr.state_checked)  // checked
        ),
        intArrayOf(uncheckedColor, checkedColor)
    )
    this.supportButtonTintList = colorStateList
}

@SuppressLint("RestrictedApi")
fun AppCompatRadioButton.setRadioButtonColor(uncheckedColor: Int, checkedColor: Int) {
    val colorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-R.attr.state_checked), // unchecked
            intArrayOf(R.attr.state_checked)  // checked
        ),
        intArrayOf(uncheckedColor, checkedColor)
    )
    this.supportButtonTintList = colorStateList
}*/



/**
 * Used for Showing and Hiding views
 */
fun View.show() {
   try{ visibility = View.VISIBLE}catch (e:Exception){}
}

fun View.hide() {
    try{visibility = View.GONE}catch (e:Exception){}
}

fun View.invisible() {
    try{visibility = View.INVISIBLE}catch (e:Exception){}
}

fun TextView.setHtmlText(text:String){
    this.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(text)
    }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Fragment.toastt(message: CharSequence){
    try{Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()}catch (e:Exception){}
}
fun Activity.toastt(message: CharSequence){
    try{Toast.makeText(this, message, Toast.LENGTH_SHORT).show()}catch (e:Exception){}
}

fun Context.toastt(message: CharSequence){
    try{Toast.makeText(this, message, Toast.LENGTH_SHORT).show()}catch (e:Exception){}
}

fun Fragment.loadJSONFromAssets(fileName: String): String {
    return activity!!.assets.open(fileName).bufferedReader().use { reader ->
        reader.readText()
    }
}
fun Activity.loadJSONFromAssets(fileName: String): String {
    return assets.open(fileName).bufferedReader().use { reader ->
        reader.readText()
    }
}
fun Context.createDialog( message: String) {

    val builder = AlertDialog.Builder(this)
    builder
        .setMessage(message)
        .setCancelable(true)
        .setNegativeButton(this.getString(R.string.ok)) { dialog, _ -> dialog.cancel() }
    val alert = builder.create()
    alert.show()
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
/**
 * Used for Showing and Hiding keyboard
 */
fun View.showKeyboard(show: Boolean) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (show) {
        if (requestFocus()) imm.showSoftInput(this, 0)
    } else {
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}