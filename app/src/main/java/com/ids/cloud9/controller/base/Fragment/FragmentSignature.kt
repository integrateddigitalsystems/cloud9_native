package com.ids.cloud9.controller.Fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.custom.MyDrawView
import com.ids.cloud9.databinding.LayoutSignatureBinding
import com.ids.cloud9.model.ResponseMessage
import com.ids.cloud9.model.SignatureList
import com.ids.cloud9.model.SignatureListItem
import com.ids.cloud9.model.SignatureRequest
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sign

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
            saveSignature(bit!!, true)
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
        var str = Gson().toJson(signs)
        Log.wtf("JAD_MEDIA_MEDIA",str)
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
                        if(isClient)
                            clientSaved = true
                        else
                            employeeSaved = true
                        createDialog(response.body()!!.message!!)
                    } else {
                        createDialog(response.body()!!.message!!)
                    }
                    binding!!.llLoading.hide()
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
            AppHelper.setImage(requireContext(), binding!!.ivSelectedDrawClient, "", true)
            AppHelper.setImage(requireContext(), binding!!.ivSelectedDrawEmployee, "", true)
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
                false
            )
        } else {
            binding!!.ivSelectedDrawClient.hide()
            binding!!.drawClient.show()
        }
        if (MyApplication.selectedVisit!!.reasonId ==  AppHelper.getReasonID(AppConstants.REASON_PENDING) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_COMPLETED) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ON_THE_WAY) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_SCHEDULED)) {
            binding!!.drawEmployee.hide()
            binding!!.drawClient.hide()
            if (emCl == null)
                AppHelper.setImage(requireContext(), binding!!.ivSelectedDrawClient, "", true)
            if (emEy == null)
                AppHelper.setImage(requireContext(), binding!!.ivSelectedDrawEmployee, "", true)
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
}