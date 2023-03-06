package com.ids.cloud9.Notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ids.cloud9.BuildConfig
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.activities.ActivitySplash
import com.ids.cloud9.utils.AppConstants
import com.ids.cloud9.utils.LocaleUtils
import com.ids.cloud9.utils.LocationForeService
import com.ids.cloud9.utils.wtf
import java.util.*
import kotlin.math.log

class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val TAG = "MyFirebaseMessagingService"
    private val NOTIFICATION_CHANNEL_ID = "while_in_use_channel_02"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
               // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            Log.wtf("TagNotf",task.result)
            MyApplication.firebaseToken = task.result
            sendRegistrationToServer(task.result)
        })

    }

    private fun sendRegistrationToServer(token: String) {
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message);
        Log.d("msg", "onMessageReceived: " + message.getData().get("message"));

        val intent = Intent("msg") //action: "msg"
        intent.setPackage(packageName)
        intent.putExtra("message", "")
        applicationContext.sendBroadcast(intent)
     


        var visitId = message.data.get("visitId")
        sendNotification(message!!.notification!!,visitId!!.toInt(),Gson().toJson(message.data))
    }


    fun sendNotification(message : com.google.firebase.messaging.RemoteMessage.Notification , visitId : Int,str : String  ){
        var intent = Intent(this,ActivitySplash::class.java)
            .putExtra("visitId",visitId)
        val pendingIntent = PendingIntent.getActivity(this, MyApplication.UNIQUE_REQUEST_CODE++, intent, PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(str)
            .setContentText(str).setAutoCancel(true).setContentIntent(pendingIntent)


        var manager :  NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var  channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }





}