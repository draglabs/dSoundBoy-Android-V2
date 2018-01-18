/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import com.draglabs.dsoundboy.dsoundboy.Params.APIparamsKt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

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

    fun performGetJamDetails(context: Context, jamID: String) {
        val call = APIparamsKt().callGetJamDetails(context, jamID)

        call.enqueue(object: Callback<ResponseModelKt.JamFunctions.GetJamDetails> {
            override fun onResponse(call: Call<ResponseModelKt.JamFunctions.GetJamDetails>, response: Response<ResponseModelKt.JamFunctions.GetJamDetails>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val jam = result!!.jam
                    val link = jam.link
                    PrefUtilsKt.Functions().storeLink(context, link)
                    LogUtils.debug("Gotten Link", link)
                } else {
                    LogUtils.debug("Failed Response", response.errorBody()!!.toString())
                    LogUtils.debug("Code", "" + response.code())
                    LogUtils.debug("Message", response.message())
                    LogUtils.debug("Headers", response.headers().toString())                }
            }

            override fun onFailure(call: Call<ResponseModelKt.JamFunctions.GetJamDetails>, t: Throwable) {
                logOnFailure(t)
                LogUtils.debug("onFailure Call", call.toString())
                LogUtils.debug("onFailure Failed", "Canceled: " + call.isCanceled.toString())
                LogUtils.debug("onFailure Failed", "Executed: " + call.isExecuted.toString())                  }
        })
    }

    fun performUploadJam(context: Context, filePath: String, userID: String, fileName: String, location: String, jamID: String, startTime: String, endTime: String) {
        val call = APIparamsKt().callUploadJam(filePath, userID, fileName, location, jamID, startTime, endTime)

        call.enqueue(object: Callback<ResponseModelKt.JamFunctions.UploadJam> {
            override fun onResponse(call: Call<ResponseModelKt.JamFunctions.UploadJam>, response: Response<ResponseModelKt.JamFunctions.UploadJam>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    LogUtils.debug("Upload Response Body", result.toString())
                    LogUtils.debug("Upload Response Message", "Code: ${response.code()}; Message: ${response.message()}")
                } else {
                    LogUtils.debug("Failed Response", response.errorBody()!!.toString())
                    LogUtils.debug("Code", "" + response.code())
                    LogUtils.debug("Message", response.message())
                    LogUtils.debug("Headers", response.headers().toString())
                }
            }

            override fun onFailure(call: Call<ResponseModelKt.JamFunctions.UploadJam>, t: Throwable) {
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

    fun performGetUserActivity(context: Context) {
        val call = APIparamsKt().callGetUserActivity(context)

        call.enqueue(object: Callback<ResponseModelKt.UserFunctions.GetUserActivity> {
            override fun onResponse(call: Call<ResponseModelKt.UserFunctions.GetUserActivity>, response: Response<ResponseModelKt.UserFunctions.GetUserActivity>) {
                LogUtils.debug("response from gua", response.toString())
                if (response.isSuccessful) {
                    val result = response.body()
                    val jams = ArrayList<JamViewModel>()
                    if (result != null) {
                        LogUtils.debug("Result of GUA", result.toString())
                        /*val length = result.jam.length()
                        var i = 0
                        while (i < length) {
                            val element = result.jam[i] as JSONObject
                            val id = element.get("id") as String
                            val name = element.get("name") as String
                            val location = element.get("location") as String
                            val link = element.get("link") as String
                            val jamViewModel = JamViewModel(i, id, name, location, link)
                            jams.add(jamViewModel)
                            i++
                        }*/
                        /*val jamsArray = result.jams
                        LogUtils.debug("Jams Array", jamsArray.toString())
                        for (element in jamsArray) {
                            val id = element.id
                            val name = element.name
                            val location = element.location
                            val link = element.link
                            val jamViewModel = JamViewModel(id, name, location, link)
                            jams.add(jamViewModel)
                        }*/
                        //jams[0] = JamViewModel(0, result.id, result.name, result.location, result.link)
                        ////result.jam.mapIndexedTo(jams) { index, jam -> JamViewModel(index, jam.id, jam.name, jam.location, jam.link) }
                        //val arrayList = FileUtils().serializeArrayList(jams)
                        //LogUtils.debug("Jams", arrayList)
                        LogUtils.debug("Jams", jams.toString())
                        //PrefUtilsKt.Functions().storeJams(context, arrayList)
                        RealmUtils().storeJams(jams)
                    } else {
                        // TODO: no jams, account for something here
                        LogUtils.debug("Result", "Result is null")
                    }
                } else {
                    LogUtils.debug("Failed Response", response.errorBody()!!.toString())
                    LogUtils.debug("Code", "" + response.code())
                    LogUtils.debug("Message", response.message())
                }
            }
            override fun onFailure(call: Call<ResponseModelKt.UserFunctions.GetUserActivity>, t: Throwable) {
                logOnFailure(t)
                LogUtils.debug("onFailure Failed", "Canceled: " + call.isCanceled.toString())
                LogUtils.debug("onFailure Failed", "Executed: " + call.isExecuted.toString())            }
        })
    }

    fun performGetUserActivityArray(context: Context) {
        val call = APIparamsKt().callGetUserActivityArray(context)

        call.enqueue(object: Callback<ResponseModelKt.UserFunctions.GetUserActivityArray> {
            override fun onResponse(call: Call<ResponseModelKt.UserFunctions.GetUserActivityArray>, response: Response<ResponseModelKt.UserFunctions.GetUserActivityArray>) {
                LogUtils.debug("response from gua", response.toString())
                if (response.isSuccessful) {
                    val result = response.body()
                    val jams = ArrayList<JamViewModel>()
                    if (result != null) {
                        LogUtils.debug("Result of GUA", result.toString())

                        val jamsArray = result.jams
                        LogUtils.debug("Jams Array", jamsArray.toString())
                        for (element in jamsArray) {
                            val id = element.id
                            val name = element.name
                            val location = element.location
                            val link = element.link
                            val jamViewModel = JamViewModel(id, name, location, link)
                            jams.add(jamViewModel)
                        }
                        LogUtils.debug("Jams", jams.toString())
                        LogUtils.debug("Result", result.toString())
                        //PrefUtilsKt.Functions().storeJams(context, arrayList)
                        RealmUtils().storeJams(jams)
                    } else {
                        // TODO: no jams, account for something here
                        LogUtils.debug("Result", "Result is null")
                    }
                } else {
                    LogUtils.debug("Failed Response", response.errorBody()!!.toString())
                    LogUtils.debug("Code", "" + response.code())
                    LogUtils.debug("Message", response.message())
                }
            }
            override fun onFailure(call: Call<ResponseModelKt.UserFunctions.GetUserActivityArray>, t: Throwable) {
                logOnFailure(t)
                LogUtils.debug("onFailure Call", call.toString())
                LogUtils.debug("onFailure Failed", "Canceled: " + call.isCanceled.toString())
                LogUtils.debug("onFailure Failed", "Executed: " + call.isExecuted.toString())            }
        })
    }

    fun performCompressor(context: Context, jamID: String) {
        val call = APIparamsKt().callCompressor(context, jamID)

        call.enqueue(object: Callback<ResponseModelKt.CompressorFunctions.Compressor> {
            override fun onResponse(call: Call<ResponseModelKt.CompressorFunctions.Compressor>, response: Response<ResponseModelKt.CompressorFunctions.Compressor>) {
                if (response.isSuccessful) {
                    val result = response.body()

                    LogUtils.debug("Upload Response Body", result.toString())
                    LogUtils.debug("Upload Response Message", "Code: ${response.code()}; Message: ${response.message()}")
                } else {
                    LogUtils.debug("Failed Response", response.errorBody()!!.toString())
                    LogUtils.debug("Code", "" + response.code())
                    LogUtils.debug("Message", response.message())
                    LogUtils.debug("Headers", response.headers().toString())
                }
            }

            override fun onFailure(call: Call<ResponseModelKt.CompressorFunctions.Compressor>, t: Throwable) {
                logOnFailure(t)
            }
        })
    }

    private fun logOnFailure(t: Throwable) {
        LogUtils.debug("onFailure Failed Message", t.message.toString())
        LogUtils.debug("onFailure Failed Cause", t.cause.toString())
        LogUtils.debug("onFailure Failed StackTrace", t.printStackTrace().toString())
    }
}