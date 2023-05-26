package com.ids.cloud9.utils

import com.ids.cloud9.model.*
import retrofit2.Call
import retrofit2.Response
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
        @Query(ApiParameters.STATUS_ID) statusId : Int
    ) : Call<ApplicationUserList>
    @GET("visit/GetVisitsListMobile")
    fun getVisits(
        @Query(ApiParameters.STATUS_ID) statusId : Int ,
        @Query(ApiParameters.USER_ID) userId : Int
    ):Call<VisitList>
    @GET("VisitProduct/GetByVisitId")
    fun getProducts(
        @Query(ApiParameters.VISIT_ID) visitId : Int
    ):Call<ProductList>
    @GET("Form/GetFormsByProductCategoryId")
    fun getReports(
        @Query(ApiParameters.PRODUCT_CATEGORY_ID) prodCat : Int
    ):Call<ArrayList<Report>>
    @GET("Activity/GetActivitiesToUserMobile")
    fun getReccomendations(
        @Query(ApiParameters.LOGGED_USER) userId: Int
    ):Call<FilteredActivityList>
    @GET("Activity/GetActivitiesMobile")
    fun getReccomendationsFilter(
        @Query(ApiParameters.ENTITY_ID) entityId : Int ,
        @Query(ApiParameters.STATUS_ID) statId : Int
    ):Call<FilteredActivityList>
    @GET("Products/GetAllActiveProductsMobile")
    fun getAllProducts():Call<ProductAllList>
    @GET("Lookups/GetLookupByParantCode")
    fun getUnits(
        @Query(ApiParameters.CODE)code :String
    ):Call<UnitList>
    @GET("ApplicationUsers/GetAllApplicationUsers")
    fun getAllUsers(
        @Query(ApiParameters.STATUS_ID) statId : Int
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
        @Query(ApiParameters.ID) id  : Int
    ):Call<ResponseMessage>
    @POST("Activity/CreateActivityMobile")
    fun createActivity(
        @Body createAct : CreateActivity
    ):Call<ResponseMessage>
    @POST("Activity/DeleteActivity")
    fun deleteActivity(
        @Query(ApiParameters.ID) id : Int
    ):Call<ResponseMessage>
    @POST("Visit/UpdateVisit")
    fun updateVisit(
        @Body param: Visit
    ): Call<ResponseMessage>
    @POST("Activity/UpdateActivityMobile")
    fun updateActivity(
        @Body param : FilteredActivityListItem
    ): Call<ResponseMessage>
    @GET("Attachments/GetAttachmentsMobile")
    fun getSignatures(
        @Query(ApiParameters.ENTITY_TYPE_CODE) entityType : String ,
        @Query(ApiParameters.ENTITY_ID) entity_id : Int
    ):Call<SignatureList>
    @POST("Attachments/DeleteAttachment")
    fun deleteMedia(
        @Query(ApiParameters.ID) id  : Int
    ):Call<ResponseMessage>
    @POST("Attachments/CreateAttachmentMobile")
    fun saveAttachment(
        @Body sigReq : ArrayList<SignatureRequest>
    ):Call<ResponseMessage>
    @POST("VisitLocation/CreateVisitLocation")
    fun createVisitLocation(
        @Body reqVisit : VisitLocationRequest
    ):Call<ResponseMessage>
    @POST("Attachments/CreateAttachmentSignatureMobile")
    fun saveSignature(
        @Body sigReq : ArrayList<SignatureRequest>
    ):Call<ResponseMessage>
    @POST("Tokens/SaveToken")
    fun saveToken(
        @Body tokenReq : TokenResource
    ):Call<ResponseMessage>

    @GET("Record/GetRecordsForProductVisit")
    fun getProductRecords(
        @Query(ApiParameters.VISIT_PRODUCT_ID) visitProductId : Int
    ):Call<RecordLists>

    @GET("Visit/GetVisitByID")
    fun getVisitById(
        @Query(ApiParameters.ID) id:Int
    ):Call<Visit>

    @GET("Companies/GetActiveCompanies")
    fun getCompanies():Call<ArrayList<Company>>

    @POST("User/LoginUser")
    fun loginUser(
        @Query(ApiParameters.EMAIL) email : String ,
        @Query(ApiParameters.PASSWORD) password : String
    ):Call<ResponseLogin>



}