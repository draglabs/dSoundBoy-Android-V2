/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Models.RecordingModel
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import com.draglabs.dsoundboy.dsoundboy.Params.APIparamsKt
import com.loopj.android.http.*
import com.mashape.unirest.http.Unirest
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.HeaderElement
import cz.msebera.android.httpclient.ParseException
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import cz.msebera.android.httpclient.client.methods.RequestBuilder.post
import kotlinx.coroutines.experimental.async
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread
import cz.msebera.android.httpclient.client.methods.RequestBuilder.post




/**
 * Created by davrukin on 12/30/2017.
 * @author Daniel Avrukin
 */
class APIutilsKt {

    object JamFunctions {
        fun performNewJam(context: Context, UUID: String, name: String, location: String, lat: Any, lng: Any, notes: String) {
            val tag = "NewJam"
            LogUtils.logEnteringFunction("perform$tag")

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
                        LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                    } else {
                        LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseModelKt.JamFunctions.NewJam>, t: Throwable) {
                    LogUtils.logOnFailure(t)
                    val error = "Call: $call\nCanceled: ${call.isCanceled}\nExecuted: ${call.isExecuted}"
                    LogUtils.error("$tag onFailure Failed", error)
                }
            })
        }

        fun performUpdateJam(jamID: String, jamName: String, jamLocation: String, jamNotes: String) {
            val tag = "UpdateJam"
            LogUtils.logEnteringFunction("perform$tag")

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

                        val code = response.code()
                        val message = response.message()
                        LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                    } else {
                        LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseModelKt.JamFunctions.UpdateJam>, t: Throwable) {
                    LogUtils.logOnFailure(t)
                    val error = "Call: $call\nCanceled: ${call.isCanceled}\nExecuted: ${call.isExecuted}"
                    LogUtils.error("$tag onFailure Failed", error)
                }
            })
        }

        fun performJoinJam(context: Context, pin: String, UUID: String) {
            val tag = "JoinJam"
            LogUtils.logEnteringFunction("perform$tag")

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

                        val code = response.code()
                        val message = response.message()
                        LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                    } else {
                        LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseModelKt.JamFunctions.JoinJam>, t: Throwable) {
                    LogUtils.logOnFailure(t)
                    val error = "Call: $call\nCanceled: ${call.isCanceled}\nExecuted: ${call.isExecuted}"
                    LogUtils.error("$tag onFailure Failed", error)
                }
            })
        }

        fun jamRecordingUpload(recording: RecordingModel, context: Context, notes: String) { // convert through binary data and multi-part upload
            val tag = "JamRecordingUpload"
            LogUtils.logEnteringFunction("jamRecordingUpload")

            val UUID = PrefUtilsKt.Functions().retrieveUUID(context)
            //val jamID = PrefUtilsKt.Functions().retrieveJamID(context)

            val headers = HttpFunctions.Headers.standardHeader(UUID)
            val requestParams = HttpFunctions.Params.jamRecordingUploadParams(UUID, recording.jamID, recording.filePath, notes, recording.startTime, recording.endTime)
            val url = "https://api.draglabs.com/api/v2.0/jam/upload"

            LogUtils.debug("Headers being sent", "${headers[0]}, ${headers[1]}")
            LogUtils.debug("RequestParams for Upload", requestParams.toString())

            HttpFunctions.Requests.upload(context, headers, requestParams, "audioFile", recording.filePath, url, object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                    LogUtils.logSuccessResponse(tag, statusCode, headers!!, response!!)
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
            val tag = "GetActiveJam"
            LogUtils.logEnteringFunction("perform$tag")

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

                        val code = response.code()
                        val message = response.message()
                        LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                    } else {
                        LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseModelKt.UserFunctions.GetActiveJam>, t: Throwable) {
                    LogUtils.logOnFailure(t)
                    val error = "Call: $call\nCanceled: ${call.isCanceled}\nExecuted: ${call.isExecuted}"
                    LogUtils.error("$tag onFailure Failed", error)
                }
            })
        }

        fun getJamDetails(context: Context, jamID: String) {
            val tag = "GetJamDetails"
            LogUtils.logEnteringFunction("getJamDetails")

            val UUID = PrefUtilsKt.Functions().retrieveUUID(context)
            //val jamID = PrefUtilsKt.Functions().retrieveJamID(context)

            val url = "https://api.draglabs.com/api/v2.0/jam/details/$jamID"

            val requestParams = RequestParams()
            val headers = HttpFunctions.Headers.userIDHeaders(UUID)

            HttpFunctions.Requests.get(context, url, headers, requestParams, object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                    LogUtils.logSuccessResponse(tag, statusCode, headers!!, response!!)
                    try {
                        val link = response.getString("link")
                        val pin = response.getString("pin")
                        LogUtils.debug("GetJamDetails NPE", "link $link, pin $pin, jamID $jamID")
                        //checkLink(link, jamID)
                        LogUtils.debug("Jam Details: ", response.toString())
                        LogUtils.debug("Jam PIN: ", pin) // TODO: create an NPE checker for all these potential values
                        RealmUtils.JamViewModelUtils.Edit.editJam(jamID, RealmUtils.JamVars.PIN, pin)
                        //PrefUtils(activity).saveJamDetails(jamDetails) // TODO: enable later

                        //LogUtils.logSuccessResponse(tag, statusCode, headers, response)
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
            LogUtils.logEnteringFunction("checkLink")

            if (link != null && link != "") {
                RealmUtils.JamViewModelUtils.Edit.editJam(jamID, RealmUtils.JamVars.LINK, link)
                LogUtils.debug("Realm'd Link", RealmUtils.JamViewModelUtils.Retrieve.retrieveJam(jamID).toString())
            }
        }

        fun performCompressor(context: Context, jamID: String) {
            val tag = "Compressor"
            LogUtils.logEnteringFunction("perform $tag")

            val call = APIparamsKt().callCompressor(context, jamID)

            val UUID = PrefUtilsKt.Functions().retrieveUUID(context)
            val url = "http://api.draglabs.com/api/v2.0/archive/"
            val requestParams = RequestParams()
            requestParams.put("user_id", UUID)
            requestParams.put("jam_id", jamID)
            val headers = HttpFunctions.Headers.userIDHeaders(UUID)

            /*HttpFunctions.Requests.post(context, url, headers, requestParams, object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                    LogUtils.info("Compressor URL", url)
                    LogUtils.logSuccessResponse(tag, statusCode, headers!!, response!!)
                    try {
                        LogUtils.debug("$tag Response", "Body: $response\nCode: $statusCode\nHeaders: $headers")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, response: JSONObject?) {
                    LogUtils.error("$tag onFailure", "Failed")
                    LogUtils.logFailureResponse(statusCode, headers!!, throwable, response!!)
                }
            })*/

            //val response = thread { testCompress(tag) }
            val startTime = System.currentTimeMillis()
            async { testCompressUnirest(tag, UUID, jamID, startTime) }

            /*call.enqueue(object: Callback<ResponseModelKt.CompressorFunctions.Compressor> {
                override fun onResponse(call: Call<ResponseModelKt.CompressorFunctions.Compressor>, response: Response<ResponseModelKt.CompressorFunctions.Compressor>) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        val code = response.code()
                        val message = response.message()
                        LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                    } else {
                        LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseModelKt.CompressorFunctions.Compressor>, t: Throwable) {
                    LogUtils.error("$tag onFailure", "Failed")
                    LogUtils.logOnFailure(t)
                }
            })*/
        }

        private fun testCompress(tag: String): okhttp3.Response {
            LogUtils.logEnteringFunction("testCompress")

            val client = OkHttpClient()

            val mediaType = MediaType.parse("application/json")
            val body = RequestBody.create(mediaType, "{\n\t\"user_id\":\"5a14dd1abe307d2394fa4565\",\n\t\"jam_id\":\"5aa307496871330001afde02\"\n}")
            val request = Request.Builder()
                    .url("https://api.draglabs.com/api/v2.0/jam/archive")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Postman-Token", "a7ed8f00-7947-467c-8f32-fc8174d48f3e")
                    .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val body = response.body().toString()
                val code = response.code().toString()
                val headers = response.headers()
                val message = response.message()
                LogUtils.logSuccessResponse("$tag Response", code, body, message)
            } else {
                LogUtils.info("$tag Response", "Failed")
            }

            return response
        }

        private fun testCompressUnirest(tag: String, UUID: String, jamID: String, startTime: Long) {
            LogUtils.logEnteringFunction("testCompressUnirest")

            val response = Unirest.post("https://api.draglabs.com/api/v2.0/jam/archive")
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .header("Postman-Token", "29f454d8-49e7-463d-80cb-d15baf01b0fe")
                    .body("{\n\t\"user_id\":\"$UUID\",\n\t\"jam_id\":\"$jamID\"\n}")
                    .asString()

            val endTime = System.currentTimeMillis()
            LogUtils.info("Compressor Run-Finish", "${endTime - startTime} ms")

            LogUtils.info("$tag Response", response.body.toString())
        }
    }

    object UserFunctions {
        fun performRegisterUser(activity: Activity, context: Context) {
            val tag = "RegisterUser"
            LogUtils.logEnteringFunction("perform$tag")

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
                        LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                    } else {
                        LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                    }
                }
                override fun onFailure(call: Call<ResponseModelKt.UserFunctions.RegisterUser>, t: Throwable) {
                    LogUtils.logOnFailure(t)
                    val error = "Call: $call\nCanceled: ${call.isCanceled}\nExecuted: ${call.isExecuted}"
                    LogUtils.error("$tag onFailure Failed", error)
                }
            })
        }

        fun getUserActivity(context: Context) {
            val tag = "getUserActivity"
            LogUtils.logEnteringFunction(tag)

            val UUID = PrefUtilsKt.Functions().retrieveUUID(context)

            val headers = HttpFunctions.Headers.userIDHeaders(UUID)
            val requestParams = RequestParams()

            val url = "https://api.draglabs.com/api/v2.0/user/activity"

            HttpFunctions.Requests.get(context, url, headers, requestParams, object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONArray) {
                    LogUtils.debug("Get User Activity", "Success")
                    LogUtils.logSuccessResponse(tag, statusCode, headers, response)
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
                LogUtils.logEnteringFunction("userIDHeaders")

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
                LogUtils.logEnteringFunction("standardHeader")

                return arrayOf(object: Header {
                    override fun getName(): String {
                        return "Content-Type"
                    }
                    override fun getValue(): String {
                        return "multipart/form-data; boundary=123456789"
                    }

                    @Throws(ParseException::class)
                    override fun getElements(): Array<HeaderElement?> {
                        return arrayOfNulls(0)
                    }

                    override fun toString(): String {
                        return "Name: $name; Value: $value; Elements: $elements"
                    }
                }, object: Header {
                    override fun getName(): String {
                        return "user_id"
                    }

                    override fun getValue(): String {
                        return UUID
                    }

                    @Throws(ParseException::class)
                    override fun getElements(): Array<HeaderElement?> {
                        return arrayOfNulls(1)
                    }

                    override fun toString(): String {
                        return "Name: $name; Value: $value; Elements: $elements"
                    }
                })
            }

            fun compressorHeaders(UUID: String, jamID: String): Array<Header> {
                LogUtils.logEnteringFunction("compressorHeaders")

                return arrayOf(object: Header {
                    override fun getName(): String {
                        return "Content-Type"
                    }
                    override fun getValue(): String {
                        return "*/*"
                    }

                    @Throws(ParseException::class)
                    override fun getElements(): Array<HeaderElement?> {
                        return arrayOfNulls(0)
                    }

                    override fun toString(): String {
                        return "Name: $name; Value: $value; Elements: $elements"
                    }
                }, object: Header {
                    override fun getName(): String {
                        return "user_id"
                    }

                    override fun getValue(): String {
                        return UUID
                    }

                    @Throws(ParseException::class)
                    override fun getElements(): Array<HeaderElement?> {
                        return arrayOfNulls(1)
                    }

                    override fun toString(): String {
                        return "Name: $name; Value: $value; Elements: $elements"
                    }
                }, object: Header {
                    override fun getName(): String {
                        return "jam_id"
                    }

                    override fun getValue(): String {
                        return jamID
                    }

                    @Throws(ParseException::class)
                    override fun getElements(): Array<HeaderElement?> {
                        return arrayOfNulls(2)
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
                LogUtils.logEnteringFunction("jamRecordingUploadParams")

                val requestParams = RequestParams()
                requestParams.put("user_id", UUID)
                requestParams.put("id", jamID)
                //requestParams.put("location", location)
                requestParams.put("start_time", startTime)
                requestParams.put("end_time", endTime)
                val testPath = "${Environment.getExternalStorageDirectory()}/dSoundBoyRecordings/$path"
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
                LogUtils.logEnteringFunction("get")

                val asyncHttpClient = AsyncHttpClient(true, 80, 433)
                asyncHttpClient.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory())
                val requestHandle = asyncHttpClient.get(context, url, headers, requestParams, asyncHttpResponseHandler)
                return if (requestHandle.isFinished) {
                    "Done with GET. Tag: " + requestHandle.tag
                } else {
                    "GET Failed. Tag: " + requestHandle.tag
                }
            }

            fun post(context: Context, url: String, headers: Array<Header>, requestParams: RequestParams, asyncHttpResponseHandler: AsyncHttpResponseHandler): String {
                LogUtils.logEnteringFunction("get")

                val asyncHttpClient = AsyncHttpClient(true, 80, 433)
                asyncHttpClient.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory())
                val requestHandle = asyncHttpClient.post(context, url, headers, requestParams,"application/json" , asyncHttpResponseHandler)
                return if (requestHandle.isFinished) {
                    "Done with GET. Tag: " + requestHandle.tag
                } else {
                    "GET Failed. Tag: " + requestHandle.tag
                }
            }

            fun upload(context: Context, headers: Array<Header>, requestParams: RequestParams, filename: String, path: String, url: String, asyncHttpResponseHandler: AsyncHttpResponseHandler) {
                LogUtils.logEnteringFunction("upload")

                val asyncHttpClient = SyncHttpClient(true, 80, 433)
                asyncHttpClient.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory())
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
        val tag = "GetJamDetails"
        LogUtils.logEnteringFunction("perform$tag")

        val call = APIparamsKt().callGetJamDetails(context, jamID)

        call.enqueue(object: Callback<ResponseModelKt.JamFunctions.GetJamDetails> {
            override fun onResponse(call: Call<ResponseModelKt.JamFunctions.GetJamDetails>, response: Response<ResponseModelKt.JamFunctions.GetJamDetails>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    LogUtils.debug("Response from JamDetails", result.toString())
                    val link = result!!.link
                    PrefUtilsKt.Functions().storeLink(context, link)
                    LogUtils.debug("Gotten Link", link)

                    val code = response.code()
                    val message = response.message()
                    LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                } else {
                    LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                }
            }

            override fun onFailure(call: Call<ResponseModelKt.JamFunctions.GetJamDetails>, t: Throwable) {
                val error = "Call: $call\nCanceled: ${call.isCanceled}\nExecuted: ${call.isExecuted}"
                LogUtils.error("$tag onFailure Failed", error)
            }
        })
    }

    @Deprecated("Use jamRecordingUpload instead")
    fun performUploadJam(filepath: String, context: Context, location: String) {
        val tag = "UploadJam"
        LogUtils.logEnteringFunction("perform$tag")

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
                    Toast.makeText(context, "Upload Complete", Toast.LENGTH_SHORT).show()
                    //RealmUtils.RecordingModelUtils.Edit.setRecordingAsUploaded(realm, recording.filePath)
                    //realm.close()
                    //val newRealm = Realm.getDefaultInstance()

                    LogUtils.debug("performUploadJam", "File Path: $filepath")
                    //RealmUtils.RecordingModelUtils.Delete.deleteRecordingModel(realm, path)
                    //recording.deleteFromRealm()

                    //realm.executeTransaction { recording.deleteFromRealm() }
                    //deleteRecording(filepath, realm)
                    val file = File(filepath)
                    if (file.exists()) {
                        file.delete()
                    }
                    LogUtils.debug("performUploadJam", "File Deleted")

                    //newRealm.close()

                    LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                } else {
                    LogUtils.error("$tag Failed", "Response Error")
                    LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                }
            }

            override fun onFailure(call: Call<ResponseModelKt.JamFunctions.UploadJam>, t: Throwable) {
                LogUtils.logOnFailure(t)
                val error = "Call: $call\nCanceled: ${call.isCanceled}\nExecuted: ${call.isExecuted}"
                LogUtils.error("$tag onFailure Failed", error)
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
        LogUtils.logEnteringFunction("deleteRecording")

        if (!realm.isClosed) {
            realm.close()
        }
        val newRealm = Realm.getDefaultInstance()

        val recording = realm.where(RecordingModel::class.java).equalTo("filePath", filepath).findFirst()
        File(filepath).delete()
        LogUtils.debug("performUploadJam", "File Deleted")
        realm.executeTransaction { recording!!.deleteFromRealm() }
        LogUtils.debug("performUploadJam", "Recording Deleted from Realm")

        newRealm.close()
    }

    @Deprecated("Use getUserActivity instead")
    fun performGetUserActivity(context: Context) {
        val tag = "GetUserActivity"
        LogUtils.logEnteringFunction("perform$tag")

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

                        val code = response.code()
                        val message = response.message()
                        LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                    } else {
                        // TODO: no jams, account for something here
                        LogUtils.debug("Result", "Result is null")
                    }
                } else {
                    LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                }
            }
            override fun onFailure(call: Call<ResponseModelKt.UserFunctions.GetUserActivity>, t: Throwable) {
                LogUtils.logOnFailure(t)
                val error = "Call: $call\nCanceled: ${call.isCanceled}\nExecuted: ${call.isExecuted}"
                LogUtils.error("$tag onFailure Failed", error)
            }
        })
    }

    @Deprecated("Use getUserActivity instead")
    fun performGetUserActivityArray(context: Context) {
        val tag = "GetUserActivity"
        LogUtils.logEnteringFunction("perform${tag}Array")

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

                        val code = response.code()
                        val message = response.message()
                        LogUtils.debug("$tag Response", "Body: $result\nCode: $code\nMessage: $message")
                    } else {
                        // TODO: no jams, account for something here
                        LogUtils.debug("$tag Result", "Result is null")
                    }
                } else {
                    LogUtils.logFailureResponse(tag, response.errorBody()!!.toString(), response.code(), response.message(), response.headers().toString())
                }
            }

            override fun onFailure(call: Call<ResponseModelKt.UserFunctions.GetUserActivityArray>, t: Throwable) {
                LogUtils.logOnFailure(t)
                val error = "Call: $call\nCanceled: ${call.isCanceled}\nExecuted: ${call.isExecuted}"
                LogUtils.error("$tag onFailure Failed", error)
            }
        })
    }
}