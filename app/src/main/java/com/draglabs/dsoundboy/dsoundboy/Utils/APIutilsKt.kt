/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import com.draglabs.dsoundboy.dsoundboy.Params.APIparamsKt
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.HeaderElement
import cz.msebera.android.httpclient.ParseException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import android.app.Activity
import com.loopj.android.http.RequestHandle
import org.json.JSONArray


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
                    val jamID = result.id
                    val jamName = result.name
                    LogUtils.debug("pin", pin)
                    PrefUtilsKt.Functions().storePIN(context, pin)
                    PrefUtilsKt.Functions().storeJamID(context, jamID)
                    PrefUtilsKt.Functions().storeJamName(context, jamName)

                    val code = response.code()
                    val message = response.message()
                    LogUtils.debug("NewJam Response Body", result.toString())
                    LogUtils.debug("NewJam Response Message", "Code: $code; Message: $message")
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
                    LogUtils.debug("Response from JamDetails", result.toString())
                    val link = result!!.link
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

    fun getJamDetails(activity: Activity, context: Context) {
        val UUID = PrefUtilsKt.Functions().retrieveUUID(context)
        val jamID = PrefUtilsKt.Functions().retrieveJamID(context)

        val newGET = "api.draglabs.com/api/v2.0/jam/details/$jamID"

        val requestParams = RequestParams()
        val headers = userIDHeaders(UUID)

        get(context, newGET, headers, requestParams, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                logSuccessResponse(statusCode, headers!!, response!!)
                try {
                    //val jamDetails = JsonUtils.INSTANCE.getJsonObject(StringsModel.GET_JAM_DETAILS, response, StringsModel.jsonTypes.DATA.type())

                    Log.v("Jam Details: ", response.toString())
                    //PrefUtils(activity).saveJamDetails(jamDetails) // TODO: enable later
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, response: JSONObject?) {
                logFailureResponse(statusCode, headers!!, throwable, response!!)
            }
        })
    }

    fun userIDHeaders(UUID: String): Array<Header> {
        return arrayOf(object : Header {
            override fun getName(): String {
                return "user_id"
            }

            override fun getValue(): String {
                return UUID
            }

            @Throws(ParseException::class)
            override fun getElements(): Array<HeaderElement?> {
                return arrayOfNulls(0)
            }
        })
    }

    private operator fun get(context: Context, url: String, headers: Array<Header>, requestParams: RequestParams, asyncHttpResponseHandler: AsyncHttpResponseHandler): String {
        val asyncHttpClient = AsyncHttpClient(true, 80, 433)
        val requestHandle = asyncHttpClient.get(context, url, headers, requestParams, asyncHttpResponseHandler)
        return if (requestHandle.isFinished) {
            "Done with GET. Tag: " + requestHandle.tag
        } else {
            "GET Failed. Tag: " + requestHandle.tag
        }
    }

    fun performUploadJam(context: Context, filePath: String, userID: String, fileName: String, location: String, jamID: String, startTime: String, endTime: String) {
        val call = APIparamsKt().callUploadJam(filePath, userID, fileName, location, jamID, startTime, endTime)

        call.enqueue(object: Callback<ResponseModelKt.JamFunctions.UploadJam> {
            override fun onResponse(call: Call<ResponseModelKt.JamFunctions.UploadJam>, response: Response<ResponseModelKt.JamFunctions.UploadJam>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val code = response.code()
                    val message = response.message()
                    LogUtils.debug("Upload Response Body", result.toString())
                    LogUtils.debug("Upload Response Message", "Code: $code; Message: $message")
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

    private fun standardHeader(UUID: String): Array<Header> {
        return arrayOf(object : Header {
            override fun getName(): String {
                return "Content-Type"
            }
            override fun getValue(): String {
                return "application/json"
            }

            @Throws(ParseException::class)
            override fun getElements(): Array<HeaderElement?> {
                return arrayOfNulls<HeaderElement>(0)
            }
        }, object : Header {
            override fun getName(): String {
                return "user_id"
            }

            override fun getValue(): String {
                return UUID
            }

            @Throws(ParseException::class)
            override fun getElements(): Array<HeaderElement?> {
                return arrayOfNulls<HeaderElement>(1)
            }
        })
    }

    private fun jamRecordingUploadParams(UUID: String,
                                   jamID: String,
                                   path: String,
                                   location: String,
                                   startTime: String,
                                   endTime: String): RequestParams {
        val requestParams = RequestParams()
        requestParams.put("user_id", UUID)
        requestParams.put("jam_id", jamID)
        requestParams.put("location", location)
        requestParams.put("start_time", startTime)
        requestParams.put("end_time", endTime)
        try {
            requestParams.put("file_name", File(path))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return requestParams
    }

    fun jamRecordingUpload(context: Context, path: String, notes: String, startTime: String, endTime: String, view: View) { // convert through binary data and multi-part upload

        val UUID = PrefUtilsKt.Functions().retrieveUUID(context)
        val jamID = PrefUtilsKt.Functions().retrieveJamID(context)

        val headers = standardHeader(UUID)
        val requestParams = jamRecordingUploadParams(UUID, jamID, path, notes, startTime, endTime)
        val url = "http://api.draglabs.com/api/v2.0/jam/upload"

        upload(context, headers, requestParams, "audioFile", path, url, object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                logSuccessResponse(statusCode, headers!!, response!!)
                try {
                    //val message = JsonUtils.INSTANCE.getJsonObject(StringsModel.JAM_RECORDING_UPLOAD, response, StringsModel.jsonTypes.MESSAGE.type())

                    LogUtils.debug("Response Message: ", response.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                Snackbar.make(view, "Recording uploaded.", Snackbar.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, response: JSONObject?) {
                logFailureResponse(statusCode, headers!!, throwable, response!!)
            }
        })
    }

    private fun logSuccessResponse(statusCode: Int, headers: Array<Header>, response: JSONArray) {
        LogUtils.debug("Status Code: ", statusCode.toString() + "")
        LogUtils.debug("Headers: ", Arrays.toString(headers))
        LogUtils.debug("Response: ", response.toString())
    }

    private fun logSuccessResponse(statusCode: Int, headers: Array<Header>, response: JSONObject) {
        LogUtils.debug("Status Code: ", statusCode.toString() + "")
        LogUtils.debug("Headers: ", Arrays.toString(headers))
        LogUtils.debug("Response: ", response.toString())
    }

    private fun logFailureResponse(statusCode: Int, headers: Array<Header>, throwable: Throwable, response: JSONObject) {
        if (headers != null && throwable != null && response != null) {
            Log.v("Status Code: ", "" + statusCode)
            Log.v("Headers: ", Arrays.toString(headers) + "")
            Log.v("Throwable: ", throwable.message)
            Log.v("Response: ", response.toString())
        } else {
            Log.v("Reason: ", "Other Failure.")
        }
    }

    private fun upload(context: Context, headers: Array<Header>, requestParams: RequestParams, filename: String, path: String, url: String, asyncHttpResponseHandler: AsyncHttpResponseHandler) {
        val asyncHttpClient = AsyncHttpClient(true, 80, 433)
        val requestHandle = asyncHttpClient.post(context, url, headers, requestParams, headers[0].value, asyncHttpResponseHandler)
        if (requestHandle.isFinished) {
            LogUtils.debug("Upload Result: ", "Done with Upload. Tag: " + requestHandle.tag)
        } else {
            LogUtils.debug("Upload Result: ", "Upload Failed. Tag: " + requestHandle.tag)
        }
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

                    val code = response.code()
                    val message = response.message()
                    LogUtils.debug("RegisterUser Response Body", result.toString())
                    LogUtils.debug("RegisterUser Response Message", "Code: $code; Message: $message")
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

        call.enqueue(object : Callback<ResponseModelKt.UserFunctions.GetUserActivityArray> {
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
                LogUtils.debug("onFailure Failed", "Executed: " + call.isExecuted.toString())
            }
        })
    }

    fun getUserActivity(context: Context) {
        val UUID = PrefUtilsKt.Functions().retrieveUUID(context)

        val headers = userIDHeaders(UUID)
        val requestParams = RequestParams()

        val url = "http://api.draglabs.com/api/v2.0/user/activity"

        get(context, url, headers, requestParams, object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONArray) {
                LogUtils.debug("Get User Activity", "Success")
                logSuccessResponse(statusCode, headers, response)
                try {
                    val quantityOfJams = response.length()
                    var i = 0
                    val jams = ArrayList<JamViewModel>()
                    while (i < quantityOfJams) {
                        val jam = response[i] as JSONObject

                        val jamViewModel = JamViewModel()

                        jamViewModel.jamID = jam.getString("id")
                        jamViewModel.name = jam.getString("name")
                        jamViewModel.location = jam.getString("location")
                        jamViewModel.link = jam.getString("link")

                        jams.add(jamViewModel)

                        i++
                    }
                    //val jams = JsonUtils.INSTANCE.getJsonObject(StringsModel.GET_USER_ACTIVITY, response, StringsModel.jsonTypes.JAMS.type())
                    LogUtils.debug("Jams: ", jams.toString())
                    RealmUtils().storeJams(jams)
                    //PrefUtils(activity).saveUserActivity(jams)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, throwable: Throwable, response: JSONObject) {
                LogUtils.debug("Get User Activity", "Failure")
                logFailureResponse(statusCode, headers, throwable, response)
            }
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