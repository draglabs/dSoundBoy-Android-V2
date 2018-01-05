/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import com.draglabs.dsoundboy.dsoundboy.Params.APIparamsKt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by davrukin on 12/30/2017.
 * @author Daniel Avrukin
 */
class APIutilsKt {

    fun performNewJam(context: Context, UUID: String, name: String, location: String, lat: Any, lng: Any, notes: String) {
        val call = APIparamsKt().callNewJam(UUID, name, location, lat, lng, notes)

        call.enqueue(object: Callback<ResponseModelKt.JamFunctions.NewJam> {
            override fun onResponse(call: Call<ResponseModelKt.JamFunctions.NewJam>, response: Response<ResponseModelKt.JamFunctions.NewJam>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val pin = result!!.pin // TODO: save picture from facebook in prefutils
                    LogUtils.debug("pin", pin)
                    PrefUtilsKt.Functions().storePIN(context, pin)
                } else {
                    LogUtils.debug("Failed Response", response.errorBody()!!.toString())
                    LogUtils.debug("Code", "" + response.code())
                    LogUtils.debug("Message", response.message())
                }
            }

            override fun onFailure(call: Call<ResponseModelKt.JamFunctions.NewJam>, t: Throwable) {
                logOnFailure(t)
            }
        })
    }

    fun performJoinJam(context: Context, pin: String, UUID: String) {
        val call = APIparamsKt().callJoinJam(pin, UUID)

        call.enqueue(object: Callback<ResponseModelKt.JamFunctions.JoinJam> {
            override fun onResponse(call: Call<ResponseModelKt.JamFunctions.JoinJam>, response: Response<ResponseModelKt.JamFunctions.JoinJam>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val jamID = result!!.id
                    val jamName = result.name
                    LogUtils.debug("jamID", jamID)
                    LogUtils.debug("jamName", jamName)
                    PrefUtilsKt.Functions().storeJamID(context, jamID)
                    PrefUtilsKt.Functions().storeJamName(context, jamName)
                } else {
                    LogUtils.debug("Failed Response", response.errorBody()!!.toString())
                    LogUtils.debug("Code", "" + response.code())
                    LogUtils.debug("Message", response.message())
                }
            }

            override fun onFailure(call: Call<ResponseModelKt.JamFunctions.JoinJam>, t: Throwable) {
                logOnFailure(t)
            }
        })
    }

    fun performRegisterUser(context: Context) {
        val call = APIparamsKt().callRegisterUser()

        call.enqueue(object : Callback<ResponseModelKt.UserFunctions.RegisterUser> {
            override fun onResponse(call: Call<ResponseModelKt.UserFunctions.RegisterUser>, response: Response<ResponseModelKt.UserFunctions.RegisterUser>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val id = result!!.id
                    LogUtils.debug("id", id)
                    PrefUtilsKt.Functions().storeUUID(context, id)
                } else {
                    LogUtils.debug("Failed Response", response.errorBody()!!.toString())
                    LogUtils.debug("Code", "" + response.code())
                    LogUtils.debug("Message", response.message())
                }
            }
            override fun onFailure(call: Call<ResponseModelKt.UserFunctions.RegisterUser>, t: Throwable) {
                logOnFailure(t)
                LogUtils.debug("onFailure Failed", "Canceled" + call.isCanceled.toString())
                LogUtils.debug("onFailure Failed", "Executed" + call.isExecuted.toString())
            }
        })
    }

    private fun logOnFailure(t: Throwable) {
        LogUtils.debug("onFailure Failed", t.message.toString())
        LogUtils.debug("onFailure Failed", t.cause.toString())
        LogUtils.debug("onFailure Failed", t.printStackTrace().toString())
    }
}