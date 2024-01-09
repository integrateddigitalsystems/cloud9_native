package com.ids.cloud9native.controller.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.activities.ActivtyVisitDetails
import com.ids.cloud9native.custom.MyDrawView
import com.ids.cloud9native.databinding.LayoutSignatureBinding
import com.ids.cloud9native.model.ResponseMessage
import com.ids.cloud9native.model.SignatureList
import com.ids.cloud9native.model.SignatureListItem
import com.ids.cloud9native.model.SignatureRequest
import com.ids.cloud9native.model.VisitLocationRequest
import com.ids.cloud9native.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class FragmentSignature : Fragment() {
    var emEy: SignatureListItem? = null
    var emCl: SignatureListItem? = null
    var myDrawViewEmpl: MyDrawView? = null
    var clientSaved = false
    var employeeSaved = false
    var myDrawViewClient: MyDrawView? = null
    var simpDate = SimpleDateFormat("yyyy-MM-dd_hhmmaa", Locale.ENGLISH)
    var simpDateSec = SimpleDateFormat("ss", Locale.ENGLISH)
    var binding: LayoutSignatureBinding? = null
    var arraySign: SignatureList? = null
    var mPermissionResult: ActivityResultLauncher<Array<String>>? = null
    val BLOCKED = -1
    val BLOCKED_OR_NEVER_ASKED = 2
    val GRANTED = 0
    val DENIED = 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutSignatureBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPermission()
        init()
        listeners()
    }


    fun lastDialog(doAction: () -> Unit){
        val client = !myDrawViewClient!!.isEmpty && !clientSaved
        val employee = !myDrawViewEmpl!!.isEmpty && !employeeSaved
        if(client || employee) {
            requireActivity().createReverseDialog(getString(R.string.not_saved_signature),getString(R.string.go_back), 0) {
                doAction()
            }
        }else{
            doAction()
        }
    }
    fun setUpPermission() {
        mPermissionResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { result ->
                var permission = true
                for (item in result) {
                    permission = item.value && permission
                }
                if (!permission) {
                    requireActivity().toast(getString(R.string.location_updates_disabled_arr))
                    for (item in result) {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                item.key
                            ) == BLOCKED
                        ) {

                            if (getPermissionStatus(Manifest.permission.ACCESS_FINE_LOCATION) == BLOCKED_OR_NEVER_ASKED) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.please_grant_permission),
                                    Toast.LENGTH_LONG
                                ).show()
                                break
                            }
                        }
                    }
                } else {
                    changeLocation(MyApplication.onTheWayVisit!!.id!!)
                }
            }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    @SuppressLint("ClickableViewAccessibility")
    fun listeners() {


        binding!!.redoClient.setOnClickListener {
            myDrawViewClient!!.onClickRedo()
            if (!myDrawViewClient!!.isEmpty()) {
                showSave(true, true)
            } else {
                showSave(true, false)
            }
        }
        binding!!.undoClient.setOnClickListener {
            myDrawViewClient!!.onClickUndo()
            if (!myDrawViewClient!!.isEmpty()) {
                clientSaved = false
                showSave(true, true)
            } else {
                showSave(true, false)
            }
        }
        binding!!.redoEmployee.setOnClickListener {
            myDrawViewEmpl!!.onClickRedo()
            if (!myDrawViewEmpl!!.isEmpty()) {
                employeeSaved = false
                showSave(false, true)
            } else {
                showSave(false, false)
            }
        }
        binding!!.undoEmployee.setOnClickListener {
            myDrawViewEmpl!!.onClickUndo()
            if (!myDrawViewEmpl!!.isEmpty()) {
                showSave(false, true)
            } else {
                showSave(false, false)
            }
        }
        binding!!.btClearClient.setOnClickListener {
            if (binding!!.drawClient.visibility == View.VISIBLE) {
                safeCall {
                    binding!!.drawClient.removeView(myDrawViewClient)
                }
                addClientSign()
                MyApplication.isSignatureEmptyClient = true
            } else {
                binding!!.drawClient.show()
                binding!!.ivSelectedDrawClient.hide()
            }
            showSave(true,false)
        }
        binding!!.btClearEmployee.setOnClickListener {
            if (binding!!.drawEmployee.visibility == View.VISIBLE) {
                safeCall {
                    binding!!.drawEmployee.removeView(myDrawViewEmpl)
                }
                addEmplSign()
                MyApplication.isSignatureEmptyEmp = true
            } else {
                binding!!.drawEmployee.show()
                binding!!.ivSelectedDrawEmployee.hide()
            }
            showSave(false,false)
        }
        binding!!.btSaveClient.setOnClickListener {
            val bit = AppHelper.encodeImage(getBitmapFromView(binding!!.drawClient))
            requireActivity().createActionDialog(
                getString(R.string.signature_completed),
                0
            ){
                saveSignature(bit!!, true)
            }

        }
        binding!!.btSaveEMployee.setOnClickListener {
            val bit = AppHelper.encodeImage(getBitmapFromView(binding!!.drawEmployee))
            saveSignature(bit!!, false)
        }
    }

    fun saveSignature(base64string: String, isClient: Boolean) {
        binding!!.llLoading.show()
        val signatureRequest: SignatureRequest
        val cal = Calendar.getInstance()
        signatureRequest = SignatureRequest(
            MyApplication.selectedVisit!!.id!!,
            simpDate.format(cal.time) + simpDateSec.format(cal.time) + "_" + MyApplication.userItem!!.applicationUserId + "_" + MyApplication.userItem!!.userName + "_signature.jpg",
            "data:image/jpeg;base64," + base64string,
            0,
            false,
            MyApplication.userItem!!.applicationUserId!!,
            !isClient,
            true
        )
        val signs = arrayListOf<SignatureRequest>()
        signs.add(signatureRequest)
        RetrofitClientSpecificAuth.client!!.create(
            RetrofitInterface::class.java
        ).saveSignature(
            signs
        ).enqueue(
            object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.body()!!.success.equals("true")) {
                        if(isClient) {
                            clientSaved = true
                            updateVisit()
                        }
                        else {
                            binding!!.llLoading.hide()
                            employeeSaved = true
                            createDialog(response.body()!!.message!!)
                        }
                    } else {
                        binding!!.llLoading.hide()
                        createDialog(response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                }
            }
        )
    }


    fun addEmplSign() {
        myDrawViewEmpl = MyDrawView(requireActivity())
        binding!!.drawEmployee.addView(myDrawViewEmpl)
        myDrawViewEmpl!!.setOnTouchListener { view, motionEvent ->
            employeeSaved = false
            showSave(false, true)
            false
        }
    }

    fun addClientSign() {
        myDrawViewClient = MyDrawView(requireActivity())
        binding!!.drawClient.addView(myDrawViewClient)
        myDrawViewClient!!.setOnTouchListener { view, motionEvent ->
            clientSaved = false
            showSave(true, true)
            false
        }
    }

    fun showSave(client: Boolean, show: Boolean) {
        if (client) {
            if (show) {
                binding!!.btSaveClient.setBackgroundResource(R.drawable.rounded_primary_layout)
                binding!!.btSaveClient.isEnabled = true
            } else {
                binding!!.btSaveClient.setBackgroundResource(R.drawable.rounded_gray_background)
                binding!!.btSaveClient.isEnabled = false
            }
        } else {
            if (show) {
                binding!!.btSaveEMployee.setBackgroundResource(R.drawable.rounded_primary_layout)
                binding!!.btSaveEMployee.isEnabled = true
            } else {
                binding!!.btSaveEMployee.setBackgroundResource(R.drawable.rounded_gray_background)
                binding!!.btSaveEMployee.isEnabled = false
            }
        }
    }

    fun init() {
        getSignatures()
        addEmplSign()
        addClientSign()
        if (MyApplication.selectedVisit!!.reasonId ==  AppHelper.getReasonID(AppConstants.REASON_PENDING)) {
            binding!!.drawEmployee.hide()
            binding!!.drawClient.hide()
            AppHelper.setImage(requireContext(), binding!!.ivSelectedDrawClient, "", true,450,200)
            AppHelper.setImage(requireContext(), binding!!.ivSelectedDrawEmployee, "", true,450,200)
            binding!!.btClearEmployee.isEnabled = false
            binding!!.btClearClient.isEnabled = false
            binding!!.btClearEmployee.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.btClearClient.setBackgroundResource(R.drawable.rounded_gray_background)
            showSave(true, false)
            showSave(false, false)
            binding!!.undoEmployee.hide()
            binding!!.redoEmployee.hide()
            binding!!.redoClient.hide()
            binding!!.undoClient.hide()
        }
    }

    fun setUpSignatures() {
        arraySign!!.sortBy { it.id }
        emEy = arraySign!!.find {
            it.signatureTypeLookup != null && it.signatureTypeLookup.equals(AppConstants.SIGNATURE_TYPE_EMPLOYEE)
        }
        emCl = arraySign!!.find {
            it.signatureTypeLookup != null && it.signatureTypeLookup.equals(AppConstants.SIGNATURE_TYPE_CLIENT)
        }
        if (emEy != null && !emEy!!.directory.isNullOrEmpty()) {
            binding!!.ivSelectedDrawEmployee.show()
            binding!!.drawEmployee.hide()
            AppHelper.setImage(
                requireActivity(),
                binding!!.ivSelectedDrawEmployee,
                /*MyApplication.BASE_URL_IMAGE +*/ emEy!!.directory!!,
                false
                ,450,200
            )
        } else {
            binding!!.ivSelectedDrawEmployee.hide()
            binding!!.drawEmployee.show()
        }
        if (emCl != null && !emCl!!.directory.isNullOrEmpty()) {
            binding!!.ivSelectedDrawClient.show()
            binding!!.drawClient.hide()
            AppHelper.setImage(
                requireActivity(),
                binding!!.ivSelectedDrawClient,
                /*MyApplication.BASE_URL_IMAGE +*/ emCl!!.directory!!,
                false,450,200
            )
        } else {
            binding!!.ivSelectedDrawClient.hide()
            binding!!.drawClient.show()
        }
        if (MyApplication.selectedVisit!!.reasonId ==  AppHelper.getReasonID(AppConstants.REASON_PENDING) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_COMPLETED) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_SCHEDULED)) {
            binding!!.drawEmployee.hide()
            binding!!.drawClient.hide()
            if (emCl == null)
                AppHelper.setImage(requireContext(), binding!!.ivSelectedDrawClient, "", true,450,200)
            if (emEy == null)
                AppHelper.setImage(requireContext(), binding!!.ivSelectedDrawEmployee, "", true,450,200)
            binding!!.ivSelectedDrawClient.show()
            binding!!.ivSelectedDrawEmployee.show()
            binding!!.btClearEmployee.isEnabled = false
            binding!!.btClearClient.isEnabled = false
            showSave(false, false)
            showSave(true, false)
            binding!!.btClearEmployee.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.btClearClient.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.undoEmployee.hide()
            binding!!.redoEmployee.hide()
            binding!!.redoClient.hide()
            binding!!.undoClient.hide()
        } else {
            binding!!.undoEmployee.show()
            binding!!.redoEmployee.show()
            binding!!.redoClient.show()
            binding!!.undoClient.show()
        }
        showSave(false,false)
        showSave(true, false)
        binding!!.llLoading.hide()
    }

    fun getSignatures() {
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).getSignatures(
            AppConstants.ENTITY_TYPE_CODE_SIGNATURE,
            MyApplication.selectedVisit!!.id!!
        ).enqueue(object : Callback<SignatureList> {
            override fun onResponse(call: Call<SignatureList>, response: Response<SignatureList>) {
                arraySign = response.body()
                setUpSignatures()
            }

            override fun onFailure(call: Call<SignatureList>, t: Throwable) {
                binding!!.llLoading.hide()
            }
        })
    }

    fun updateVisit() {
        for (item in MyApplication.onTheWayVisit!!.visitResources)
            item.id = 0
        val str = Gson().toJson(MyApplication.onTheWayVisit!!)
        MyApplication.onTheWayVisit!!.reasonId =AppHelper.getReasonID(AppConstants.REASON_COMPLETED)
        wtf(str)
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .updateVisit(MyApplication.userItem!!.applicationUserId!!.toInt(),MyApplication.onTheWayVisit!!)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    safeCall(binding!!.llLoading) {
                        binding!!.llLoading.hide()
                        MyApplication.selectedVisit = MyApplication.onTheWayVisit!!
                        if (response.code() != 500) {
                            FirebaseCrashlytics.getInstance().log("UPDATED:\n" + str)
                            FirebaseCrashlytics.getInstance()
                                .recordException(RuntimeException("UPDATED:\n" + str))

                                requireContext().createRetryDialog(
                                    response.body()!!.message!!) {
                                    changeLocation(MyApplication.onTheWayVisit!!.id!!)
                                    MyApplication.gettingTracked = false
                                    (requireActivity() as ActivtyVisitDetails).changeState(false)
                                    requireActivity().onBackPressedDispatcher.onBackPressed()

                                }


                        } else {
                            FirebaseCrashlytics.getInstance().log("UPDATE ERROR 500+ITEM:\n" + str)
                            FirebaseCrashlytics.getInstance()
                                .recordException(RuntimeException("UPDATE ERROR 500+ITEM:\n" + str))
                            requireContext().createRetryDialog(
                                getString(R.string.visit_succ)
                            ) {
                                changeLocation(MyApplication.onTheWayVisit!!.id!!)
                                MyApplication.gettingTracked = false
                                (requireActivity() as ActivtyVisitDetails).changeState(false)
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                    requireActivity().createDialog( getString(R.string.failure))
                }

            })
    }

    fun changeLocation(id: Int) {
        var firstLocation: Location? = null
        var gps_enabled = false
        var network_enabled = false
        val mLocationManager =
            requireActivity().getSystemService(Service.LOCATION_SERVICE) as LocationManager
        safeCall {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        safeCall {
            network_enabled =
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }



        if ((ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    ) && (gps_enabled || network_enabled)
        ) {


            if (gps_enabled && firstLocation == null)
                firstLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (network_enabled && firstLocation == null) {
                firstLocation =
                    mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            if (firstLocation != null) {
                val visitLocationRequest = VisitLocationRequest(
                    MyApplication.userItem!!.applicationUserId!!.toInt(),
                    0,
                    true,
                    firstLocation.latitude,
                    firstLocation.longitude,
                    id
                )
                Log.wtf("JAD_TEST_LOCATION", Gson().toJson(visitLocationRequest))
                RetrofitClientSpecificAuth.client!!.create(
                    RetrofitInterface::class.java
                ).createVisitLocation(
                    visitLocationRequest
                ).enqueue(object : Callback<ResponseMessage> {
                    override fun onResponse(
                        call: Call<ResponseMessage>,
                        response: Response<ResponseMessage>
                    ) {
                        try {
                            wtf(response.body()!!.message!!)
                        } catch (ex: Exception) {
                            wtf(getString(R.string.error_getting_data))
                        }
                    }

                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        wtf(getString(R.string.failure))
                    }

                })
            }else{
                changeLocation(id)
            }



        } else {
            if (gps_enabled && network_enabled)
                openChooser()
            else {
                requireActivity().createActionDialog(
                    getString(R.string.gps_settings), 0
                ) {
                    startActivity(
                        Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        )
                    )
                }
            }

        }
    }
    fun openChooser() {
        mPermissionResult!!.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    fun getPermissionStatus(androidPermissionName: String?): Int {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                androidPermissionName!!
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    androidPermissionName
                )
            ) {
                BLOCKED_OR_NEVER_ASKED
            } else DENIED
        } else GRANTED
    }
}