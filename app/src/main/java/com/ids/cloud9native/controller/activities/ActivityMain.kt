package com.ids.cloud9native.controller.activities

import android.content.*
import android.location.Location
import android.os.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.MyApplication.Companion.isFirstHome
import com.ids.cloud9native.controller.MyApplication.Companion.isFirstVisit
import com.ids.cloud9native.controller.MyApplication.Companion.isFirstGps
import com.ids.cloud9native.controller.MyApplication.Companion.toSettings
import com.ids.cloud9native.controller.MyApplication.Companion.toSettingsGps
import com.ids.cloud9native.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9native.controller.adapters.StickyAdapter
import com.ids.cloud9native.custom.AppCompactBase
import com.ids.cloud9native.databinding.ActivityMainBinding
import com.ids.cloud9native.model.*
import com.ids.cloud9native.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class ActivityMain : AppCompactBase(), RVOnItemClickListener {
    var simp = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH)
    var half = SimpleDateFormat("MMM dd", Locale.ENGLISH)
    var secondHalf = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
    var secondNoMonthHalf = SimpleDateFormat("dd, yyyy", Locale.ENGLISH)
    var binding: ActivityMainBinding? = null
    var fromDefault = Calendar.getInstance()
    var toDefault = Calendar.getInstance()
    var afterApi = false
    var doneOnce = 0
    var visitIdNotf = -1
    var editingFrom = Calendar.getInstance()
    var editingTo = Calendar.getInstance()
    var tempArray: ArrayList<Visit> = arrayListOf()
    var mainArray: ArrayList<Visit> = arrayListOf()
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver


    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(LocationForeService.EXTRA_LOCATION, Location::class.java)
            } else {
                intent.getParcelableExtra(
                    LocationForeService.EXTRA_LOCATION
                )
            }
            if (location != null) {
                wtf("Foreground location: ${location.toText()}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        if (visitIdNotf == -1) {
            visitIdNotf = intent.getIntExtra("visitId", -1)
        }
        if (visitIdNotf != -1) {
            getVisits()
        }
        if (isFirstHome){
            toSettingsGps =true
            toSettings =true
        }
        safeCall {
            setUpPermission()
            listeners()
        }

    }
    fun editDate(isNext: Boolean) {
        if (isNext) {
            val oldEdge = editingTo.time
            editingFrom.time = oldEdge
            editingTo.time = oldEdge
            editingFrom.add(Calendar.DAY_OF_MONTH, 1)
            editingTo.add(Calendar.DAY_OF_MONTH, 7)

        } else {
            val oldEdge = editingFrom.time
            editingFrom.time = oldEdge
            editingTo.time = oldEdge
            editingFrom.add(Calendar.DAY_OF_MONTH, -7)
            editingTo.add(Calendar.DAY_OF_MONTH, -1)
        }
        val curr = Calendar.getInstance()
        editingFrom.time = secondHalf.parse(secondHalf.format(editingFrom.time))!!
        editingTo.time = secondHalf.parse(secondHalf.format(editingTo.time))!!
        curr.time = secondHalf.parse(secondHalf.format(curr.time))!!


        if (curr.time.time >= editingFrom.time.time && curr.time.time <= editingTo.time.time) {
            binding!!.llHomeMain.tvToday.setBackgroundResource(R.drawable.rounded_selected)
        } else {
            binding!!.llHomeMain.tvToday.setBackgroundResource(R.drawable.rounded_dark_blue)
        }
        binding!!.llHomeMain.loading.show()
        filterDate(mainArray)
        if (editingFrom.get(Calendar.MONTH) != editingTo.get(Calendar.MONTH))
            binding!!.llHomeMain.tvDate.text = half.format(editingFrom.time) + " - " + secondHalf.format(editingTo.time)
        else
            binding!!.llHomeMain.tvDate.text =
                half.format(editingFrom.time) + " - " + secondNoMonthHalf.format(editingTo.time)
    }

    fun setUpDate() {
        val from = Calendar.getInstance()
        val to = Calendar.getInstance()
        from.add(Calendar.DAY_OF_MONTH, -3)
        to.add(Calendar.DAY_OF_MONTH, 3)
        fromDefault = from
        toDefault = to
        editingFrom = from
        editingTo = to
        editingFrom.time = secondHalf.parse(secondHalf.format(editingFrom.time))!!
        editingTo.time = secondHalf.parse(secondHalf.format(editingTo.time))!!
        filterDate(mainArray)
        if (editingFrom.get(Calendar.MONTH) != editingTo.get(Calendar.MONTH))
            binding!!.llHomeMain.tvDate.text =
                half.format(editingFrom.time) + " - " + secondHalf.format(editingTo.time)
        else
            binding!!.llHomeMain.tvDate.text =
                half.format(editingFrom.time) + " - " + secondNoMonthHalf.format(editingTo.time)
    }

    fun filterDate(arrayList: ArrayList<Visit>) {
        tempArray.clear()
        tempArray.addAll(arrayList.filter {
            simp.parse(it.visitDate!!)!!.time >= editingFrom.time.time && simp.parse(it.visitDate!!)!!.time <= editingTo.time.time
        }
        )
        wtf(tempArray.size.toString())
        val vl = VisitList()
        vl.addAll(tempArray)
        setUpData(vl)
    }

/*    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        super.onStop()
    }*/
    override fun onPause() {
        MyApplication.activityPaused()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        MyApplication.activityResumed()
        if(!this::foregroundOnlyBroadcastReceiver.isInitialized)
            foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            registerReceiver(
                foregroundOnlyBroadcastReceiver,
                IntentFilter(
                    LocationForeService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
                ), RECEIVER_NOT_EXPORTED)
        }else{
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                LocationForeService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            ))
        }

        val mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                getVisits()
            }
        }
        val filter = IntentFilter("msg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            registerReceiver(mBroadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
        else
            registerReceiver(mBroadcastReceiver, filter)

        if (doneOnce == 0) {
            doneOnce = 1
            setUpDate()
        }
        getVisits()

    }

/*    override fun onStart() {
        super.onStart()
        MyApplication.serviceContext = this
        val serviceIntent = Intent(this, LocationForeService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }*/

    fun setUpData(list: VisitList) {
        if (list.size > 0) {
            list.sortBy {
                simp.parse(it.visitDate!!)!!.time
            }
            tempArray.clear()
            var headers = 0
            for (item in list) {
                val date = simp.parse(item.visitDate!!)
                wtf(item.title + " ---DATE: " + date!!.time)
                val itm = tempArray.find { it.dateMill == date.time }
                if (itm != null) {
                    val nextHead = tempArray.find {
                        it.headerOrder == (itm.headerOrder + 1)
                    }
                    if (nextHead == null)
                        tempArray.add(item)
                    else {
                        val indx = tempArray.indexOf(nextHead)
                        tempArray.add(indx, item)
                    }
                } else {
                    val visHeader = Visit()
                    visHeader.isHeader = true
                    visHeader.visitDate = item.visitDate
                    visHeader.dateMill = date.time
                    visHeader.headerOrder = headers
                    headers++
                    tempArray.add(visHeader)
                    tempArray.add(item)
                }
            }
        } else {
            tempArray.clear()
        }
        val currTemp: ArrayList<Visit> = arrayListOf()
        currTemp.addAll(tempArray)
        tempArray.clear()
        tempArray.addAll(currTemp)
        for (it in tempArray) {
            it.appearDuration = AppHelper.durationToString(it.duration!!.toFloat())
            var millFrom  = Calendar.getInstance().timeInMillis
            var millTo = Calendar.getInstance().timeInMillis
            safeCall {
                 millFrom = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ssss",
                    Locale.ENGLISH
                ).parse(it.fromTime!!)!!.time
            }
            safeCall{
                millTo = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ssss",
                    Locale.ENGLISH
                ).parse(it.toTime!!)!!.time
            }
            var reasonStatus: String
            var reasonBg:Int
            var reasonText:Int
            if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)){
                reasonBg = R.drawable.arrived_bg
                reasonText = R.color.arrived_text
                reasonStatus =AppHelper.getReasonName(AppConstants.REASON_ARRIVED)
            }else if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_COMPLETED)){
                reasonBg = R.drawable.completed_bg
                reasonText = R.color.comp_text
                reasonStatus = AppHelper.getReasonName(AppConstants.REASON_COMPLETED)
            }else if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_SCHEDULED)){
                reasonBg = R.drawable.scheduled_bg
                reasonText = R.color.scheduled_text
                reasonStatus = AppHelper.getReasonName(AppConstants.REASON_SCHEDULED)
            }else if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY)){
                reasonBg = R.drawable.on_the_way_bg
                reasonText = R.color.otw_text
                reasonStatus =AppHelper.getReasonName(AppConstants.REASON_ON_THE_WAY)
            }else if(it.reasonId == AppHelper.getReasonID(AppConstants.REASON_PENDING)){
                reasonBg = R.drawable.pending_bg
                reasonText = R.color.pending_text
                reasonStatus = AppHelper.getReasonName(AppConstants.REASON_PENDING)
            }else{
                reasonBg = R.drawable.on_the_way_bg
                reasonText = R.color.otw_text
                reasonStatus =AppHelper.getReasonName(AppConstants.REASON_ON_THE_WAY)
            }
            val call = Calendar.getInstance()
            val dateMil = SimpleDateFormat(
                "yyyy-MM-dd'T'hh:mm:ss",
                Locale.ENGLISH
            ).parse(it.visitDate!!)!!.time
            call.time.time = dateMil
            val day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(dateMil))
            val date = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(Date(dateMil))
            val cal = Calendar.getInstance()
            val visitDate = it.visitDate
            val original = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH)
            val toForm = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val visitMil = toForm.parse(toForm.format(original.parse(visitDate!!)!!))
            val today = toForm.parse(toForm.format(cal.time))
            var tod = false
            if (visitMil!!.time == today!!.time  && it.isHeader!!) {
                tod = true
            }
            val show = ShowStickyItem(
                it.isHeader,
                it.title,
                if(it.company!=null && !it.company!!.companyName.isNullOrEmpty()) it.company!!.companyName else "",
                SimpleDateFormat(
                    "hh:mm aa",
                    Locale.ENGLISH
                ).format(Date(millFrom)) + "-\n" + SimpleDateFormat(
                    "hh:mm aa",
                    Locale.ENGLISH
                ).format(Date(millTo)) + " (" + it.appearDuration + ")",
                reasonStatus,
                day,
                date,
                it.reasonId,
                tod,
                reasonText,
                reasonBg
            )
            it.showData = show
        }
        if (tempArray.size > 0) {
            binding!!.llHomeMain.nvTop.smoothScrollTo(0,0)
            val adapter = StickyAdapter(this, tempArray, this)
            binding!!.llHomeMain.rvVisits.layoutManager = LinearLayoutManager(this)
            binding!!.llHomeMain.rvVisits.adapter = adapter
            binding!!.llHomeMain.rvVisits.isNestedScrollingEnabled = false
       /*     binding!!.llHomeMain.rvVisits.addItemDecoration(
                HeaderDecoration(
                    binding!!.llHomeMain.rvVisits,
                    adapter
                )
            )*/
            wtf(Gson().toJson(tempArray))
            binding!!.llHomeMain.loading.hide()
            binding!!.llHomeMain.rvVisits.show()
            binding!!.llHomeMain.tvNoVisits.hide()
        } else {
            if (afterApi) {
                binding!!.llHomeMain.loading.hide()
                binding!!.llHomeMain.rvVisits.hide()
                binding!!.llHomeMain.tvNoVisits.show()
            }
        }
        binding!!.llHomeMain.srVisits.isRefreshing = false

    }

    fun getVisits() {
        binding!!.llHomeMain.loading.show()
        RetrofitClientSpecificAuth.client?.create(RetrofitInterface::class.java)
            ?.getVisits(
                -1,
                MyApplication.userItem!!.applicationUserId!!.toInt()
            )?.enqueue(object : Callback<VisitList> {
                override fun onResponse(call: Call<VisitList>, response: Response<VisitList>) {
                    mainArray.clear()
                    mainArray.addAll(response.body()!!)
                    if (visitIdNotf != -1) {
                        MyApplication.selectedVisit = mainArray.find {
                            it.id == visitIdNotf
                        }
                        startActivity(
                            Intent(
                                this@ActivityMain,
                                ActivtyVisitDetails::class.java
                            )
                        )
                    }
                    MyApplication.allVisits.clear()
                    MyApplication.allVisits.addAll(response.body()!!)
                    val visit = mainArray.find {
                        it.reasonId == AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY) ||   it.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)
                    }
                    if (visit != null) {
                        MyApplication.onTheWayVisit = visit
                        MyApplication.gettingTracked = true
                        changeState(true)
                    }else {
                        MyApplication.gettingTracked = false
                        changeState(false)
                    }
                    afterApi = true


                    filterDate(response.body()!!)
                }

                override fun onFailure(call: Call<VisitList>, throwable: Throwable) {
                    binding!!.llHomeMain.loading.hide()
                    filterDate(arrayListOf())
                }
            })
    }
    fun logoutStep(){
        safeCall {
            MyApplication.saveLocTracking = false
            MyApplication.gettingTracked = false
            foregroundOnlyLocationService!!.unsubscribeToLocationUpdates()

            val intent = Intent()
            intent.setClass(this, LocationForeService::class.java)
            stopService(intent)
        }
        finishAffinity()
        MyApplication.loggedIn = false
        startActivity(Intent(this, ActivityLogin::class.java))
    }
    fun updateDevice(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            { task ->

                val imei = Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                val updateDevice = UpdateDeviceRequest(
                    AppHelper.getAndroidVersion(),
                    MyApplication.simpleDate.format(Calendar.getInstance().time),
                    "",
                    AppHelper.getDeviceName(),
                    task.result,
                    2,
                    MyApplication.deviceId,
                    MyApplication.languageCode,
                    "",
                    true,
                    imei
                )
                RetrofitClient.client!!.create(RetrofitInterface::class.java).updateDevice(
                    updateDevice
                ).enqueue(object : Callback<UpdateDeviceResponse> {
                    override fun onResponse(
                        call: Call<UpdateDeviceResponse>,
                        response: Response<UpdateDeviceResponse>
                    ) {
                        logoutStep()
                    }

                    override fun onFailure(call: Call<UpdateDeviceResponse>, t: Throwable) {
                        createRetryDialog(
                            getString(R.string.error_getting_data)
                        ) {
                           updateDevice()
                        }
                    }
                })
            }
        )
    }

    fun listeners() {
        binding!!.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }
            override fun onDrawerOpened(drawerView: View) {

            }
            override fun onDrawerClosed(drawerView: View) {
               /* val shake = AnimationUtils.loadAnimation(this@ActivityMain, R.anim.close_corner)
                binding!!.navView.startAnimation(shake)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding!!.drawerLayout.closeDrawers()
                }, 300)*/
            }

            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_DRAGGING){
                    if (!binding!!.drawerLayout.isDrawerOpen(GravityCompat.START)){
                        val shake = AnimationUtils.loadAnimation(this@ActivityMain, R.anim.corner)
                        binding!!.navView.startAnimation(shake)
                        binding!!.drawerLayout.openDrawer(GravityCompat.START)
                    }else{

                        val shake = AnimationUtils.loadAnimation(this@ActivityMain, R.anim.close_corner)
                        binding!!.navView.startAnimation(shake)
                        binding!!.drawerLayout.closeDrawers()
                      /*  Handler(Looper.getMainLooper()).postDelayed({
                            binding!!.drawerLayout.closeDrawers()
                        }, 300)*/
                    }
                }

            }
        })



        binding!!.drawerMenu.tvWelcome.text = binding!!.drawerMenu.tvWelcome.text.toString() + MyApplication.userItem!!.firstName + " " + MyApplication.userItem!!.lastName
        binding!!.llHomeMain.ivDrawer.setOnClickListener {
            val shake = AnimationUtils.loadAnimation(this, R.anim.corner)
            binding!!.navView.startAnimation(shake)
            binding!!.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding!!.drawerMenu.llLogout.setOnClickListener {
            createActionDialog(
                getString(R.string.sure_logout),
                0
            ) {
                updateDevice()
            }
        }
        binding!!.drawerMenu.btArabic.hide()
        binding!!.drawerMenu.btEnglish.hide()
        binding!!.drawerMenu.btArabic.setOnClickListener {
            AppHelper.changeLanguage(this, "ar")
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
            finishAffinity()
        }
        binding!!.drawerMenu.btEnglish.setOnClickListener {
            AppHelper.changeLanguage(this, "en")
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
            finishAffinity()
        }
        binding!!.drawerMenu.tvAllTasks.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ActivityAllTasks::class.java
                )
            )
            val shake = AnimationUtils.loadAnimation(this, R.anim.close_corner)
            binding!!.navView.startAnimation(shake)
            binding!!.drawerLayout.closeDrawers()
          /*  Handler(Looper.getMainLooper()).postDelayed({
                binding!!.drawerLayout.closeDrawers()
            }, 300)*/
        }
        binding!!.drawerMenu.tvHome.setOnClickListener {
            val shake = AnimationUtils.loadAnimation(this, R.anim.close_corner)
            binding!!.navView.startAnimation(shake)
            binding!!.drawerLayout.closeDrawers()
   /*         Handler(Looper.getMainLooper()).postDelayed({
                binding!!.drawerLayout.closeDrawers()
            }, 300)*/
        }
        binding!!.drawerMenu.btClose.setOnClickListener {
            val shake = AnimationUtils.loadAnimation(this, R.anim.close_corner)
            binding!!.navView.startAnimation(shake)
            binding!!.drawerLayout.closeDrawers()
        /*    Handler(Looper.getMainLooper()).postDelayed({
                binding!!.drawerLayout.closeDrawers()
            }, 300)*/
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                createActionDialog(
                    getString(R.string.are_you_sure_exit),
                    0
                ) {
                    finish()
                    isFirstHome =true
                    isFirstGps =true
                    isFirstVisit=true

                }
            }
        })
        binding!!.llHomeMain.btDatePrevious.setOnClickListener {
            binding!!.llHomeMain.nvTop.smoothScrollTo(0,0)
            binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_darker_left)
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_dark_right)
            editDate(false)
        }
        binding!!.llHomeMain.btDateNext.setOnClickListener {
            binding!!.llHomeMain.nvTop.smoothScrollTo(0,0)
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_darker_right)
            binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_dark_left)
            editDate(true)
        }
        binding!!.llHomeMain.tvToday.setOnClickListener {
            binding!!.llHomeMain.nvTop.smoothScrollTo(0,0)
            binding!!.llHomeMain.tvToday.setBackgroundResource(R.drawable.rounded_selected)
            binding!!.llHomeMain.btDateNext.setBackgroundResource(R.drawable.rounded_dark_right)
            binding!!.llHomeMain.btDatePrevious.setBackgroundResource(R.drawable.rounded_dark_left)
            setUpDate()
        }
        binding!!.llHomeMain.srVisits.setOnRefreshListener {
            getVisits()
        }
    }
    override fun onItemClicked(view: View, position: Int) {
        MyApplication.selectedVisit = tempArray.get(position)
        Log.wtf("JSON_DATA",Gson().toJson(MyApplication.selectedVisit))
        startActivity(Intent(this, ActivtyVisitDetails::class.java))
    }

}