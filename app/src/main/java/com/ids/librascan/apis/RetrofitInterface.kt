package com.ids.librascan.apis


import com.ids.librascan.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*


interface RetrofitInterface {

    @POST("api/Auth/GoogleLogin")
    fun getLogin(
        @Query("idToken") idToken: String?,
    ): Call<ResponseLogin>

    @GET("/api/WareHouses")
    fun getWarehouse(
    ): Call<ResponseGetWareHouse>

    @GET("api/WareHouses")
   suspend fun getWarehouses(
    ): Response<ResponseGetWareHouse>

    @GET("/api/v1/companies")
    suspend fun getCompanies(
    ): Response<ResponseGetCompanies>

    @GET("/api/v1/persons")
    suspend fun getPerson(
    ): Response<ResponseGetPerson>

}
