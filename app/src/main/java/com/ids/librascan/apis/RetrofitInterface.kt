package com.ids.librascan.apis


import com.ids.librascan.model.ResponseGetWareHouse
import com.ids.librascan.model.ResponseLogin
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface RetrofitInterface {

    @POST("api/Auth/GoogleLogin")
    fun getLogin(
        @Query("idToken") idToken: String?,
    ): Call<ResponseLogin>

    @GET("/api/WareHouses")
    fun getWarehouse(
    ): Call<ResponseGetWareHouse>

    @GET("/api/WareHouses")
   suspend fun getWarehouses(
    ): Response<ResponseGetWareHouse>

}
