package com.ids.cloud9.controller.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.AdapterText
import com.ids.cloud9.controller.adapters.AdapterUser
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.ActivityAddReccomendBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class ActivityAddReccomendations : AppCompactBase(), RVOnItemClickListener {

    var binding: ActivityAddReccomendBinding? = null
    var adapter: AdapterText? = null
    private var simp = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
    private var simpOrg = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    private var adapterUser: AdapterUser? = null
    private var arraySpinner: ArrayList<ItemSpinner> = arrayListOf()
    var arrayUsers: ArrayList<ApplicationUserListItem> = arrayListOf()
    private var arrayUserSelected: ArrayList<ApplicationUserListItem> = arrayListOf()


    override fun onResume() {
        super.onResume()
        MyApplication.activityResumed()
    }

    override fun onPause() {
        super.onPause()
        MyApplication.activityPaused()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReccomendBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }
    private fun popupDate() {
        val myCalendar: Calendar = Calendar.getInstance()
        val DatePicker =
            DatePickerDialog(
                this,
                0,
                { _, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    binding!!.tvDueDate.text = simp.format(myCalendar.time)
                },
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
        DatePicker.show()
    }
    fun listeners() {
        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.layoutFragment.show()
        if(MyApplication.selectedReccomend!=null)
            binding!!.llTool.tvTitleTool.text = MyApplication.selectedReccomend!!.subject
        else {
            binding!!.llTool.tvTitleTool.text = getString(R.string.task)
            binding!!.lLDelete.hide()
        }

        binding!!.llMainLayout.setOnClickListener {
            if(binding!!.llNameSelect.visibility == View.VISIBLE){
                binding!!.llNameSelect.hide()
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
        binding!!.llTool.btBack.setOnClickListener {
           onBackPressedDispatcher.onBackPressed()
        }
        binding!!.llAssignTo.setOnClickListener {
            if (binding!!.llNameSelect.visibility == View.GONE)
                  binding!!.llNameSelect.show()
            else  binding!!.llNameSelect.hide()
        }
        binding!!.ivSelectUser.setOnClickListener {
            if (binding!!.llNameSelect.visibility == View.GONE)
                 binding!!.llNameSelect.show()
            else binding!!.llNameSelect.hide()
        }
        binding!!.tvDueDate.setOnClickListener {
            popupDate()
        }
        binding!!.lLDelete.setOnClickListener {
            createActionDialog(
                getString(R.string.you_wanna_delete),
                0
            ){
                deleteReccomend()
            }
        }
        binding!!.llTool.llToolbarLayout.setOnClickListener {
            if(binding!!.llNameSelect.visibility == View.VISIBLE){
                binding!!.llNameSelect.hide()
            }
        }
        binding!!.btSave.setOnClickListener {
            if (!binding!!.etSubject.text.toString()
                    .isEmpty() && !binding!!.tvDueDate.text.toString()
                    .isEmpty() && !binding!!.etDesc.text.toString()
                    .isEmpty() && arrayUserSelected.size > 0
            ) {
                if(binding!!.etSubject.text!!.length >=5)
                    createReccomend()
                else{
                    createDialog( getString(R.string.less_than_5))
                }

            } else if (binding!!.etSubject.text.toString()
                    .isNotEmpty() && binding!!.tvDueDate.text.toString()
                    .isNotEmpty() && binding!!.etDesc.text.toString()
                    .isNotEmpty() && MyApplication.selectedReccomend != null
            ) {

                if(binding!!.etSubject.text!!.length >=5)
                    updateReccomend()
                else{
                    createDialog( getString(R.string.less_than_5))
                }
            } else {
                createDialog( getString(R.string.fill_details))
            }
        }
        if (MyApplication.selectedReccomend == null) {
            binding!!.llAssignTo.show()
            binding!!.llEditAssign.hide()
        } else {
            binding!!.llAssignTo.hide()
            binding!!.llEditAssign.show()
            binding!!.tvName.text = MyApplication.selectedReccomend!!.assignedTo
            binding!!.etSubject.text = MyApplication.selectedReccomend!!.subject.toEditable()
            binding!!.etDesc.text = MyApplication.selectedReccomend!!.description.toEditable()
            binding!!.tvDueDate.text =
                simp.format(simpOrg.parse(MyApplication.selectedReccomend!!.dueDate)!!)
        }
    }
    private fun updateReccomend() {
        binding!!.llLoading.show()
        MyApplication.selectedReccomend!!.dueDate = simpOrg.format(simp.parse(binding!!.tvDueDate.text.toString())!!)
        MyApplication.selectedReccomend!!.subject = binding!!.etSubject.text.toString()
        MyApplication.selectedReccomend!!.description = binding!!.etDesc.text.toString()
        wtf(Gson().toJson(MyApplication.selectedReccomend))
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .updateActivity(
                MyApplication.selectedReccomend!!
            ).enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.body()!!.success.equals("true")) {
                        createRetryDialog( response.body()!!.message!!
                        ) {
                            finish()
                        }
                    } else {
                        createDialog(
                            response.body()!!.message!!
                        )
                        binding!!.llLoading.hide()
                    }
                }
                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                }
            })
    }
    private fun deleteReccomend(){
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).deleteActivity(
            MyApplication.selectedReccomend!!.id
        ).enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if(response.body()!!.success.equals("true")){
                    binding!!.llLoading.hide()
                    finish()
                    toast(getString(R.string.task_deleted_successfully))
                 /*   toast(response.body()!!.message!!)*/
                }else{
                    binding!!.llLoading.hide()
                }
            }
            override fun onFailure(call: Call<ResponseMessage>, throwable: Throwable) {
                binding!!.llLoading.hide()
            }
        })
    }
    private fun createReccomend() {
        binding!!.llLoading.show()
        val arrId: ArrayList<Int> = arrayListOf()
            for (item in arrayUserSelected)
                arrId.add(item.id)
        val createActivity = CreateActivity(
            null,
            binding!!.etDesc.text.toString(),
            MyApplication.selectedVisit!!.id,
            arrId,
            MyApplication.userItem!!.applicationUserId,
            binding!!.etSubject.text.toString(),
            binding!!.tvDueDate.text.toString()
        )
        wtf(Gson().toJson(createActivity))
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .createActivity(
                createActivity
            ).enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.body()!!.success.equals("true")) {
                        createRetryDialog(
                            response.body()!!.message!!
                        ) {
                            finish()
                        }
                    } else {
                        binding!!.llLoading.hide()
                        createDialog(
                            response.body()!!.message!!
                        )
                    }

                }
                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                }
            })
    }
    private fun setUpData() {
        binding!!.tvDueDate.text = simp.format(Calendar.getInstance().time)
        binding!!.rvSelectedUser.layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        adapterUser = AdapterUser(arrayUserSelected, this, this)
        binding!!.rvSelectedUser.adapter = adapterUser
    }
    fun init() {
        getUsers()
        listeners()
        setUpData()
    }
    fun setUpUsers() {
        for (item in arrayUsers)
            arraySpinner.add(
                ItemSpinner(
                    item.id,
                    item.firstName + " " + item.lastName,
                    selected = false,
                    selectable = true
                )
            )
        binding!!.rvNames.layoutManager = LinearLayoutManager(this)
        adapter = AdapterText(arraySpinner, this, this)
        binding!!.rvNames.adapter = adapter
        binding!!.llLoading.hide()
    }
    private fun getUsers() {
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .getAllAppUsers(1)
            .enqueue(object : Callback<ApplicationUserList> {
                override fun onResponse(
                    call: Call<ApplicationUserList>,
                    response: Response<ApplicationUserList>
                ) {
                    arrayUsers.clear()
                    arrayUsers.addAll(response.body()!!)
                    setUpUsers()
                }
                override fun onFailure(call: Call<ApplicationUserList>, t: Throwable) {
                    binding!!.llLoading.hide()
                }
            })
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.btRemoveMore) {
            arraySpinner.find {
                it.id == arrayUserSelected[position].id
            }!!.selected = false
            arrayUserSelected.remove(arrayUserSelected[position])
            if (arrayUsers.size == 0)
                binding!!.rvSelectedUser.hide()
            else{
                adapterUser!!.notifyDataSetChanged()
                adapter!!.notifyDataSetChanged()
            }
        } else {
            if (!arraySpinner[position].selected!!) {
                binding!!.rvSelectedUser.show()
                binding!!.llNameSelect.hide()
                arrayUserSelected.add(arrayUsers[position])
                arraySpinner[position].selected = true
                adapter!!.notifyDataSetChanged()
                adapterUser!!.notifyDataSetChanged()
            }else{
                binding!!.rvSelectedUser.show()
                binding!!.llNameSelect.hide()
                arrayUserSelected.remove(arrayUsers[position])
                arraySpinner[position].selected = false
                adapter!!.notifyDataSetChanged()
                adapterUser!!.notifyDataSetChanged()
            }
        }
    }
}