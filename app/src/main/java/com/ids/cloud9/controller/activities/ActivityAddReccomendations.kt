package com.ids.cloud9.controller.activities

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.gson.Gson
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.AdapterText
import com.ids.cloud9.controller.adapters.AdapterUser
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ActivityAddProductBinding
import com.ids.cloud9.databinding.ActivityAddReccomendBinding
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivityAddReccomendations : AppCompatActivity(), RVOnItemClickListener {

    var binding: ActivityAddReccomendBinding? = null
    var adapter: AdapterText? = null
    var simp = SimpleDateFormat("MM/dd/yyyy")
    var simpOrg = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    var adapterUser: AdapterUser? = null
    var arraySpinner: ArrayList<ItemSpinner> = arrayListOf()
    var arrayUsers: ArrayList<ApplicationUserListItem> = arrayListOf()
    var arrayUserSelected: ArrayList<ApplicationUserListItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.ids.cloud9.databinding.ActivityAddReccomendBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()


    }


    fun popupDate() {
        var myCalendar: Calendar = Calendar.getInstance()
        var DatePicker =
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
        binding!!.btBack.setOnClickListener {
            super.onBackPressed()
        }
        binding!!.llAssignTo.setOnClickListener {
            binding!!.llNameSelect.show()
        }
        binding!!.ivSelectUser.setOnClickListener {
            binding!!.llNameSelect.show()
        }
        binding!!.tvDueDate.setOnClickListener {
            popupDate()
        }
        binding!!.btSave.setOnClickListener {
            if (!binding!!.etSubject.text.toString()
                    .isNullOrEmpty() && !binding!!.tvDueDate.text.toString()
                    .isNullOrEmpty() && !binding!!.etDesc.text.toString()
                    .isNullOrEmpty() && arrayUserSelected.size > 0
            ) {
                if(binding!!.etSubject.text!!.length >=5)
                    createReccomend()
                else{
                    AppHelper.createDialogPositive(this, getString(R.string.less_than_5))
                }

            } else if (!binding!!.etSubject.text.toString()
                    .isNullOrEmpty() && !binding!!.tvDueDate.text.toString()
                    .isNullOrEmpty() && !binding!!.etDesc.text.toString()
                    .isNullOrEmpty() && MyApplication.selectedReccomend != null
            ) {

                if(binding!!.etSubject.text!!.length >=5)
                    updateReccomend()
                else{
                    AppHelper.createDialogPositive(this, getString(R.string.less_than_5))
                }
            } else {
                AppHelper.createDialogPositive(this, getString(R.string.fill_details))
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
                simp.format(simpOrg.parse(MyApplication.selectedReccomend!!.dueDate))
        }
    }

    fun updateReccomend() {
        var id = 0
        binding!!.llLoading.show()

        MyApplication.selectedReccomend!!.dueDate = simpOrg.format(simp.parse(binding!!.tvDueDate.text.toString()))
        MyApplication.selectedReccomend!!.subject = binding!!.etSubject.text.toString()
        MyApplication.selectedReccomend!!.description = binding!!.etDesc.text.toString()

        var upAct = UpdateActivity(
            MyApplication.selectedReccomend!!.assignedTo!!,
            MyApplication.selectedReccomend!!.assignedToId,
            simp.format(simpOrg.parse(MyApplication.selectedReccomend!!.creationDate)),
            MyApplication.selectedReccomend!!.description,
            simp.format(simpOrg.parse(MyApplication.selectedReccomend!!.dueDate)),
            simp.format(simpOrg.parse(MyApplication.selectedReccomend!!.dueDate)),
            MyApplication.selectedReccomend!!.entity,
            MyApplication.selectedVisit!!.id!!,
            MyApplication.selectedReccomend!!.entityType,
            MyApplication.selectedVisit!!.typeId!!,
            MyApplication.selectedReccomend!!.id,
            MyApplication.selectedReccomend!!.owner,
            arraySpinner.find { it.name.equals(MyApplication.selectedReccomend!!.owner) }!!.id!!,
            MyApplication.selectedReccomend!!.reasonId,
            simp.format(simpOrg.parse(MyApplication.selectedReccomend!!.dueDate)),
            MyApplication.selectedReccomend!!.status,
            MyApplication.selectedVisit!!.statusId!!,
            MyApplication.selectedReccomend!!.reason,
            MyApplication.selectedReccomend!!.subject,
            if (MyApplication.selectedVisit!!.type != null)
                MyApplication.selectedVisit!!.type!!
            else
                ""

        )


        Log.wtf("TAG_CREATE", Gson().toJson(MyApplication.selectedReccomend))
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java)
            .updateActivity(
                upAct
            ).enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.body()!!.success.equals("true")) {
                        AppHelper.createDialogAgain(
                            this@ActivityAddReccomendations,
                            response.body()!!.message!!
                        ) {
                            finish()
                        }
                    } else {
                        AppHelper.createDialogPositive(
                            this@ActivityAddReccomendations,
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

    fun createReccomend() {
        var id = 0
        binding!!.llLoading.show()
        var arrId: kotlin.collections.ArrayList<Int> = arrayListOf()

        if (arrayUserSelected.size == 1)
            id = arrayUserSelected.get(0).id
        else {
            for (item in arrayUserSelected)
                arrId.add(item.id)
        }
        var createActivity: CreateActivity = CreateActivity(
            if (arrId.size > 0) null else id,
            binding!!.etDesc.text.toString(),
            MyApplication.selectedVisit!!.id,
            arrId,
            MyApplication.userItem!!.applicationUserId,
            binding!!.etSubject.text.toString(),
            binding!!.tvDueDate.text.toString()

        )

        Log.wtf("TAG_CREATE", Gson().toJson(createActivity))
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java)
            .createActivity(
                createActivity
            ).enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.body()!!.success.equals("true")) {
                        AppHelper.createDialogAgain(
                            this@ActivityAddReccomendations,
                            response.body()!!.message!!
                        ) {
                            finish()
                        }
                    } else {
                        binding!!.llLoading.hide()
                        AppHelper.createDialogPositive(
                            this@ActivityAddReccomendations,
                            response.body()!!.message!!
                        )
                    }

                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                }

            })
    }

    fun setUpData() {
        binding!!.rvSelectedUser.layoutManager =
            GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
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
                    false,
                    true
                )
            )
        binding!!.rvNames.layoutManager = LinearLayoutManager(this)
        adapter = AdapterText(arraySpinner, this, this)
        binding!!.rvNames.adapter = adapter
        binding!!.llLoading.hide()
    }

    fun getUsers() {
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java)
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

    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.btRemoveMore) {

            arraySpinner.find {
                it.id == arrayUsers.get(position).id
            }!!.selected = false

            arrayUserSelected.removeAt(position)
            if (arrayUsers.size == 0)
                binding!!.rvSelectedUser.hide()
            else
                adapterUser!!.notifyDataSetChanged()
        } else {
            if (!arraySpinner.get(position).selected!!) {
                binding!!.rvSelectedUser.show()
                binding!!.llNameSelect.hide()
                arrayUserSelected.add(arrayUsers.get(position))
                arraySpinner.get(position).selected = true
                adapter!!.notifyDataSetChanged()
                adapterUser!!.notifyDataSetChanged()
            }
        }

    }

}