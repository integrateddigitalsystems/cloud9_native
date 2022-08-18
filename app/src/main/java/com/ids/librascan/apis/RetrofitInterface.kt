package com.ids.librascan.apis


import com.ids.librascan.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*



interface RetrofitInterface {

    @POST("api/Auth/GoogleLogin")
    fun getLogin(
        @Query("idToken") idToken: String?,
    ): Call<ResponseLogin>

    @GET("api/WareHouses")
   suspend fun getWarehouses(
    ): Response<ResponseGetWareHouse>

}
