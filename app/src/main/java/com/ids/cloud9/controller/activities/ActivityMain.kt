package com.ids.cloud9.controller.activities

import HeaderDecoration
import android.Manifest
import android.app.Activity
import android.content.*
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.ids.cloud9.controller.adapters.StickyAdapter
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.ActivityMainBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ActivityMain: AppCompactBase() , RVOnItemClickListener{
    var simp = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    var half = SimpleDateFormat("MMM dd")
    var secondHalf = SimpleDateFormat("MMM dd yyyy")
    var secondNoMonthHalf = SimpleDateFormat("dd, yyyy")
    var binding : ActivityMainBinding?=null
    var fromDefault = Calendar.getInstance()
    var toDefault = Calendar.getInstance()
    var foregroundOnlyLocationService: LocationForeService? = null
    val BLOCKED = -1
    val GRANTED = 0
    val DENIED = 1
    val BLOCKED_OR_NEVER_ASKED = 2
    var mPermissionResult: ActivityResultLauncher<Array<String>>? = null
    var prevHeaders : ArrayList<Int> = arrayListOf()
    var editingFrom = Calendar.getInstance()
    var editingTo = Calendar.getInstance()
    var tempArray : ArrayList<testVisitItem> = arrayListOf()
    var mainArray : ArrayList<testVisitItem> = arrayListOf()
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
    private var foregroundOnlyLocationServiceBound = false
    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                LocationForeService.EXTRA_LOCATION
            )
            if (location != null) {
                Log.wtf("FORE", "Foreground location: ${location.toText()}")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setUpPermission()
        listeners()
        getVisits()
        setUpDate()
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
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
                                    Log.d(TAG, "Service Not Bound")
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
                    Log.wtf("Ex",ex.toString())
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
                                    getString(info.bideens.barcode.R.string.grant_permission),
                                    Toast.LENGTH_LONG
                                ).show()
                                break
                            }
                        }
                    }
                }
            }
    }

    fun editDate(isNext : Boolean ){
        if(isNext){
            var oldEdge = editingTo.time
            editingFrom.time = oldEdge
            editingTo.time = oldEdge
            editingFrom.add(Calendar.DAY_OF_MONTH,1)
            editingTo.add(Calendar.DAY_OF_MONTH,7)

        }else{
            var oldEdge = editingFrom.time
            editingFrom.time = oldEdge
            editingTo.time = oldEdge
            editingFrom.add(Calendar.DAY_OF_MONTH,-7)
            editingTo.add(Calendar.DAY_OF_MONTH,-1)
        }
        editingFrom.set(Calendar.HOUR,0)
        editingFrom.set(Calendar.HOUR_OF_DAY,0)
        editingFrom.set(Calendar.MINUTE,0)
        editingFrom.set(Calendar.SECOND,0)
        editingFrom.set(Calendar.MILLISECOND,0)
        editingTo.set(Calendar.HOUR,0)
        editingTo.set(Calendar.HOUR_OF_DAY,0)
        editingTo.set(Calendar.MINUTE,0)
        editingTo.set(Calendar.SECOND,0)
        editingTo.set(Calendar.MILLISECOND,0)
        var curr = Calendar.getInstance()
        curr.set(Calendar.HOUR,0)
        curr.set(Calendar.HOUR_OF_DAY,0)
        curr.set(Calendar.MINUTE,0)
        curr.set(Calendar.SECOND,0)
        curr.set(Calendar.MILLISECOND,0)
        if(curr.time.time >= editingFrom.time.time && curr.time.time <= editingTo.time.time){
            binding!!.llHomeMain.tvToday.setBackgroundResource(R.drawable.rounded_selected)
        }else{
            binding!!.llHomeMain.tvToday.setBackgroundResource(R.drawable.rounded_dark_blue)
        }
        var vl = VisitList()
        binding!!.llHomeMain.loading.show()
        filterDate(mainArray)
        if(editingFrom.get(Calendar.MONTH) != editingTo.get(Calendar.MONTH))
            binding!!.llHomeMain.tvDate.text = half.format(editingFrom.time) + " - "+secondHalf.format(editingTo.time)
        else
            binding!!.llHomeMain.tvDate.text = half.format(editingFrom.time) + " - "+secondNoMonthHalf.format(editingTo.time)
    }fun setUpDate(){
        var from = Calendar.getInstance()
        var to = Calendar.getInstance()
        from.add(Calendar.DAY_OF_MONTH,-2)
        to.add(Calendar.DAY_OF_MONTH,3)
        fromDefault = from
        toDefault = to
        editingFrom = from
        editingTo = to
        editingFrom.set(Calendar.HOUR,0)
        editingFrom.set(Calendar.HOUR_OF_DAY,0)
        editingFrom.set(Calendar.MINUTE,0)
        editingFrom.set(Calendar.SECOND,0)
        editingFrom.set(Calendar.MILLISECOND,0)
        editingTo.set(Calendar.HOUR,0)
        editingTo.set(Calendar.HOUR_OF_DAY,0)
        editingTo.set(Calendar.MINUTE,0)
        editingTo.set(Calendar.SECOND,0)
        editingTo.set(Calendar.MILLISECOND,0)
        filterDate(mainArray)
        if(editingFrom.get(Calendar.MONTH) != editingTo.get(Calendar.MONTH))
            binding!!.llHomeMain.tvDate.text = half.format(editingFrom.time) + " - "+secondHalf.format(editingTo.time)
        else
            binding!!.llHomeMain.tvDate.text = half.format(editingFrom.time) + " - "+secondNoMonthHalf.format(editingTo.time)
    }
    fun filterDate(arrayList: ArrayList<testVisitItem>){
        tempArray.clear()
        tempArray.addAll(arrayList.filter {
            simp.parse(it.visitDate).time >= editingFrom.time.time && simp.parse(it.visitDate).time <= editingTo.time.time
        }
        )
        Log.wtf("TAG_ARRAY",tempArray.size.toString())
        var vl = VisitList()
        vl.addAll(tempArray)
        setUpData(vl)
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
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                LocationForeService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }
    override fun onStart() {
        super.onStart()
        MyApplication.serviceContext = this
        val serviceIntent = Intent(this, LocationForeService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }
    fun setUpData(list: VisitList){
        if(list.size >0) {
            list.sortBy {
                simp.parse(it.visitDate).time
            }
            tempArray.clear()
            var headers = 0
            for (item in list) {
                var date = simp.parse(item.visitDate)
                Log.wtf("TAG_VISIT", item.title + " ---DATE: " + date.time)
                var itm = tempArray.find { it.dateMill == date.time }
                if (itm != null) {
                    var nextHead = tempArray.find {
                        it.headerOrder == (itm.headerOrder + 1)
                    }
                    if (nextHead == null)
                        tempArray.add(item)
                    else {
                        var indx = tempArray.indexOf(nextHead)
                        tempArray.add(indx, item)
                    }
                } else {
                    var visHeader = testVisitItem()
                    visHeader.isHeader = true
                    visHeader.visitDate = item.visitDate
                    visHeader.dateMill = date.time
                    visHeader.headerOrder = headers
                    headers++
                    tempArray.add(visHeader)
                    tempArray.add(item)
                }
            }
        }else{
            tempArray.clear()
        }
        var x : ArrayList<testVisitItem> = arrayListOf()
        x.addAll(tempArray)
        tempArray.clear()
        tempArray.addAll(x.filter {
            it.reasonId != AppConstants.SCHEDULED_REASON_ID
        })
        if(tempArray.size > 0) {
            var adapter = StickyAdapter(this, tempArray,this)
            binding!!.llHomeMain.rvVisits.layoutManager = LinearLayoutManager(this)
            binding!!.llHomeMain.rvVisits.adapter = adapter
            binding!!.llHomeMain.rvVisits.addItemDecoration(
                HeaderDecoration(
                    binding!!.llHomeMain.rvVisits,
                    adapter
                )
            );
            Log.wtf("Visitors", Gson().toJson(tempArray))
            binding!!.llHomeMain.loading.hide()
            binding!!.llHomeMain.rvVisits.show()
            binding!!.llHomeMain.tvNoVisits.hide()
        }else{
            binding!!.llHomeMain.loading.hide()
            binding!!.llHomeMain.rvVisits.hide()
            binding!!.llHomeMain.tvNoVisits.show()
        }
    }
    fun getVisits(){
        binding!!.llHomeMain.loading.show()
        RetrofitClientAuth.client?.create(RetrofitInterface::class.java)
            ?.getVisits(
               -1,
                MyApplication.userItem!!.applicationUserId!!.toInt()
            )?.enqueue(object : Callback<VisitList> {
                override fun onResponse(call: Call<VisitList>, response: Response<VisitList>) {
                    mainArray.clear()
                    mainArray.addAll(response.body()!!)
                    MyApplication.allVisits.clear()
                    MyApplication.allVisits.addAll(response.body()!!)
                    var visit = mainArray.find {
                        it.reasonId == AppConstants.ON_THE_WAY_REASON_ID
                    }
                    MyApplication.onTheWayVisit = visit
                    if(visit!=null)
                        changeState(true,0)
                    else
                        changeState(false , 0 )
                    filterDate(response.body()!!)
                }
                override fun onFailure(call: Call<VisitList>, throwable: Throwable) {
                }
            })
    }
    fun listeners(){
        binding!!.drawerMenu.tvWelcome.text = binding!!.drawerMenu.tvWelcome.text.toString() + MyApplication.userItem!!.firstName + " "+MyApplication.userItem!!.lastName
        binding!!.llHomeMain.ivDrawer.setOnClickListener {
            var  shake =  AnimationUtils.loadAnimation(this, R.anim.corner)
            binding!!.navView.startAnimation(shake)
            binding!!.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding!!.drawerMenu.llLogout.setOnClickListener {
            AppHelper.createYesNoDialog(this,getString(R.string.sure_logout),0){
                try {
                    foregroundOnlyLocationService!!.unsubscribeToLocationUpdates()
                    val intent = Intent()
                    intent.setClass(this, LocationForeService::class.java)
                    stopService(intent)
                }catch (ex:Exception){
                }
                finishAffinity()
                MyApplication.loggedIn = false
                startActivity(Intent(this,ActivityLogin::class.java))
            }
        }
        binding!!.drawerMenu.btArabic.hide()
        binding!!.drawerMenu.btEnglish.hide()
        binding!!.drawerMenu.btArabic.setOnClickListener {
            AppHelper.changeLanguage(this,"ar")
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
            finishAffinity()
        }
        binding!!.drawerMenu.btEnglish.setOnClickListener {
            AppHelper.changeLanguage(this,"en")
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
            finishAffinity()
        }
        binding!!.drawerMenu.tvAllTasks.setOnClickListener {
            startActivity(Intent(
                this,
                ActivityAllTasks::class.java
            ))
        }
        binding!!.drawerMenu.tvHome.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this,ActivityMain::class.java))
        }
        binding!!.drawerMenu.btClose.setOnClickListener {
            var  shake =  AnimationUtils.loadAnimation(this, R.anim.close_corner)
            binding!!.navView.startAnimation(shake)
            Handler(Looper.getMainLooper()).postDelayed({
                binding!!.drawerLayout.closeDrawers()
            }, 300)
        }
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AppHelper.createYesNoDialog(this@ActivityMain,getString(R.string.are_you_sure_exit),0){
                    finish()
                }
            }
        })
        binding!!.llHomeMain.btDatePrevious.setOnClickListener {
            binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_darker_left)
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_dark_right)
            editDate(false)
        }
        binding!!.llHomeMain.btDateNext.setOnClickListener {
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_darker_right)
            binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_dark_left)
            editDate(true)
        }
        binding!!.llHomeMain.tvToday.setOnClickListener {
            setUpDate()
            binding!!.llHomeMain.tvToday.setBackgroundResource(R.drawable.rounded_selected)
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_dark_right)
            binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_dark_left)
        }
    }
    override fun onItemClicked(view: View, position: Int) {
        MyApplication.selectedVisit = tempArray.get(position)
        startActivity(Intent(this,ActivtyVisitDetails::class.java))
    }
}