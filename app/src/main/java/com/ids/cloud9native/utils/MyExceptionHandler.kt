package com.ids.cloud9native.utils

import android.app.Activity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import android.content.Intent
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.activities.ActivitySplash
import com.jakewharton.processphoenix.ProcessPhoenix


class MyExceptionHandler(private val activity: Activity) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(thread: Thread, ex: Throwable) {
       FirebaseCrashlytics.getInstance().recordException(ex)
       val intent = Intent(activity, ActivitySplash::class.java)
        ProcessPhoenix.triggerRebirth(MyApplication.instance.baseContext, intent)
    }
}