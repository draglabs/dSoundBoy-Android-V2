/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2017. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Interfaces

import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by davrukin on 12/30/2017.
 */
interface ApiInterface {

    /*
    JAM FUNCTIONS
    */

    @POST("jam/new")
    fun newJam(
        @Header("user_id") user_id: String,
        @Query("name") name: String,
        @Query("location") location: String,
        @Query("lat") lat: Long,
        @Query("lng") lng: Long,
        @Query("notes") notes: String
    ): Observable<ResponseModelKt.JamFunctions.NewJam>

    @POST("jam/update")
    fun updateJam(
        //@Header("id") id: String,
        @Query("name") name: String,
        @Query("id") id: String,
        @Query("location") location: String,
        @Query("notes") notes: String
    ): Observable<ResponseModelKt.JamFunctions.UpdateJam>

    @POST("jam/join")
    fun joinJam(
        @Header("id") id: String,
        @Query("pin") pin: String,
        @Query("user_id") userID: String
    ): Observable<ResponseModelKt.JamFunctions.JoinJam>

    @POST("jam/upload") // multi-part upload
    fun uploadJam(
        @Header("id") id: String,
        @Query("user_id") user_id: String,
        @Query("file_name") file_name: String,
        @Query("location") location: String,
        @Query("jam_id") jam_id: String,
        @Query("start_time") start_time: String,
        @Query("end_time") end_time: String
    ): Observable<ResponseModelKt.JamFunctions.UploadJam>

    @GET("jam/details/{id}")
    fun getJamDetails(
        @Path("id") id: String,
        @Header("user_id") user_id: String
    ): Observable<ResponseModelKt.JamFunctions.GetJamDetails>

    @GET("jam/recording/{id}")
    fun getRecordings(
        @Path("id") id: String
    ): Observable<ResponseModelKt.JamFunctions.GetRecordings>

    /*
    USER FUNCTIONS
    */

    @POST("user/register")
    fun registerUser(
        @Query("facebook_id") facebookID: String,
        @Query("access_token") accessToken: String
    ): Observable<ResponseModelKt.UserFunctions.RegisterUser>

    @PUT("user/update")
    fun updateUser(
        @Header("email") email: String
    ): Observable<ResponseModelKt.UserFunctions.UpdateUser>

    @GET("user/jam/active")
    fun getActiveJam(
        @Header("user_id") user_id: String
    ): Observable<ResponseModelKt.UserFunctions.GetActiveJam>

    @GET("user/activity")
    fun getUserActivity(
        @Header("user_id") user_id: String
    ): Observable<ResponseModelKt.UserFunctions.GetUserActivity>

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