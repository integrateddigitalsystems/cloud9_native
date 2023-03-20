package com.ids.cloud9.custom


import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.utils.*
import dev.b3nedikt.restring.Restring
import java.util.*

open class AppCompactBase : AppCompatActivity() {
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
    private var foregroundOnlyLocationServiceBound = false
    var foregroundOnlyLocationService: LocationForeService? = null
    var mPermissionResult: ActivityResultLauncher<Array<String>>? = null
    val BLOCKED = -1
    val BLOCKED_OR_NEVER_ASKED = 2
    val GRANTED = 0
    val DENIED = 1
    fun setUpPermission() {
        mPermissionResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { result ->
                var permission = false
                for (item in result) {
                    permission = item.value
                }
                if (!permission) {
                    toast(getString(R.string.location_updates_disabled))
                    for (item in result) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                item.key
                            ) == BLOCKED
                        ) {

                            if (getPermissionStatus(Manifest.permission.ACCESS_FINE_LOCATION) == BLOCKED_OR_NEVER_ASKED) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.please_grant_permission),
                                    Toast.LENGTH_LONG
                                ).show()
                                break
                            }
                        }
                    }
                }
            }
    }
    fun getPermissionStatus(androidPermissionName: String?): Int {
        return if (ContextCompat.checkSelfPermission(
                this,
                androidPermissionName!!
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    androidPermissionName
                )
            ) {
                BLOCKED_OR_NEVER_ASKED
            } else DENIED
        } else GRANTED
    }
    private val appCompatDelegate: AppCompatDelegate by lazy {
        ViewPumpAppCompatDelegate(
            baseDelegate = super.getDelegate(),
            baseContext = this,
            wrapContext = Restring::wrapContext
        )
    }
    private fun openChooser() {
        mPermissionResult!!.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }
    private val foregroundOnlyServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationForeService.LocalBinder
            foregroundOnlyLocationService = binder.foreService
            foregroundOnlyLocationServiceBound = true
            if (MyApplication.saveLocTracking) {
                MyApplication.gettingTracked = true
                changeState(true)
            }
        }
        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        super.onStop()
    }
    override fun onPause() {
        MyApplication.activityPaused()
        changeState(MyApplication.gettingTracked)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }
    override fun onStart() {
        super.onStart()
        MyApplication.serviceContext = this
        val serviceIntent = Intent(this, LocationForeService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }
    override fun onResume() {
        super.onResume()
        MyApplication.activityResumed()
        changeState(MyApplication.gettingTracked)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                LocationForeService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    fun changeState(track: Boolean) {
        var gps_enabled = false
        val mLocationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager

        safeCall {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
        if (gps_enabled) {
            if (!track) {
                MyApplication.saveLocTracking = false
                safeCall {
                    foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
                    val intent = Intent()
                    intent.setClass(this, LocationForeService::class.java)
                    stopService(intent)
                }
            } else {
                try {
                    // TODO: Step 1.0, Review Permissions: Checks and requests if needed.
                    if (foregroundPermissionApproved()) {

                        MyApplication.saveLocTracking = true
                        foregroundOnlyLocationService?.subscribeToLocationUpdates()
                            ?: run {
                                wtf("Service Not Bound")
                            }
                    } else {
                        createActionDialog(
                            getString(R.string.permission_background_android),
                            0
                        ){
                            openChooser()
                        }
                    }
                } catch (ex: Exception) {
                    wtf(ex.toString())
                }
            }
        }
    }
    init {
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
        setUpPermission()
        AppHelper.setLocal()
    }
    override fun getDelegate(): AppCompatDelegate {
        return appCompatDelegate
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppHelper.setLocal()
        if (MyApplication.languageCode == AppConstants.LANG_ENGLISH) {
            AppHelper.setLocalStrings(this, "en", Locale("en"))
        } else if (MyApplication.languageCode == AppConstants.LANG_ARABIC) {
            AppHelper.setLocalStrings(this, "ar", Locale("ar"))
        }
    }
    override fun attachBaseContext(newBas: Context) {
        var newBase = newBas
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val config = newBase.resources.configuration
            config.setLocale(Locale.getDefault())
            newBase = newBase.createConfigurationContext(config)
        }
        super.attachBaseContext(newBase)
    }

    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(LocationForeService.EXTRA_LOCATION, Location::class.java)
            } else {
                intent.getParcelableExtra(
                    LocationForeService.EXTRA_LOCATION)
            }
            if (location != null) {
                wtf("Foreground location: ${location.toText()}")
            }
        }
    }
}
