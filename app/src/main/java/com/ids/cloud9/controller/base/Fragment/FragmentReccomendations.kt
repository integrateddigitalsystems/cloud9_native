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
import com.ids.cloud9.controller.adapters.AdapterFilteredReccomendations
import com.ids.cloud9.controller.adapters.AdapterReccomendations
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.LayoutProductsBinding
import com.ids.cloud9.databinding.LayoutReccomendationsBinding
import com.ids.cloud9.model.ActivitiesList
import com.ids.cloud9.model.ActivitiesListItem
import com.ids.cloud9.model.FilteredActivityList
import com.ids.cloud9.model.FilteredActivityListItem
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class FragmentReccomendations : Fragment(), RVOnItemClickListener {

    var binding: LayoutReccomendationsBinding? = null

    var arrayReccomend: ArrayList<FilteredActivityListItem> = arrayListOf()
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

    fun listeners() {
        if (MyApplication.selectedVisit!!.reasonId == AppConstants.PENDING_REASON_ID || MyApplication.selectedVisit!!.reasonId == AppConstants.COMPLETED_REASON_ID || MyApplication.selectedVisit!!.reasonId == AppConstants.ON_THE_WAY_REASON_ID) {
            binding!!.btAddReccomend.hide()
        }
        binding!!.btAddReccomend.setOnClickListener {
            MyApplication.selectedReccomend = null
            startActivity(
                Intent(
                    requireActivity(),
                    ActivityAddReccomendations::class.java
                )
            )
        }
    }


    fun init() {
        getReccomendations()
    }

    fun setDataReccomend() {
        binding!!.rvReccomendations.layoutManager = LinearLayoutManager(requireContext())
        binding!!.rvReccomendations.adapter =
            AdapterFilteredReccomendations(arrayReccomend, requireActivity(), this)
        binding!!.llLoading.hide()
    }

    override fun onResume() {
        super.onResume()
        getReccomendations()
    }

    fun getReccomendations() {
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getReccomendationsFilter(
            MyApplication.selectedVisit!!.id!!,
            -1
        ).enqueue(object : Callback<FilteredActivityList> {
            override fun onResponse(
                call: Call<FilteredActivityList>,
                response: Response<FilteredActivityList>
            ) {
                arrayReccomend.clear()
                arrayReccomend.addAll(response.body()!!)
                setDataReccomend()

            }

            override fun onFailure(call: Call<FilteredActivityList>, t: Throwable) {
                binding!!.llLoading.hide()
            }

        })
    }

    override fun onItemClicked(view: View, position: Int) {
        if (MyApplication.selectedVisit!!.reasonId != AppConstants.PENDING_REASON_ID || MyApplication.selectedVisit!!.reasonId != AppConstants.COMPLETED_REASON_ID || MyApplication.selectedVisit!!.reasonId != AppConstants.ON_THE_WAY_REASON_ID) {

            MyApplication.selectedReccomend = arrayReccomend.get(position)
            startActivity(
                Intent(
                    requireActivity(),
                    ActivityAddReccomendations::class.java
                )
            )
        }
    }
}