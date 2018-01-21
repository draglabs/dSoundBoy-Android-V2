/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2017. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Interfaces

import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by davrukin on 12/30/2017.
 * @author Daniel Avrukin
 */
interface ApiInterface {

    /*
    JAM FUNCTIONS
    */

    @POST("jam/new")
    fun newJam(
        @Header("user_id") user_id: String,
        @Body params: RequestBody
        /*@Query("name") name: String,
        @Query("location") location: String,
        @Query("lat") lat: Long,
        @Query("lng") lng: Long,
        @Query("notes") notes: String*/
    ): Call<ResponseModelKt.JamFunctions.NewJam>

    @POST("jam/update")
    fun updateJam(
        //@Header("id") id: String,
        @Body params: RequestBody
        /*@Query("name") name: String,
        @Query("id") id: String,
        @Query("location") location: String,
        @Query("notes") notes: String*/
    ): Call<ResponseModelKt.JamFunctions.UpdateJam>

    @POST("jam/join")
    fun joinJam(
        //@Header("id") id: String,
        @Body params: RequestBody
        /*@Query("pin") pin: String,
        @Query("user_id") userID: String*/
    ): Call<ResponseModelKt.JamFunctions.JoinJam>

    @Multipart
    @POST("jam/upload") // multi-part upload
    fun uploadJam(
        //@Header("id") id: String,
        //@Part params: MultipartBody.Part,
        @Part filePart: MultipartBody.Part,
        @Part user_id: MultipartBody.Part,
        @Part file_name: MultipartBody.Part,
        @Part location: MultipartBody.Part,
        @Part jam_id: MultipartBody.Part,
        @Part start_time: MultipartBody.Part,
        @Part end_time: MultipartBody.Part
        /*@Query("user_id") user_id: String,
        @Query("file_name") file_name: String,
        @Query("location") location: String,
        @Query("jam_id") jam_id: String,
        @Query("start_time") start_time: String,
        @Query("end_time") end_time: String*/
        ): Call<ResponseModelKt.JamFunctions.UploadJam>

    @GET("jam/details/{id}")
    fun getJamDetails(
        @Path("id") id: String,
        @Header("user_id") user_id: String
    ): Call<ResponseModelKt.JamFunctions.GetJamDetails>

    @GET("jam/recording/{id}")
    fun getRecordings(
        @Path("id") id: String
    ): Call<ResponseModelKt.JamFunctions.GetRecordings>

    /*
    USER FUNCTIONS
    */

    @POST("user/register")
    fun registerUser(
        //@Field("facebook_id") facebookID: String,
        //@Field("access_token") accessToken: String
        @Body params: RequestBody
        //@Query("facebook_id") facebookID: String,
        //@Query("access_token") accessToken: String
    ): Call<ResponseModelKt.UserFunctions.RegisterUser>

    @PUT("user/update")
    fun updateUser(
        @Header("email") email: String
    ): Call<ResponseModelKt.UserFunctions.UpdateUser>

    @GET("user/jam/active")
    fun getActiveJam(
        @Header("user_id") user_id: String
    ): Call<ResponseModelKt.UserFunctions.GetActiveJam>

    @GET("user/activity")
    fun getUserActivity(
        @Header("user_id") user_id: String
    ): Call<ResponseModelKt.UserFunctions.GetUserActivity>

    @GET("user/activity")
    fun getUserActivityArray(
        @Header("user_id") user_id: String
    ): Call<ResponseModelKt.UserFunctions.GetUserActivityArray>

    /*
    COMPRESSOR FUNCTIONS
    */

    @POST("jam/archive")
    fun compress(
        @Body params: RequestBody
    ): Call<ResponseModelKt.CompressorFunctions.Compressor>

    /*
    COMPANIONS
    */

    companion object {
        fun create(): ApiInterface {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.draglabs.com/api/v2.0/")
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }


}

class RetrofitClient {

    fun getClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://api.draglabs.com/api/v2.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

/*class ApiService {
    val service = ApiInterface
    var baseURL = "http://api.draglabs.com/api/v2.0/"

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()

        service = retrofit.create<ApiInterface.Companion>(ApiInterface::class.java)
    }

    fun loadUUID(): Observable<ResponseModelKt> {
        return service.
    }
}*/