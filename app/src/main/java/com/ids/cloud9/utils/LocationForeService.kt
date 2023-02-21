package com.ids.cloud9.utils

import android.Manifest
import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.gson.Gson
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.activities.ActivitySplash
import com.ids.cloud9.model.ResponseMessage
import com.ids.cloud9.model.VisitLocationRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import java.util.concurrent.TimeUnit

class LocationForeService : Service() {
    /*
     * Checks whether the bound activity has really gone away (foreground service with notification
     * created) or simply orientation change (no-op).
     */
    private var configurationChange = false
    private var serviceRunningInForeground = false
    var locationListenerGps : LocationListener?=null
    var currNotf : Notification ?=null
    lateinit var notificationCompatBuilder : NotificationCompat.Builder
    var docLat : LatLng?=null
    var doneONce = false
    var indx = 0
    var mLocationManager : LocationManager?=null
    var doc: DocumentReference? = null
    private val localBinder = LocalBinder()
    var LOCATION_REFRESH_DISTANCE = 5
    private val NOTIFICATION_ID = 12345678
    var LOCATION_REFRESH_TIME = 5000
    var timer : CountDownTimer?=null
    private lateinit var auth: FirebaseAuth
    private lateinit var notificationManager: NotificationManager

    // TODO: Step 1.1, Review variables (no changes).
    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e., how often you should receive
    // updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient has a new Location.
    private lateinit var locationCallback: LocationCallback

    // Used only for local storage of the last known location. Usually, this would be saved to your
    // database, but because this is a simplified sample without a full database, we only need the
    // last location to create a Notification if the user navigates away from the app.
    private var currentLocation: Location? = null


    inner class LocalBinder : Binder() {
        internal val foreService: LocationForeService
            get() = this@LocationForeService
    }

    companion object {
        private const val TAG = "LocationForeService"

        private const val PACKAGE_NAME = "com.example.android.whileinuselocation"

        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"

        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 12345678

        private const val NOTIFICATION_CHANNEL_ID = "while_in_use_channel_01"
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate()")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // TODO: Step 1.2, Review the FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // TODO: Step 1.3, Create a LocationRequest.
        locationRequest = LocationRequest.create().apply {
            // Sets the desired interval for active location updates. This interval is inexact. You
            // may not receive updates at all if no location sources are available, or you may
            // receive them less frequently than requested. You may also receive updates more
            // frequently than requested if other applications are requesting location at a more
            // frequent interval.
            //
            // IMPORTANT NOTE: Apps running on Android 8.0 and higher devices (regardless of
            // targetSdkVersion) may receive updates less frequently than this interval when the app
            // is no longer in the foreground.
            interval = TimeUnit.SECONDS.toMillis(60)

            // Sets the fastest rate for active location updates. This interval is exact, and your
            // application will never receive updates more frequently than this value.
            fastestInterval = TimeUnit.SECONDS.toMillis(30)

            // Sets the maximum time when batched location updates are delivered. Updates may be
            // delivered sooner than this interval.
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // TODO: Step 1.4, Initialize the LocationCallback.




        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationResult ?: return
                try {
                    locationResult.lastLocation
                    /*   doc!!.update("order_laltitude", locationResult.lastLocation.latitude.toString())
                       doc!!.update("order_longitude", locationResult.lastLocation.longitude.toString())*/

                    val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                    intent.putExtra(EXTRA_LOCATION, currentLocation)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                    currentLocation = locationResult.lastLocation
                    if(currentLocation!=null) {
                        startForeground(NOTIFICATION_ID, generateNotification())
                    }


                    Toast.makeText(applicationContext, "LOC CHANG---"+locationResult.lastLocation!!.longitude+"--"+locationResult.lastLocation!!.latitude, Toast.LENGTH_SHORT).show()

                    changeLocation(currentLocation!!)
                    if (serviceRunningInForeground) {
                        /*notificationManager.notify(
                            NOTIFICATION_ID,
                            generateNotification())*/
                    }
                } catch (ex: Exception) {
                    Log.wtf("", ex.toString())
                }
            }


        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")

        val cancelLocationTrackingFromNotification =
            intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        if (cancelLocationTrackingFromNotification) {
            unsubscribeToLocationUpdates()
            stopSelf()
        }
        // Tells the system not to recreate the service after it's been killed.
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")

        // MainActivity (client) comes into foreground and binds to service, so the service can
        // become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind()")

        // MainActivity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")

        // MainActivity (client) leaves foreground, so service needs to become a foreground service
        // to maintain the 'while-in-use' label.
        // NOTE: If this method is called due to a configuration change in MainActivity,
        // we do nothing.
        if (!configurationChange && MyApplication.saveLocTracking!!) {
            Log.d(TAG, "Start foreground service")
            if(currentLocation!=null) {
                val notification = generateNotification()
                startForeground(NOTIFICATION_ID, notification)
            }
            serviceRunningInForeground = true
        }

        // Ensures onRebind() is called if MainActivity (client) rebinds.
        return true
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val intent = Intent("com.android.ServiceStopped")
        sendBroadcast(intent)
    }

    override fun onStart(intent: Intent?, startId: Int) {
            subscribeToLocationUpdates()
    }


    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")

        /* val broadcastIntent = Intent()
         broadcastIntent.action = "restartservice"
         broadcastIntent.setClass(this, Restarter::class.java)
         this.sendBroadcast(broadcastIntent)*/
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }





    fun notifyClose(orderId :  Int,index : Int ){
        /*var req = RequestOrderIdNotify(orderId)
        RetrofitClient.client?.create(RetrofitInterface::class.java)
            ?.notifyOrderArrived(req)?.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if(response.body()!!.result == 1 ){
                        try {
                            doneONce = true
                            logw("LOGDIST", "less 50")
                            MyApplication.doneOrders.find { it.orderId!!.toInt() == orderId }!!.done = true
                            AppHelper.toGSOnDOne(MyApplication.doneOrders)
                            var x = MyApplication.listDoneOrders
                        }catch (ex:Exception){}
                    }else{
                        logw("NOTF","error")
                    }

                }

                override fun onFailure(call: Call<ResponseMessage>, throwable: Throwable) {
                    logw("NOTF","error")
                }
            })*/
    }


    fun getDIstance(from : LatLng,
                    to : LatLng,
                    con : Application,
                    index: Int ) {

       /* var dest = from.latitude.toString() + ","+from.longitude.toString()
        var origin  = to.latitude.toString() + ","+to.longitude.toString()

        RetroFitMap2.client?.create(RetrofitInterface::class.java)
            ?.getDistance(dest, origin, con.getString(R.string.googleKey))
            ?.enqueue(object : Callback<ResponseDistance> {
                override fun onResponse(
                    call: Call<ResponseDistance>,
                    response: Response<ResponseDistance>
                ) {

                    try {

                        var dist = response.body()!!
                        var element = dist.rows.get(0).elements.get(0)
                        if(element.status.equals("OK")){
                            if(element.distance.value!!.toDouble() <= MyApplication.notifyDistance!!.toDouble() && !doneONce){
                                //API
                                notifyClose(MyApplication.doneOrders.get(index).orderId!!.toInt(),index)

                            }else{
                                MyApplication.doneOrders.get(index).done = false
                            }
                        }

                    } catch (E: java.lang.Exception) {

                    }
                }

                override fun onFailure(call: Call<ResponseDistance>, throwable: Throwable) {

                }
            })*/
    }




    fun setUpLocations() {



        val locationResult = object : MyLocation.LocationResult() {
            override fun gotLocation(location: Location?) {
                if (location != null) {
                    currentLocation = location
                    changeLocation(currentLocation!!)
                    Toast.makeText(applicationContext, "LOC CHANG---"+location.longitude+"--"+location.latitude, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "cannot detect location", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

          val myLocation = MyLocation()

        myLocation.getLocation(this, locationResult)

        var firstLocation : Location?=null



        locationListenerGps = object : LocationListener {


            override fun onLocationChanged(location: Location) {
                try{
                    currentLocation = location
                    Toast.makeText(applicationContext, "LOC CHANG---"+location.longitude+"--"+location.latitude, Toast.LENGTH_SHORT).show()
                    Log.wtf("UPDATE_DESTORY",location.latitude.toString()+","+location.longitude.toString())
                    MyApplication.address!!.lat=location.latitude.toString()
                    MyApplication.address!!.long=location.longitude.toString()}catch (e:Exception){}
                generateNotification()

                var update = com.google.android.gms.maps.model.LatLng(location.latitude,location.longitude)
                if(location!=null)
                    firstLocation = location
                var ct = 0


                changeLocation(currentLocation!!)






                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)


                if(currentLocation!=null) {

                    startForeground(NOTIFICATION_ID, generateNotification())
                }
                if (serviceRunningInForeground) {

                    /*notificationManager.notify(
                        NOTIFICATION_ID,
                        generateNotification(location))*/
                }
            }

            override fun onProviderDisabled(provider: String) {
                Log.wtf("test", provider)
            }

            override fun onProviderEnabled(provider: String) {
                Log.wtf("test", provider)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                Log.wtf("test", provider)
            }
        }

        mLocationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME.toLong(),
                LOCATION_REFRESH_DISTANCE.toFloat(), locationListenerGps!!
            )

            var gps_enabled  = false
            var network_enabled = false

            try {
                gps_enabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
            }

            try {
                network_enabled = mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (ex: Exception) {
            }

            if(gps_enabled && firstLocation == null)
                firstLocation = mLocationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(network_enabled){
                firstLocation = mLocationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            if(firstLocation!=null) {
                currentLocation = firstLocation
                generateNotification()
                changeLocation(currentLocation!!)
            }



        }


    }
    fun subscribeToLocationUpdates() {
            Log.d(TAG, "subscribeToLocationUpdates()")
            try {
                MyApplication.saveLocTracking = true
                setUpLocations()
            } catch (ex: Exception) {
                var x = ex
                try {
                    signInAnonymously()
                } catch (e: java.lang.Exception) {
                }
            }

        if(currentLocation!=null) {
            notificationManager.notify(
                NOTIFICATION_ID,
                generateNotification()
            )
        }
        startService(Intent(applicationContext, LocationForeService::class.java))

    }


    fun changeLocation(location: Location){
        var visitLocationRequest = VisitLocationRequest(
            MyApplication.userItem!!.applicationUserId!!.toInt(),
            0,
            false,
            location.latitude ,
            location.longitude ,
            MyApplication.onTheWayVisit!!.id!!


        )
        Log.wtf("TAG_UPDATE",Gson().toJson(visitLocationRequest))
        RetrofitClientAuth.client!!.create(
            RetrofitInterface::class.java
        ).createVisitLocation(
            visitLocationRequest
        ).enqueue(object : Callback<ResponseMessage>{
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                try {
                    Log.wtf("LOCATION_UPDATE", response.body()!!.message)
                }catch (ex:Exception){
                    Log.wtf("LOCATION_UPDATE", "error")
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Log.wtf("LOCATION_UPDATE","FAILURE")
            }

        })
    }




    fun unsubscribeToLocationUpdates() {
        Log.d(TAG, "unsubscribeToLocationUpdates()")

        notificationManager.cancel(NOTIFICATION_ID)

            try {
                // TODO: Step 1.6, Unsubscribe to location changes.
                val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                mLocationManager!!.removeUpdates(locationListenerGps!!)
                removeTask.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Location Callback removed.")
                        stopSelf()
                    } else {
                        Log.d(TAG, "Failed to remove Location Callback.")
                    }
                }

                MyApplication.saveLocTracking = false
            } catch (unlikely: SecurityException) {
                MyApplication.saveLocTracking = true
                Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
            }

            this.stopSelf()
    }


    private fun signInAnonymously() {
        // [START signin_anonymously]
        auth = FirebaseAuth.getInstance()
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.wtf("auth", "signInAnonymously:success")
                    val user = auth.currentUser
                    setUpLocations()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.wtf("auth", "signInAnonymously:failure+"+task.exception)
                    Toast.makeText(baseContext, getString(R.string.auth_error),
                        Toast.LENGTH_SHORT).show()

                }
            }
        // [END signin_anonymously]
    }


    /*
     * Generates a BIG_TEXT_STYLE Notification that represent latest location.
     */
    private fun generateNotification(): Notification {
        Log.d(TAG, "generateNotification()")

        // Main steps for building a BIG_TEXT_STYLE notification:
        //      0. Get data
        //      1. Create Notification Channel for O+
        //      2. Build the BIG_TEXT_STYLE
        //      3. Set up Intent / Pending Intent for notification
        //      4. Build and issue the notification

        // 0. Get data
        // val mainNotificationText = location?.toText() ?: AppHelper.getRemoteString("collecting_loc",this)
        val mainNotificationText = AppHelper.getRemoteString("collecting_loc",this)

            val titleText =
                currentLocation!!.latitude.toString() + ":" + currentLocation!!.longitude

            // 1. Create Notification Channel for O+ and beyond devices (26+).
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT
                )

                // Adds NotificationChannel to system. Attempting to create an
                // existing notification channel with its original values performs
                // no operation, so it's safe to perform the below sequence.
                notificationManager.createNotificationChannel(notificationChannel)
            }

            // 2. Build the BIG_TEXT_STYLE.
            val bigTextStyle = NotificationCompat.BigTextStyle()
                .bigText(mainNotificationText)
                .setBigContentTitle(titleText)

            // 3. Set up main Intent/Pending Intents for notification.
            var launchActivityIntent: Intent? = null
            /* if(MyApplication.fromOrderDetails!!) {
             launchActivityIntent = Intent(this, ActivityOrderDetails::class.java)
         }else{*/

            //MyApplication.tintColor = R.color.primary
            launchActivityIntent = Intent(this, ActivitySplash::class.java)
            //launchActivityIntent.putExtra("typeId",AppConstants.NOTF_TYPE_ACCEPT_ORDER)
            launchActivityIntent.putExtra("fromNotf", 1)
            // }

            val cancelIntent = Intent(this, LocationForeService::class.java)
            cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)

            val servicePendingIntent = PendingIntent.getService(
                this, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE
            )

            val activityPendingIntent = PendingIntent.getActivity(
                this, 0, launchActivityIntent, PendingIntent.FLAG_IMMUTABLE
            )

            // 4. Build and issue the notification.
            // Notification Channel Id is ignored for Android pre O (26).
            notificationCompatBuilder =
                NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

            currNotf = notificationCompatBuilder
                .setStyle(bigTextStyle)
                .setContentTitle(titleText)
                .setContentText(mainNotificationText)
                .setSmallIcon(com.ids.cloud9.R.drawable.google)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
            return currNotf!!

    }



    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */


    /*override fun onDataRetrieved(success: Boolean, response: Any, apiId: Int) {
        try{
            if(success){

                if(apiId == AppConstants.DISTANCE_GEO){
                    var dist = response as ResponseDistance
                    var element = dist.rows.get(0).elements.get(0)
                    if(element.status.equals("OK")){
                        if(element.distance.value!!.toDouble() <= MyApplication.notifyDistance!!.toDouble() && !doneONce){
                            //API
                            notifyClose(MyApplication.doneOrders.get(indx).orderId!!.toInt(),indx)

                        }
                    }
                }else{
                    var order = response as ResponseOrders
                    var dest = LatLng(order.shipping_latitude!!.toDouble(),order.shipping_longitude!!.toDouble())
                    *//* var distance = dest.getDistance(docLat!!)
                     if(distance<=50f){
                         //do API
                         logw("LOGLOC",distance.toString())
                     }*//*
                    getDIstance(docLat!!,dest,application,indx)
                }


            }
        }catch (ex:Exception){}
    }*/
}