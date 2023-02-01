package com.ids.cloud9.controller.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.ids.cloud9.R
import com.ids.cloud9.controller.adapters.AdapterText
import com.ids.cloud9.controller.adapters.AdapterUser
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.ActivityAddProductBinding
import com.ids.cloud9.databinding.ActivityAddReccomendBinding
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding
import com.ids.cloud9.model.ApplicationUserList
import com.ids.cloud9.model.ApplicationUserListItem
import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.utils.RetrofitClientAuth
import com.ids.cloud9.utils.RetrofitInterface
import com.ids.cloud9.utils.hide
import com.ids.cloud9.utils.show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityAddReccomendations : AppCompatActivity(),RVOnItemClickListener {

    var binding: ActivityAddReccomendBinding? = null
    var adapter : AdapterText ?=null
    var adapterUser : AdapterUser ?=null
    var arraySpinner : ArrayList<ItemSpinner> = arrayListOf()
    var arrayUsers: ArrayList<ApplicationUserListItem> = arrayListOf()
    var arrayUserSelected : ArrayList<ApplicationUserListItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReccomendBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()


    }

    fun listeners(){
        binding!!.llAssignTo.setOnClickListener {
            binding!!.llNameSelect.show()
        }
        binding!!.ivSelectUser.setOnClickListener {
            binding!!.llNameSelect.show()
        }
        binding!!.tvDueDate.setOnClickListener {

        }
    }

    fun setUpData(){
        binding!!.rvSelectedUser.layoutManager=GridLayoutManager(this,2,LinearLayoutManager.VERTICAL,false)
        adapterUser = AdapterUser(arrayUserSelected,this,this)
        binding!!.rvSelectedUser.adapter = adapterUser
    }

    fun init() {
        getUsers()
        listeners()
        setUpData()
    }
    fun setUpUsers(){
        for(item in arrayUsers)
            arraySpinner.add(ItemSpinner(item.id,item.firstName+" "+item.lastName,false,true))
        binding!!.rvNames.layoutManager=LinearLayoutManager(this)
        adapter = AdapterText(arraySpinner,this,this)
        binding!!.rvNames.adapter = adapter
    }
    fun getUsers() {
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

                }

            })
    }

    override fun onItemClicked(view: View, position: Int) {
        if(view.id == R.id.btRemoveMore) {
            arrayUsers.removeAt(position)
            if(arrayUsers.size==0)
                binding!!.rvSelectedUser.hide()
        } else
        {
            binding!!.rvSelectedUser.show()
            binding!!.llNameSelect.hide()
            arrayUserSelected.add(arrayUsers.get(position))
            adapterUser!!.notifyDataSetChanged()
        }

    }

}