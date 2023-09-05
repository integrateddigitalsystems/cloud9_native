package com.ids.cloud9.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ids.cloud9.BuildConfig
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.activities.ActivitySplash
import com.ids.cloud9.model.RemoteMessageType
import com.ids.cloud9.utils.wtf

class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val TAG = "MyFirebaseMessagingService"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
               // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            wtf(task.result)
            MyApplication.firebaseToken = task.result
            //sendRegistrationToServer(task.result)
        })

    }



    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        wtf(Gson().toJson(message))
        wtf("onMessageReceived: " + message.getData().get("message"))
        var visitId : Int ?=0
        try{
            visitId = message.data.get("recordId")!!.toInt()
        }catch (ex:Exception){
            visitId = 0
        }

        var messageSent : String ?=""
        try{
            messageSent = message.data.get("message")
        }catch (ex:Exception){
            messageSent = ""
        }
        if(messageSent.isNullOrEmpty())
            messageSent = ""
        sendNotification("",visitId!!,messageSent)
    }


    fun sendNotification(message :String , visitId : Int,title : String  ){
        val intent = Intent(this,ActivitySplash::class.java)
            .putExtra("visitId",visitId)
        val pendingIntent = PendingIntent.getActivity(this, MyApplication.UNIQUE_REQUEST_CODE++, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, BuildConfig.APPLICATION_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message).setAutoCancel(true).setContentIntent(pendingIntent)


        val manager :  NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val  channel = NotificationChannel(BuildConfig.APPLICATION_ID, "Default channel", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        manager.notify(0, builder.build())
    }





}