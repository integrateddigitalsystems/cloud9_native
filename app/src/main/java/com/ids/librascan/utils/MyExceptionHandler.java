package com.ids.librascan.utils;


import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.ids.librascan.controller.MyApplication;
import com.ids.librascan.controller.activities.ActivitySplash;
import com.jakewharton.processphoenix.ProcessPhoenix;


public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Activity activity;
    public MyExceptionHandler(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {

        FirebaseCrashlytics.getInstance().recordException(ex);
        Intent intent = new Intent(activity, ActivitySplash.class);
        ProcessPhoenix.triggerRebirth(MyApplication.instance.getBaseContext(),intent);

    }
}