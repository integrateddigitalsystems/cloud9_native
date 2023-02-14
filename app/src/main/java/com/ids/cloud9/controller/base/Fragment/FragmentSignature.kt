package com.ids.cloud9.controller.Fragment

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
import java.util.Calendar

class FragmentSignature : Fragment() {

    var emEy : SignatureListItem ?=null
    var emCl : SignatureListItem ?=null
    var simpDate = SimpleDateFormat("yyyy-MM-dd_hhmmaa")
    var simpDateSec = SimpleDateFormat("ss")
    var binding : LayoutSignatureBinding?=null
    var arraySign : SignatureList ?=null

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


    fun listeners(){
        binding!!.redoClient.setOnClickListener {
            binding!!.drawClient.redo()
        }

        binding!!.undoClient.setOnClickListener {
            binding!!.drawClient.undo()
        }

        binding!!.redoEmployee.setOnClickListener {
            binding!!.drawEmployee.redo()
        }

        binding!!.undoEmployee.setOnClickListener {
            binding!!.drawEmployee.undo()
        }

        binding!!.btClearClient.setOnClickListener {
            if(binding!!.drawClient.visibility == View.VISIBLE)
                binding!!.drawClient.clearCanvas()
            else{
                binding!!.drawClient.show()
                binding!!.ivSelectedDrawClient.hide()
            }
        }

        binding!!.btClearEmployee.setOnClickListener {
            if(binding!!.drawEmployee.visibility == View.VISIBLE)
                binding!!.drawEmployee.clearCanvas()
            else{
                binding!!.drawEmployee.show()
                binding!!.ivSelectedDrawEmployee.hide()
            }
        }

        binding!!.btSaveClient.setOnClickListener {
            var bit = AppHelper.encodeImage(getBitmapFromView(binding!!.drawClient))
            saveSignature(bit!! , true )
        }

        binding!!.btSaveEMployee.setOnClickListener {
            var bit = AppHelper.encodeImage(getBitmapFromView(binding!!.drawEmployee))
            saveSignature(bit!! , false )
        }
    }

    fun saveSignature(base64string  : String , isClient : Boolean){

        binding!!.llLoading.show()
        var signatureRequest : SignatureRequest ?=null
        var cal = Calendar.getInstance()

        signatureRequest = SignatureRequest(
            MyApplication.selectedVisit!!.id!!,
            simpDate.format(cal.time)+simpDateSec.format(cal.time)+"_"+MyApplication.userItem!!.applicationUserId+"_"+MyApplication.userItem!!.userName+"_signature.jpg",
            "data:image/jpeg;base64,"+base64string,
            0,
            false,
            MyApplication.userItem!!.applicationUserId!!,
            !isClient,
            true

        )
        var signs = arrayListOf<SignatureRequest>()
        signs.add(signatureRequest)
        Log.wtf("LOGTAGE",Gson().toJson(signatureRequest))
        RetrofitClientAuth.client!!.create(
            RetrofitInterface::class.java
        ).saveSignature(
           signs
        ).enqueue(
            object : Callback<ResponseMessage>{
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if(response.body()!!.success.equals("true")){
                        AppHelper.createDialogPositive(requireActivity(),response.body()!!.message!!)
                    }else{
                        AppHelper.createDialogPositive(requireActivity(),response.body()!!.message!!)
                    }

                    binding!!.llLoading.hide()
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                }

            }
        )
    }

    fun init(){
        getSignatures()

        if(MyApplication.selectedVisit!!.reasonId == AppConstants.PENDING_REASON_ID)
        {
            binding!!.drawEmployee.hide()
            binding!!.drawClient.hide()
            AppHelper.setImage(requireContext(),binding!!.ivSelectedDrawClient,"",true)
            AppHelper.setImage(requireContext(),binding!!.ivSelectedDrawEmployee,"",true)
            binding!!.btSaveClient.isEnabled = false
            binding!!.btSaveEMployee.isEnabled = false
            binding!!.btClearEmployee.isEnabled = false
            binding!!.btClearClient.isEnabled = false
            binding!!.btSaveClient.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.btSaveEMployee.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.btClearEmployee.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.btClearClient.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.undoEmployee.hide()
            binding!!.redoEmployee.hide()
            binding!!.redoClient.hide()
            binding!!.undoClient.hide()
        }
    }

    fun setUpSignatures(){
        arraySign!!.sortBy { it.id }
        emEy = arraySign!!.find {
            it.signatureTypeLookup!=null &&  it.signatureTypeLookup.equals(AppConstants.SIGNATURE_TYPE_EMPLOYEE)
        }
        emCl = arraySign!!.find {
            it.signatureTypeLookup!=null && it.signatureTypeLookup.equals(AppConstants.SIGNATURE_TYPE_CLIENT)
        }

        if(emEy!=null && !emEy!!.directory.isNullOrEmpty()){
            binding!!.ivSelectedDrawEmployee.show()
            binding!!.drawEmployee.hide()
            AppHelper.setImage(requireActivity(),binding!!.ivSelectedDrawEmployee,MyApplication.BASE_URL_IMAGE+emEy!!.directory,false)
        }else{
            binding!!.ivSelectedDrawEmployee.hide()
            binding!!.drawEmployee.show()
        }

        if(emCl!=null && !emCl!!.directory.isNullOrEmpty()){
            binding!!.ivSelectedDrawClient.show()
            binding!!.drawClient.hide()
            AppHelper.setImage(requireActivity(),binding!!.ivSelectedDrawClient,MyApplication.BASE_URL_IMAGE+emCl!!.directory,false)
        }else{
            binding!!.ivSelectedDrawClient.hide()
            binding!!.drawClient.show()
        }

        if(MyApplication.selectedVisit!!.reasonId == AppConstants.PENDING_REASON_ID || MyApplication.selectedVisit!!.reasonId == AppConstants.COMPLETED_REASON_ID || MyApplication.selectedVisit!!.reasonId == AppConstants.ON_THE_WAY_REASON_ID)
        {
            binding!!.drawEmployee.hide()
            binding!!.drawClient.hide()
            if(emCl==null)
                AppHelper.setImage(requireContext(),binding!!.ivSelectedDrawClient,"",true)
            if(emEy==null)
                AppHelper.setImage(requireContext(),binding!!.ivSelectedDrawEmployee,"",true)
            binding!!.ivSelectedDrawClient.show()
            binding!!.ivSelectedDrawEmployee.show()
            binding!!.btSaveClient.isEnabled = false
            binding!!.btSaveEMployee.isEnabled = false
            binding!!.btClearEmployee.isEnabled = false
            binding!!.btClearClient.isEnabled = false
            binding!!.btSaveClient.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.btSaveEMployee.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.btClearEmployee.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.btClearClient.setBackgroundResource(R.drawable.rounded_gray_background)
            binding!!.undoEmployee.hide()
            binding!!.redoEmployee.hide()
            binding!!.redoClient.hide()
            binding!!.undoClient.hide()
        }
        binding!!.llLoading.hide()
    }


    fun getSignatures(){
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getSignatures(
            AppConstants.ENTITY_TYPE_CODE_SIGNATURE,
            MyApplication.selectedVisit!!.id!!
        ).enqueue(object : Callback<SignatureList>{
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