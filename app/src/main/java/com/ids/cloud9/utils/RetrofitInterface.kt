package com.ids.cloud9.utils

import com.ids.cloud9.model.ApplicationUserList
import com.ids.cloud9.model.RequestLogin
import com.ids.cloud9.model.ResponseLogin
import com.ids.cloud9.model.VisitList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitInterface {

    @POST("Auth/SignIn")
    fun logIn(
        @Body param: RequestLogin
    ): Call<ResponseLogin>

    @GET("ApplicationUsers/GetAllApplicationUsers")
    fun getAllAppUsers(
        @Query("statusId") statusId : Int
    ) : Call<ApplicationUserList>

    @GET("visit/GetVisitsListMobile")
    fun getVisits(
        @Query("statusId") statusId : Int ,
        @Query("userId") userId : Int
    ):Call<VisitList>

}