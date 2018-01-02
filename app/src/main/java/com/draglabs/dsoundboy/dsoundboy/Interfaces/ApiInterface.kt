/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2017. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Interfaces

import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by davru on 12/30/2017.
 */
interface ApiInterface {

    @POST("jam/new")
    fun newJam(
        @Query("name") name: String,
        @Query("location") location: String,
        @Query("lat") lat: Long,
        @Query("lng") lng: Long,
        @Query("notes") notes: String
    ): Observable<ResponseModelKt.JamFunctions.NewJam>

    @POST("jam/join")
    fun joinJam(
        @Query("pin") pin: String,
        @Query("user_id") userID: String
    ): Observable<ResponseModelKt.JamFunctions.JoinJam>

    @POST("user/register")
    fun registerUser(
        @Query("facebook_id") facebookID: String,
        @Query("access_token") accessToken: String
    ): Observable<ResponseModelKt.UserFunctions.RegisterUser>

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