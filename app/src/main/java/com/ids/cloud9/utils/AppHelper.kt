package com.ids.cloud9.utils


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.databinding.ErrorDialogBinding
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.restring.toMutableRepository
import dev.b3nedikt.reword.Reword
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt


class AppHelper {
    companion object {
        fun changeLanguage(context: Context, language: String) {
            when (language) {
                AppConstants.LANG_ARABIC -> Locale.setDefault(Locale("ar"))
                AppConstants.LANG_ENGLISH -> Locale.setDefault(Locale.ENGLISH)
                "0" -> {
                    Locale.setDefault(Locale.ENGLISH)
                }
            }

            MyApplication.languageCode = language
            val configuration = Configuration()
            configuration.setLocale(Locale.getDefault())
            configuration.setLayoutDirection(Locale.getDefault())
            context.createConfigurationContext(configuration)

        }

          fun setLocal() {

            if (MyApplication.languageCode == AppConstants.LANG_ENGLISH) {
                LocaleUtils.setLocale(Locale("en"))
           } else if (MyApplication.languageCode == AppConstants.LANG_ARABIC) {
                LocaleUtils.setLocale(Locale("ar"))
            }

        }

        fun hasPermission(context: Context, permissions: Array<String>): Boolean = permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        fun setLocalStrings(activity: Activity,language: String,locale: Locale) {
            Restring.stringRepository.toMutableRepository().strings.clear()
            if(MyApplication.locMessages.size > 0){
            try {
                val myStringsMap: HashMap<String, String> = HashMap()
                MyApplication.locMessages.forEachIndexed { index, _ ->
                    run {
                        myStringsMap[MyApplication.locMessages[index].key] =
                            if(language == "en") MyApplication.locMessages[index].valueEn else MyApplication.locMessages[index].valueAr
                    }
                }
                Restring.putStrings(locale, myStringsMap)
                Restring.locale = locale
                updateView(activity)
            } catch (exception: IOException) {
                wtf( exception.message.toString() )
            }
            }
        }

        fun isImageFile(path: String?): Boolean {
          if(path!!.contains("png")||
              path.contains("jpg")||
              path.contains("jpeg")||
              path.contains("png")){
              return true
          }else{
              return false
          }
        }


        fun isValidPhoneNumber(phoneNumber: String): Boolean {
            return !TextUtils.isEmpty(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches()
        }


        fun convertImageFileToBase64(imageFile: File): String {
            return ByteArrayOutputStream().use { outputStream ->
                Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                    imageFile.inputStream().use { inputStream ->
                        inputStream.copyTo(base64FilterStream)
                    }
                }
                return@use outputStream.toString()
            }
        }

        fun encodeImage(bm: Bitmap): String? {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                java.util.Base64.getEncoder().encodeToString(byteArray)
            } else {
                return Base64.encodeToString(byteArray, 0)
            }
        }


        fun setImage(context: Context, img: ImageView, ImgUrl: String, isLocal: Boolean) {
            safeCall {
                if (isLocal) {
                    Glide.with(context)
                        .load(File(ImgUrl))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .fitCenter()
                        .dontTransform()
                        .into(img)
                } else {
                    Glide.with(context)
                        .load(ImgUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .fitCenter()
                        .dontTransform()
                        .into(img)
                }


            }

        }

        fun getAndroidVersion(): String {

            val release = Build.VERSION.RELEASE
            val sdkVersion = Build.VERSION.SDK_INT
            return "Android:$sdkVersion ($release)"
        }
        fun getDeviceName(): String {

            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

        private fun capitalize(model: String): String {
            if (model.length == 0) {
                return ""
            }
            val first = model.get(0)
            return if (Character.isUpperCase(first)) {
                model
            } else {
                Character.toUpperCase(first) + model.substring(1)
            }
        }

        fun getVersionNumber(): Int {

            val pInfo: PackageInfo
            var version = -1
            try {
                pInfo = MyApplication.instance.packageManager
                    .getPackageInfo(MyApplication.instance.packageName, 0)
                version = pInfo.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return version
        }

        fun isInForeground():Boolean{
            val actMan = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(actMan)
            return (actMan.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || actMan.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
        }
        fun getReasonID(code:String):Int{
            val reason = MyApplication.lookupsReason.find {
                it.lookupCode.equals(code)
            }
            if(reason!=null){
                return reason.id
            }else{
                return -1
            }
        }
        fun getReasonName(code:String):String{
            val reason = MyApplication.lookupsReason.find {
                it.lookupCode.equals(code)
            }
            if(reason!=null){
                return reason.name
            }else{
                return ""
            }
        }

        fun getColor(con:Context , color : Int ):Int{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return ContextCompat.getColor(con, color)
            } else {
                return con.resources.getColor(color)
            }
        }

        private fun updateView(activity: Activity) {
            val rootView: View = activity.window.decorView.findViewById(android.R.id.content)
            Reword.reword(rootView)
        }


        @SuppressLint("NewApi")
        fun isNetworkAvailable(context: Context): Boolean {
            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            return capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true
        }


        fun setTextColor(context: Context, view: TextView, color: Int) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setTextColor(ContextCompat.getColor(context, color))
            } else {
                view.setTextColor(context.resources.getColor(color))
            }
        }

        fun getTypeFace(context: Context): Typeface? {
            return if (MyApplication.languageCode.equals(AppConstants.LANG_ENGLISH)) Typeface.createFromAsset(
                context.applicationContext.assets,
                "fonts/Raleway-Medium.ttf"
            ) else Typeface.createFromAsset(
                context.applicationContext.assets,
                "fonts/GESSTwoMedium-Medium.ttf"
            )
        }
        fun getTypeFaceItalic(context: Context): Typeface? {
            return if (MyApplication.languageCode.equals(AppConstants.LANG_ENGLISH)) Typeface.createFromAsset(
                context.applicationContext.assets,
                "fonts/Raleway-Italic.ttf"
            ) else Typeface.createFromAsset(
                context.applicationContext.assets,
                "fonts/Poppins-Italic.ttf"
            )
        }

        fun getTypeFaceExtraBold(context: Context): Typeface? {
            return if (MyApplication.languageCode.equals(AppConstants.LANG_ENGLISH)) Typeface.createFromAsset(
                context.applicationContext.assets,
                "fonts/Raleway-ExtraBold.ttf"
            ) else Typeface.createFromAsset(
                context.applicationContext.assets,
                "fonts/GESSTwoBold-Bold.ttf"
            )
        }
        fun getTypeFaceBold(context: Context): Typeface {
            return if (MyApplication.languageCode.equals(AppConstants.LANG_ENGLISH)) Typeface.createFromAsset(
                context.applicationContext.assets,
                "fonts/Raleway-Bold.ttf"
            ) else Typeface.createFromAsset(
                context.applicationContext.assets,
                "fonts/GESSTwoBold-Bold.ttf"
            )
        }


        @SuppressLint("ResourceAsColor")
        fun createDialogError(activity: Activity, errorMessage: String, titleMessage:String, isSuccess:Boolean) {
            val errorDialogBinding = ErrorDialogBinding.inflate(activity.layoutInflater)
            val builder = AlertDialog.Builder(activity)

            errorDialogBinding.tvDetails.text = errorMessage
            errorDialogBinding.tvTitle.text = titleMessage

            if (isSuccess)  errorDialogBinding.tvDetails.setTextColor(Color.parseColor("#009688"))
            errorDialogBinding.llItem.setOnClickListener {
                if (errorDialogBinding.tvDetails.visibility == View.VISIBLE)
                    errorDialogBinding.tvDetails.hide()
                else
                    errorDialogBinding.tvDetails.show()
            }

            builder.setView(errorDialogBinding.root)
                .setNegativeButton(activity.resources.getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
            val d = builder.create()
            d.setOnShowListener {
                d.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
                d.getButton(AlertDialog.BUTTON_NEGATIVE).transformationMethod = null
                d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
            }
            d.setCancelable(false)
            d.show()
        }

        fun durationToString(duratio:Float):String{
            return if(duratio > 1.0){
                val hrs = duratio.toInt()
                val minutes = ((duratio - hrs)*60).roundToInt()
                hrs.toString()+"hrs "+minutes+"mins"
            }else{
                val minutes = ((duratio)*60).roundToInt()
                minutes.toString()+"mins"
            }
        }

        fun handleCrashes(context: Activity) {
            Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(context))
        }


        fun createActionDialog(
            c: Activity,
            positiveButton: String,
            message: String,
            cancelable:Boolean?=true,
            doAction: () -> Unit,

            ) {
            val builder = AlertDialog.Builder(c)
            builder
                .setMessage(message)
                .setCancelable(cancelable!!)
                .setPositiveButton(positiveButton) { dialog, _ ->
                    doAction()
                }
            val alert = builder.create()
            alert.show()
        }
    }

}
