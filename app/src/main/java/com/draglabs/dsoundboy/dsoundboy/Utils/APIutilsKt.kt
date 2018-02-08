/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import com.draglabs.dsoundboy.dsoundboy.Params.APIparamsKt
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
import android.os.Environment
import android.widget.Toast
import com.draglabs.dsoundboy.dsoundboy.Models.RecordingModel
import com.draglabs.dsoundboy.dsoundboy.Models.RecordingModelForList
import com.loopj.android.http.*
import io.realm.Realm
import org.json.JSONArray


/**
 * Created by davrukin on 12/30/2017.
 * @author Daniel Avrukin
 */
class APIutilsKt {

    object JamFunctions {
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
                    LogUtils.logOnFailure(t)
                }
            })
        }

        fun performUpdateJam(jamID: String, jamName: String, jamLocation: String, jamNotes: String) {
            val call = APIparamsKt().callUpdateJam(jamID, jamName, jamLocation, jamNotes)

            call.enqueue(object: Callback<ResponseModelKt.JamFunctions.UpdateJam> {
                override fun onResponse(call: Call<ResponseModelKt.JamFunctions.UpdateJam>, response: Response<ResponseModelKt.JamFunctions.UpdateJam>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val jamNameResponse = result!!.name
                        val jamLocationResponse = result.location
                        //val jamNotes = result.notes TODO: maybe add this into the JamViewModel later?
                        val jamLinkResponse = result.link
                        val jamNotesResponse = result.notes
                        val jamPinResponse = result.pin

                        RealmUtils.JamViewModelUtils.Edit.editJam(jamID, jamNameResponse, jamLocationResponse, jamLinkResponse, jamNotesResponse, jamPinResponse)

                        LogUtils.debug("UpdateJam Response", response.toString())
                        LogUtils.debug("UpdateJam Body", response.body().toString())
                    } else {
                        LogUtils.debug("Failed Response", response.errorBody()!!.toString())
                        LogUtils.debug("Code", "" + response.code())
                        LogUtils.debug("Message", response.message())
                        LogUtils.debug("Headers", response.headers().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseModelKt.JamFunctions.UpdateJam>, t: Throwable) {
                    LogUtils.logOnFailure(t)
                    LogUtils.debug("onFailure Call", call.toString())
                    LogUtils.debug("onFailure Failed", "Canceled: " + call.isCanceled.toString())
                    LogUtils.debug("onFailure Failed", "Executed: " + call.isExecuted.toString())
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
                    LogUtils.logOnFailure(t)
                }
            })
        }

        fun jamRecordingUpload(recording: RecordingModel, context: Context, notes: String) { // convert through binary data and multi-part upload

            val UUID = PrefUtilsKt.Functions().retrieveUUID(context)
            //val jamID = PrefUtilsKt.Functions().retrieveJamID(context)

            val headers = HttpFunctions.Headers.standardHeader(UUID)
            val requestParams = HttpFunctions.Params.jamRecordingUploadParams(UUID, recording.jamID, recording.filePath, notes, recording.startTime, recording.endTime)
            val url = "http://api.draglabs.com/api/v2.0/jam/upload"

            LogUtils.debug("Headers being sent", "${headers[0]}, ${headers[1]}")
            LogUtils.debug("RequestParams for Upload", requestParams.toString())

            HttpFunctions.Requests.upload(context, headers, requestParams, "audioFile", recording.filePath, url, object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                    LogUtils.logSuccessResponse(statusCode, headers!!, response!!)
                    try {
                        //val message = JsonUtils.INSTANCE.getJsonObject(StringsModel.JAM_RECORDING_UPLOAD, response, StringsModel.jsonTypes.MESSAGE.type())
                        LogUtils.debug("Response Message: ", response.toString())
                        LogUtils.debug("Recording Being Deleted", "$recording")
                        //val path = recording.filePath
                        RealmUtils.RecordingModelUtils.Delete.deleteRecordingModel(recording)
                        recording.deleteFromRealm()
                        LogUtils.debug("Recording Deleted", "Done")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    //Snackbar.make(view, "Recording uploaded.", Snackbar.LENGTH_LONG).show()
                    Toast.makeText(context, "Recording Uploaded", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, response: JSONObject?) {
                    LogUtils.logFailureResponse(statusCode, headers!!, throwable, response!!)
                }
            })
        }

        fun performGetActiveJam(context: Context) {
            val realm = Realm.getDefaultInstance()
            val uuid = RealmUtils.UserModelUtils.Retrieve.retrieveUser(realm).UUID
            realm.close()
            val call = APIparamsKt().callGetActiveJam(uuid)

            call.enqueue(object: Callback<ResponseModelKt.UserFunctions.GetActiveJam> {
                override fun onResponse(call: Call<ResponseModelKt.UserFunctions.GetActiveJam>, response: Response<ResponseModelKt.UserFunctions.GetActiveJam>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        //val jamNameResponse = result!!.name
                        //val jamLocationResponse = result.location
                        //val jamNotes = result.notes TODO: maybe add this into the JamViewModel later?
                        //val jamLinkResponse = result.link
                        //val jamNotesResponse = result.notes
                        val jamPinResponse = result!!.pin
                        val jamIdResponse = result.id
                        PrefUtilsKt.Functions().storePIN(context, jamPinResponse)
                        PrefUtilsKt.Functions().storeJamID(context, jamIdResponse)
                        //RealmUtils.JamViewModelUtils.Edit.editJam(jamID, jamNameResponse, jamLocationResponse, jamLinkResponse, jamNotesResponse, jamPinResponse)

                        LogUtils.debug("ActiveJam Response", response.toString())
                        LogUtils.debug("ActiveJam Body", result.toString())
                    } else {
                        LogUtils.debug("ActiveJam Failed Response", response.errorBody()!!.toString())
                        LogUtils.debug("ActiveJam Code", "" + response.code())
                        LogUtils.debug("ActiveJam Message", response.message())
                        LogUtils.debug("ActiveJam Headers", response.headers().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseModelKt.UserFunctions.GetActiveJam>, t: Throwable) {
                    LogUtils.logOnFailure(t)
                    LogUtils.debug("ActiveJam onFailure Failed", "Canceled" + call.isCanceled.toString())
                    LogUtils.debug("ActiveJam onFailure Failed", "Executed" + call.isExecuted.toString())
                }
            })
        }

        fun getJamDetails(context: Context, jamID: String) {
            val UUID = PrefUtilsKt.Functions().retrieveUUID(context)
            //val jamID = PrefUtilsKt.Functions().retrieveJamID(context)

            val url = "http://api.draglabs.com/api/v2.0/jam/details/$jamID"

            val requestParams = RequestParams()
            val headers = HttpFunctions.Headers.userIDHeaders(UUID)

            HttpFunctions.Requests.get(context, url, headers, requestParams, object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                    LogUtils.logSuccessResponse(statusCode, headers!!, response!!)
                    try {
                        val link = response.getString("link")
                        val pin = response.getString("pin")
                        LogUtils.debug("GetJamDetails NPE", "link $link, pin $pin, jamID $jamID")
                        //checkLink(link, jamID)
                        LogUtils.debug("Jam Details: ", response.toString())
                        LogUtils.debug("Jam PIN: ", pin) // TODO: create an NPE checker for all these potential values
                        RealmUtils.JamViewModelUtils.Edit.editJam(jamID, RealmUtils.JamVars.PIN, pin)
                        //PrefUtils(activity).saveJamDetails(jamDetails) // TODO: enable later
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, response: JSONObject?) {
                    LogUtils.logFailureResponse(statusCode, headers!!, throwable, response!!)
                }
            })
        }

        private fun checkLink(link: String, jamID: String) {
            if (link != null && link != "") {
                RealmUtils.JamViewModelUtils.Edit.editJam(jamID, RealmUtils.JamVars.LINK, link)
                LogUtils.debug("Realm'd Link", RealmUtils.JamViewModelUtils.Retrieve.retrieveJam(jamID).toString())
            }
        }

        fun performCompressor(context: Context, jamID: String) {
            val call = APIparamsKt().callCompressor(context, jamID)

            call.enqueue(object: Callback<ResponseModelKt.CompressorFunctions.Compressor> {
                override fun onResponse(call: Call<ResponseModelKt.CompressorFunctions.Compressor>, response: Response<ResponseModelKt.CompressorFunctions.Compressor>) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        LogUtils.debug("Compressor Response Body", result.toString())
                        LogUtils.debug("Compressor Response Message", "Code: ${response.code()}; Message: ${response.message()}")
                    } else {
                        LogUtils.debug("Compressor Failed Response", response.errorBody()!!.toString())
                        LogUtils.debug("Compressor Code", "" + response.code())
                        LogUtils.debug("Compressor Message", response.message())
                        LogUtils.debug("Compressor Headers", response.headers().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseModelKt.CompressorFunctions.Compressor>, t: Throwable) {
                    LogUtils.logOnFailure(t)
                }
            })
        }
    }

    object UserFunctions {
        fun performRegisterUser(activity: Activity, context: Context) {
            val call = APIparamsKt().callRegisterUser(activity)

            call.enqueue(object : Callback<ResponseModelKt.UserFunctions.RegisterUser> {
                override fun onResponse(call: Call<ResponseModelKt.UserFunctions.RegisterUser>, response: Response<ResponseModelKt.UserFunctions.RegisterUser>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val id = result!!.id
                        LogUtils.debug("id", id)
                        PrefUtilsKt.Functions().storeUUID(context, id)
                        //RealmUtils.UserModelUtils.Store.storeUUID(id)

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
                    LogUtils.logOnFailure(t)
                    LogUtils.debug("onFailure Failed", "Canceled" + call.isCanceled.toString())
                    LogUtils.debug("onFailure Failed", "Executed" + call.isExecuted.toString())
                }
            })
        }

        fun getUserActivity(context: Context) {
            val UUID = PrefUtilsKt.Functions().retrieveUUID(context)

            val headers = HttpFunctions.Headers.userIDHeaders(UUID)
            val requestParams = RequestParams()

            val url = "http://api.draglabs.com/api/v2.0/user/activity"

            HttpFunctions.Requests.get(context, url, headers, requestParams, object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONArray) {
                    LogUtils.debug("Get User Activity", "Success")
                    LogUtils.logSuccessResponse(statusCode, headers, response)
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
                            jamViewModel.pin = jam.getString("pin")
                            jamViewModel.notes = jam.getString("notes")

                            jams.add(jamViewModel)

                            i++
                        }
                        //val jams = JsonUtils.INSTANCE.getJsonObject(StringsModel.GET_USER_ACTIVITY, response, StringsModel.jsonTypes.JAMS.type())
                        LogUtils.debug("Jams: ", jams.toString())
                        RealmUtils.JamViewModelUtils.Store.storeJams(jams)
                        //PrefUtils(activity).saveUserActivity(jams)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, throwable: Throwable, response: JSONObject) {
                    LogUtils.debug("Get User Activity", "Failure")
                    LogUtils.logFailureResponse(statusCode, headers, throwable, response)
                }
            })
        }
    }

    private object HttpFunctions {
        object Headers {
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

            fun standardHeader(UUID: String): Array<Header> {
                return arrayOf(object : Header {
                    override fun getName(): String {
                        return "Content-Type"
                    }
                    override fun getValue(): String {
                        return "multipart/form-data; boundary=123456789"
                    }

                    @Throws(ParseException::class)
                    override fun getElements(): Array<HeaderElement?> {
                        return arrayOfNulls<HeaderElement>(0)
                    }

                    override fun toString(): String {
                        return "Name: $name; Value: $value; Elements: $elements"
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

                    override fun toString(): String {
                        return "Name: $name; Value: $value; Elements: $elements"
                    }
                })
            }
        }
        object Params {
            fun jamRecordingUploadParams(UUID: String,
                                                 jamID: String,
                                                 path: String,
                                                 location: String,
                                                 startTime: String,
                                                 endTime: String): RequestParams {
                val requestParams = RequestParams()
                requestParams.put("user_id", UUID)
                requestParams.put("id", jamID)
                //requestParams.put("location", location)
                requestParams.put("start_time", startTime)
                requestParams.put("end_time", endTime)
                val testPath = "${Environment.getExternalStorageDirectory()}/Download/realm-java-4.3.3.zip"
                try {
                    requestParams.put("audioFile", File(testPath), "application/octet-stream")
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                return requestParams
            }
        }
        object Requests {
            fun get(context: Context, url: String, headers: Array<Header>, requestParams: RequestParams, asyncHttpResponseHandler: AsyncHttpResponseHandler): String {
                val asyncHttpClient = AsyncHttpClient(true, 80, 433)
                val requestHandle = asyncHttpClient.get(context, url, headers, requestParams, asyncHttpResponseHandler)
                return if (requestHandle.isFinished) {
                    "Done with GET. Tag: " + requestHandle.tag
                } else {
                    "GET Failed. Tag: " + requestHandle.tag
                }
            }

            fun upload(context: Context, headers: Array<Header>, requestParams: RequestParams, filename: String, path: String, url: String, asyncHttpResponseHandler: AsyncHttpResponseHandler) {
                val asyncHttpClient = SyncHttpClient(true, 80, 433)
                val requestHandle = asyncHttpClient.post(context, url, headers, requestParams, "multipart/form-data; boundary=123456789", asyncHttpResponseHandler)
                if (requestHandle.isFinished) {
                    LogUtils.debug("Upload Result: ", "Done with Upload. Tag: " + requestHandle.tag)
                } else {
                    LogUtils.debug("Upload Result: ", "Upload Failed. Tag: " + requestHandle.tag)
                }
            }
        }
    }

    @Deprecated("Use getJamDetails() instead")
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
                    LogUtils.debug("Headers", response.headers().toString())
                }
            }

            override fun onFailure(call: Call<ResponseModelKt.JamFunctions.GetJamDetails>, t: Throwable) {
                LogUtils.logOnFailure(t)
                LogUtils.debug("onFailure Call", call.toString())
                LogUtils.debug("onFailure Failed", "Canceled: " + call.isCanceled.toString())
                LogUtils.debug("onFailure Failed", "Executed: " + call.isExecuted.toString())
            }
        })
    }

    @Deprecated("Use jamRecordingUpload instead")
    fun performUploadJam(filepath: String, context: Context, location: String) {
        LogUtils.debug("Entering Function", "performUploadJam")
        val realm = Realm.getDefaultInstance()
        val recording = realm.where(RecordingModel::class.java).equalTo("filePath", filepath).findFirst()

        val UUID = PrefUtilsKt.Functions().retrieveUUID(context)
        val call = APIparamsKt().callUploadJam(recording!!.filePath, UUID, "audioFile", location, recording.jamID, recording.startTime, recording.endTime)

        call.enqueue(object: Callback<ResponseModelKt.JamFunctions.UploadJam> {
            override fun onResponse(call: Call<ResponseModelKt.JamFunctions.UploadJam>, response: Response<ResponseModelKt.JamFunctions.UploadJam>) {
                LogUtils.debug("performUploadJam", "API Responded")
                if (response.isSuccessful) {
                    LogUtils.debug("performUploadJam", "Successful Response")
                    val result = response.body()
                    val code = response.code()
                    val message = response.message()
                    LogUtils.debug("performUploadJam Response Body", result.toString())
                    LogUtils.debug("performUploadJam Response Message", "Code: $code; Message: $message")

                    //RealmUtils.RecordingModelUtils.Edit.setRecordingAsUploaded(realm, recording.filePath)
                    //realm.close()
                    //val newRealm = Realm.getDefaultInstance()

                    LogUtils.debug("performUploadJam", "File Path: $filepath")
                    //RealmUtils.RecordingModelUtils.Delete.deleteRecordingModel(realm, path)
                    //recording.deleteFromRealm()

                    //realm.executeTransaction { recording.deleteFromRealm() }
                    //deleteRecording(filepath, realm)
                    File(filepath).delete()
                    LogUtils.debug("performUploadJam", "File Deleted")

                    //newRealm.close()
                } else {
                    LogUtils.debug("performUploadJam Failed Response", "${response.errorBody()}")
                    LogUtils.debug("performUploadJam Code", "${response.code()}")
                    LogUtils.debug("performUploadJam Message", response.message())
                    LogUtils.debug("performUploadJam Headers", response.headers().toString())
                }
            }

            override fun onFailure(call: Call<ResponseModelKt.JamFunctions.UploadJam>, t: Throwable) {
                LogUtils.debug("performUploadJam", "onFailure")
                LogUtils.logOnFailure(t)
            }
        })
        /*LogUtils.debug("performUploadJam Success?", "$success")
        if (success) {
            File(filepath).delete()
            LogUtils.debug("performUploadJam", "File Deleted")
            realm.executeTransaction { recording.deleteFromRealm() }
            LogUtils.debug("performUploadJam", "Recording Deleted from Realm")
        }
        realm.close()*/
    }

    private fun deleteRecording(filepath: String, realm: Realm) {
        if (!realm.isClosed) {
            realm.close()
        }
        var newRealm = Realm.getDefaultInstance()

        val recording = realm.where(RecordingModel::class.java).equalTo("filePath", filepath).findFirst()
        File(filepath).delete()
        LogUtils.debug("performUploadJam", "File Deleted")
        realm.executeTransaction { recording!!.deleteFromRealm() }
        LogUtils.debug("performUploadJam", "Recording Deleted from Realm")

        newRealm.close()
    }

    @Deprecated("Use getUserActivity instead")
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
                        RealmUtils.JamViewModelUtils.Store.storeJams(jams)
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
                LogUtils.logOnFailure(t)
                LogUtils.debug("onFailure Failed", "Canceled: " + call.isCanceled.toString())
                LogUtils.debug("onFailure Failed", "Executed: " + call.isExecuted.toString())            }
        })
    }

    @Deprecated("Use getUserActivity instead")
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
                            val notes = element.notes
                            val pin = element.pin
                            val jamViewModel = JamViewModel(id, name, location, link, notes, pin)
                            jams.add(jamViewModel)
                        }
                        LogUtils.debug("Jams", jams.toString())
                        LogUtils.debug("Result", result.toString())
                        //PrefUtilsKt.Functions().storeJams(context, arrayList)
                        RealmUtils.JamViewModelUtils.Store.storeJams(jams)
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
                LogUtils.logOnFailure(t)
                LogUtils.debug("onFailure Call", call.toString())
                LogUtils.debug("onFailure Failed", "Canceled: " + call.isCanceled.toString())
                LogUtils.debug("onFailure Failed", "Executed: " + call.isExecuted.toString())
            }
        })
    }
}