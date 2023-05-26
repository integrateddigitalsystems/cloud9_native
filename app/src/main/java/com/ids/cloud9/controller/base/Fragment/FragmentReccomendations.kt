package com.ids.cloud9.controller.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.activities.ActivityAddReccomendations
import com.ids.cloud9.controller.adapters.AdapterFilteredReccomendations
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.LayoutReccomendationsBinding
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
    }
    fun listeners() {
        if (MyApplication.selectedVisit!!.reasonId!= AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
           binding!!.btAddReccomend.isEnabled = false
            binding!!.llButton.setBackgroundResource(R.color.disabled_primary)
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
        binding!!.srReccomendations.setOnRefreshListener{
            getReccomendations()
        }
    }
    fun init() {
        listeners()
    }

    fun setDataReccomend() {
        if(arrayReccomend.size > 0 ) {
            binding!!.rvReccomendations.show()
            binding!!.tvNotasks.hide()
        }else{
            binding!!.rvReccomendations.hide()
            binding!!.tvNotasks.show()
        }
        binding!!.rvReccomendations.layoutManager = LinearLayoutManager(requireContext())
        binding!!.rvReccomendations.adapter =
            AdapterFilteredReccomendations(arrayReccomend, requireActivity(), this)

        binding!!.llLoading.hide()
        binding!!.srReccomendations.isRefreshing = false
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
        if (MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_ARRIVED)) {
            MyApplication.selectedReccomend = arrayReccomend.get(position)
            startActivity(
                Intent(
                    requireActivity(),
                    ActivityAddReccomendations::class.java
                )
            )
        }else{
            createDialog( getString(R.string.visit_arrived_completed))
        }
    }
}