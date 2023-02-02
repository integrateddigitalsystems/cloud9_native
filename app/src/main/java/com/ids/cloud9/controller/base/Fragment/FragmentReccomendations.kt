package com.ids.cloud9.controller.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.activities.ActivityAddReccomendations
import com.ids.cloud9.controller.adapters.AdapterReccomendations
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.LayoutProductsBinding
import com.ids.cloud9.databinding.LayoutReccomendationsBinding
import com.ids.cloud9.model.ActivitiesList
import com.ids.cloud9.model.ActivitiesListItem
import com.ids.cloud9.utils.RetrofitClientAuth
import com.ids.cloud9.utils.RetrofitInterface
import com.ids.cloud9.utils.hide
import com.ids.cloud9.utils.show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class FragmentReccomendations : Fragment() , RVOnItemClickListener {

    var binding : LayoutReccomendationsBinding?=null

    var arrayReccomend: ArrayList<ActivitiesListItem> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LayoutReccomendationsBinding.inflate(inflater, container, false)
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

    fun listeners(){
        binding!!.btAddReccomend.setOnClickListener{
            MyApplication.selectedReccomend = null
            startActivity(
                Intent(
                    requireActivity(),
                    ActivityAddReccomendations::class.java
                )
            )
        }
    }


    fun init(){
        getReccomendations()
    }

    fun setDataReccomend() {
        binding!!.rvReccomendations.layoutManager = LinearLayoutManager(requireContext())
        binding!!.rvReccomendations.adapter =
            AdapterReccomendations(arrayReccomend, requireActivity(), this)
        binding!!.llLoading.hide()
    }

    fun getReccomendations() {
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getReccomendations(
            MyApplication.userItem!!.applicationUserId!!.toInt()
        ).enqueue(object : Callback<ActivitiesList> {
            override fun onResponse(
                call: Call<ActivitiesList>,
                response: Response<ActivitiesList>
            ) {
                arrayReccomend.clear()
                arrayReccomend.addAll(response.body()!!
                    .filter {
                        if(!it.entity.isNullOrEmpty())
                            it.entity.equals(MyApplication.selectedVisit!!.title)
                        else
                            false
                    })
                setDataReccomend()

            }

            override fun onFailure(call: Call<ActivitiesList>, t: Throwable) {
                binding!!.llLoading.hide()
            }

        })
    }

    override fun onItemClicked(view: View, position: Int) {
        MyApplication.selectedReccomend = arrayReccomend.get(position)
        startActivity(
            Intent(
                requireActivity(),
                ActivityAddReccomendations::class.java
            )
        )
    }
}