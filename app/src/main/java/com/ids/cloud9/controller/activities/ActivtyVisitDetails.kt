package com.ids.cloud9.controller.activities

import android.Manifest
import android.animation.AnimatorSet
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.controller.adapters.AdapterDialog
import com.ids.cloud9.controller.adapters.AdapterMedia
import com.ids.cloud9.controller.adapters.AdapterProducts
import com.ids.cloud9.controller.adapters.AdapterReccomendations
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding

import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class ActivtyVisitDetails :AppCompactBase(), RVOnItemClickListener {

    var binding: ActivityVisitDetailsBinding? = null
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
    private var foregroundOnlyLocationServiceBound = false
    var CODE_CAMERA = 1
    var CODE_VIDEO = 2
    var foregroundOnlyLocationService: LocationForeService? = null
    val BLOCKED = -1
    val GRANTED = 0
    val DENIED = 1
    val BLOCKED_OR_NEVER_ASKED = 2
    var mPermissionResult: ActivityResultLauncher<Array<String>>? = null
    lateinit var mPermissionResult2: ActivityResultLauncher<Intent>
    var CODE_GALLERY = 3
    var visitId = 0
    var fromNotf = 0
    var code: Int? = -1
    var currLayout: LinearLayout? = null
    var currLayPost = 0

    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                LocationForeService.EXTRA_LOCATION
            )
            if (location != null) {
                wtf("Foreground location: ${location.toText()}")
            }
        }
    }
    override fun onBackPressed() {
        if(fromNotf>0){
            startActivity(
                Intent(
                    this,
                    ActivityMain::class.java
                )
            )
            finishAffinity()
        }else{
           finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
        setUp()
        setUpPermission()
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
        listeners()
    }
    fun init() {
        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.layoutFragment.show()
        visitId = intent.getIntExtra("visitId",-1)
        fromNotf = intent.getIntExtra("fromNotf",0)
        AppHelper.setTextColor(this, binding!!.tvVisit, R.color.medium_blue)
        binding!!.llBorderVisit.hide()
        binding!!.llSelectedVisitBorder.show()

    }


    fun setUpPermission() {
        mPermissionResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { result ->
                var permission = false
                for (item in result) {
                    permission = item.value
                }
                if (permission) {

                } else {
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
    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    private fun openChooser() {
        mPermissionResult!!.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }
    fun changeState(track: Boolean, indx: Int) {
        var gps_enabled = false
        var mLocationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager

        try {
            gps_enabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        if (gps_enabled) {
            if (!track) {
                MyApplication.saveLocTracking = false
                try {
                    foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
                    val intent = Intent()
                    intent.setClass(this, LocationForeService::class.java)
                    stopService(intent)
                } catch (ex: Exception) {
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
                        AppHelper.createYesNoDialog(
                            this,
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
    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        super.onStop()
    }
    private val foregroundOnlyServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationForeService.LocalBinder
            foregroundOnlyLocationService = binder.foreService
            foregroundOnlyLocationServiceBound = true
            if (MyApplication.saveLocTracking!!)
                changeState(true, 0)
        }
        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }
    override fun onPause() {
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
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                LocationForeService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
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
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CODE_CAMERA -> {
                var permissioned = false
                for (item in grantResults) {
                    permissioned = item == PackageManager.PERMISSION_GRANTED
                }
                if (permissioned) {
                    MyApplication.permission = 0
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        if (MyApplication.permission!! >= 2) {
                            for (item in permissions) {
                                var x = checkSelfPermission(item)
                                if (checkSelfPermission(item) == BLOCKED) {
                                    mPermissionResult2!!.launch(
                                        Intent(android.provider.Settings.ACTION_SETTINGS)!!
                                    )

                                    toast(
                                        getString(R.string.grant_setting_perm)
                                    )
                                    break
                                }
                            }
                        } else {
                            MyApplication.permission = MyApplication.permission!! + 1
                        }
                    }
                }
            }

        }
    }
    fun setUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), CODE_GALLERY
            )
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), CODE_GALLERY
            )
        }
    }
    fun restartLayouts() {
        AppHelper.setTextColor(this, binding!!.tvCompany, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvVisit, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvMedia, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvReccom, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvProducts, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvSignature, R.color.text_gray)
        binding!!.llBorderCompany.show()
        binding!!.llSelectedCompanyBorder.hide()
        binding!!.llBorderVisit.show()
        binding!!.llSelectedVisitBorder.hide()
        binding!!.llMediaBorder.show()
        binding!!.llSelectedMediaBorder.hide()
        binding!!.llBorderRecc.show()
        binding!!.llSelectedReccBorder.hide()
        binding!!.llProductsBorder.show()
        binding!!.llSelectedProductsBorder.hide()
        binding!!.llBorderSignature.show()
        binding!!.llSelectedSignatureBorder.hide()
    }
    fun listeners() {
        binding!!.llCompany.setOnClickListener {
            if (currLayPost != 1) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvCompany, R.color.medium_blue)
                binding!!.llSelectedCompanyBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentCompany)
                val navBuilder = NavOptions.Builder()
                if (currLayPost > 1) {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentCompany, null, navBuilder.build())
            }
            currLayPost = 1
        }
        binding!!.llVisit.setOnClickListener {
            if (currLayPost != 0) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvVisit, R.color.medium_blue)
                binding!!.llBorderVisit.hide()
                binding!!.llSelectedVisitBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentVisitDetails)
                val navBuilder = NavOptions.Builder()
                navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                    .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentVisitDetails, null, navBuilder.build())
                currLayPost = 0
            }
        }
        binding!!.llTool.ivCalendar.setOnClickListener {
            finishAffinity()
            startActivity(
                Intent(
                    this,
                    ActivityMain::class.java
                )
            )
        }
        binding!!.llMedia.setOnClickListener {
            if (currLayPost != 5) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvMedia, R.color.medium_blue)
                binding!!.llMediaBorder.hide()
                binding!!.llSelectedMediaBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentMedia)
                val navBuilder = NavOptions.Builder()
                if (currLayPost > 5) {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentMedia, null, navBuilder.build())
                currLayPost = 5
            }
        }
        binding!!.llProducts.setOnClickListener {
            if (currLayPost != 2) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvProducts, R.color.medium_blue)
                binding!!.llProductsBorder.hide()
                binding!!.llSelectedProductsBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentProducts)
                val navBuilder = NavOptions.Builder()
                if (currLayPost > 2) {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentProducts, null, navBuilder.build())
                currLayPost = 2
            }
        }
        binding!!.llRecommendations.setOnClickListener {
            if (currLayPost != 3) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvReccom, R.color.medium_blue)
                binding!!.llBorderRecc.hide()
                binding!!.llSelectedReccBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentReccomendations)
                val navBuilder = NavOptions.Builder()
                if (currLayPost > 3) {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentReccomendations, null, navBuilder.build())
                currLayPost = 3
            }
        }
        binding!!.llSignature.setOnClickListener {
            if (currLayPost != 4) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvSignature, R.color.medium_blue)
                binding!!.llBorderSignature.hide()
                binding!!.llSelectedSignatureBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentProducts)
                val navBuilder = NavOptions.Builder()
                if (currLayPost < 4) {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentSignature, null, navBuilder.build())
                currLayPost = 4
            }
        }
        binding!!.llTool.btBack.setOnClickListener {
            if(fromNotf>0){
                startActivity(
                    Intent(
                        this,
                        ActivityMain::class.java
                    )
                )
                finishAffinity()
            }else{
               finish()
            }
        }
    }
    override fun onItemClicked(view: View, position: Int) {
    }
}