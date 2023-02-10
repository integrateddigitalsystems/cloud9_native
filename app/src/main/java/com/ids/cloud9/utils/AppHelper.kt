package com.ids.cloud9.utils


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
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

        fun setLocalStrings(activity: Activity,language: String,locale: Locale) {
            Restring.stringRepository.toMutableRepository().strings.clear()
            if(MyApplication.locMessages != null){
            try {
                val myStringsMap: HashMap<String, String> = HashMap()
                MyApplication.locMessages.forEachIndexed { index, _ ->
                    run {
                        myStringsMap[MyApplication.locMessages!![index].key!!] =
                            if(language == "en") MyApplication.locMessages!![index].valueEn!! else MyApplication.locMessages!![index].valueAr!!
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
              path!!.contains("jpg")||
              path!!.contains("jpeg")||
              path!!.contains("png")){
              return true
          }else{
              return false
          }
        }


        fun isValidPhoneNumber(phoneNumber: String): Boolean {
            return !TextUtils.isEmpty(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches()
        }

        fun loadVideo(view:VideoView, video :String){

                view.setVideoURI(Uri.parse(video))
                view.requestFocus()
                view.start()


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
                return android.util.Base64.encodeToString(byteArray, 0)
            }
        }


        fun setImage(context: Context, img: ImageView, ImgUrl: String, isLocal: Boolean) {
            try {
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


            } catch (e: Exception) {
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

        fun createDialogAgain(c: Activity, message: String,doAction: () -> Unit) {
            val builder = AlertDialog.Builder(c)
            val textView: TextView
            val inflater = c.layoutInflater
            val textEntryView = inflater.inflate(R.layout.item_dialog, null)
            textView = textEntryView.findViewById(R.id.dialogMsg)
            textView.text = message
            builder.setView(textEntryView)
                .setCancelable(true)
                .setNegativeButton(c.getString(R.string.ok)) {
                        dialog, _ ->  doAction() }
            val alert = builder.create()
            alert.setCancelable(false)
            alert.show()
        }

        fun getRemoteString(key: String, con: Context): String {
            if (MyApplication.locMessages != null) {
                return try {
                    MyApplication.locMessages!!.find { it.key == key }!!
                        .getMessage()!!
                } catch (e: Exception) {
                    try {
                        val resId = con.resources.getIdentifier(key, "string", con.packageName)
                        con.resources.getString(resId)
                    } catch (e: Exception) {
                        ""
                    }
                }

            } else {
                val resId = con.resources.getIdentifier(key, "string", con.packageName)
                return con.resources.getString(resId)
            }


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

        fun createDialogPositive(c: Activity, message: String) {
            val builder = AlertDialog.Builder(c)
            builder
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(c.getString(R.string.ok)){ dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
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

        fun handleCrashes(context: Activity) {
            Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(context))
        }

        fun createYesNoDialog(c: Activity, message: String,position: Int, doAction: (position: Int) -> Unit) {

            val builder = AlertDialog.Builder(c)
            builder
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(c.getString(R.string.no)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(c.getString(R.string.yes)) { dialog, _ ->
                    doAction(position)
                }
            val alert = builder.create()
            alert.show()
        }

    }

}
