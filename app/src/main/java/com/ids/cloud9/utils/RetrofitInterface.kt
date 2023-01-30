package com.ids.cloud9.utils

import com.ids.cloud9.model.*
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


    @GET("VisitProduct/GetByVisitId")
    fun getProducts(
        @Query("VisitId") visitId : Int
    ):Call<Products>

    @GET("Form/GetFormsByProductCategoryId")
    fun getReports(
        @Query("productCategoryId") prodCat : Int
    ):Call<ArrayList<Report>>


    @GET("Activity/GetActivitiesToUserMobile")
    fun getReccomendations(
        @Query("loggedUser") userId: Int
    ):Call<ActivitiesList>


    @GET("Products/GetAllActiveProductsMobile")
    fun getAllProducts():Call<ProductAllList>

    @GET("Lookups/GetLookupByParantCode")
    fun getUnits(
        code:String
    ):Call<UnitList>

    @GET("ApplicationUsers/GetAllApplicationUsers")
    fun getAllUsers(
        statusId:Int
    ):Call<UnitList>

}