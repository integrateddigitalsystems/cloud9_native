package com.ids.cloud9.utils

import com.ids.cloud9.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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
    ):Call<ProductList>

    @GET("Form/GetFormsByProductCategoryId")
    fun getReports(
        @Query("productCategoryId") prodCat : Int
    ):Call<ArrayList<Report>>


    @GET("Activity/GetActivitiesToUserMobile")
    fun getReccomendations(
        @Query("loggedUser") userId: Int
    ):Call<FilteredActivityList>

    @GET("Activity/GetActivitiesMobile")
    fun getReccomendationsFilter(
        @Query("entityId") entityId : Int ,
        @Query("statusId") statId : Int

    ):Call<FilteredActivityList>


    @GET("Products/GetAllActiveProductsMobile")
    fun getAllProducts():Call<ProductAllList>

    @GET("Lookups/GetLookupByParantCode")
    fun getUnits(
        @Query("code")code :String
    ):Call<UnitList>

    @GET("ApplicationUsers/GetAllApplicationUsers")
    fun getAllUsers(
        statusId:Int
    ):Call<UnitList>

    @POST("VisitProduct/CreateMobile")
    fun createMobile(
        @Body param : CreateProduct
    ):Call<ResponseMessage>

    @POST("VisitProduct/UpdateMobile")
    fun updateProduct(
        @Body param: CreateProduct
    ):Call<ResponseMessage>

    @POST("VisitProduct/Delete")
    fun deleteProduct(
        @Query("id") id  : Int
    ):Call<ResponseMessage>

    @POST("Activity/CreateActivityMobile")
    fun createActivity(
        @Body createAct : CreateActivity
    ):Call<ResponseMessage>

    @POST("Activity/DeleteActivity")
    fun deleteActivity(
        @Query("id") id : Int
    ):Call<ResponseMessage>

    @POST("Visit/UpdateVisit")
    fun updateVisit(
        @Body param: VisitListItem
    ): Call<ResponseMessage>

    @POST("Activity/UpdateActivityMobile")
    fun updateActivity(
        @Body param : FilteredActivityListItem
    ): Call<ResponseMessage>

    @GET("Attachments/GetAttachmentsMobile")
    fun getSignatures(
        @Query("entityTypeCode") entityType : String ,
        @Query("entityId") entity_id : Int

    ):Call<SignatureList>

    @POST("Attachments/DeleteAttachment")
    fun deleteMedia(
        @Query("id") id  : Int
    ):Call<ResponseMessage>

    @POST("Attachments/CreateAttachmentMobile")
    fun saveAttachment(
        @Body sigReq : ArrayList<SignatureRequest>
    ):Call<ResponseMessage>

    @POST("VisitLocation/UpdateVisitLocation")
    fun createVisitLocation(
        @Body reqVisit : VisitLocationRequest
    ):Call<ResponseMessage>

    @POST("Attachments/CreateAttachmentSignatureMobile")
    fun saveSignature(
        @Body sigReq : ArrayList<SignatureRequest>
    ):Call<ResponseMessage>

}