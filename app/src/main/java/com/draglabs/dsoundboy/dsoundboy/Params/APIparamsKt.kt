/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Params

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.ArrayMap
import com.draglabs.dsoundboy.dsoundboy.Activities.MainActivity
import com.draglabs.dsoundboy.dsoundboy.Interfaces.ApiInterface
import com.draglabs.dsoundboy.dsoundboy.Interfaces.RetrofitClient
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import com.draglabs.dsoundboy.dsoundboy.Models.StringsModelKt
import com.draglabs.dsoundboy.dsoundboy.Routines.MainRoutine
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtilsKt
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.Profile
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import java.io.File

/**
 * Created by davrukin on 1/4/2018.
 * @author Daniel Avrukin
 */
class APIparamsKt {

    /**
     * Each ones takes in parameters
     * Creates ArrayMap<String, String>
     * Generates RequestBody
     * Returns a call object
     */

    private val userService = RetrofitClient().getClient().create(ApiInterface::class.java)

    private val name            = StringsModelKt.JsonParsingKeys.NAME
    private val location        = StringsModelKt.JsonParsingKeys.LOCATION
    private val lat             = StringsModelKt.JsonParsingKeys.LAT
    private val lng             = StringsModelKt.JsonParsingKeys.LNG
    private val notes           = StringsModelKt.JsonParsingKeys.NOTES
    private val pin             = StringsModelKt.JsonParsingKeys.PIN
    private val user_id         = StringsModelKt.JsonParsingKeys.USER_ID
    private val file_name       = StringsModelKt.JsonParsingKeys.FILE_NAME
    private val jam_id          = StringsModelKt.JsonParsingKeys.JAM_ID
    private val start_time      = StringsModelKt.JsonParsingKeys.START_TIME
    private val end_time        = StringsModelKt.JsonParsingKeys.END_TIME
    private val facebook_id     = StringsModelKt.JsonParsingKeys.FACEBOOK_ID
    private val access_token    = StringsModelKt.JsonParsingKeys.ACCESS_TOKEN
    private val id              = StringsModelKt.JsonParsingKeys.ID

    private val jsonTypeStringForRequest = "application/json; charset=utf-8"

    fun callNewJam(UUID: String, name: String, location: String, lat: Any, lng: Any, notes: String): Call<ResponseModelKt.JamFunctions.NewJam> {
        val params = ArrayMap<String, Any>()
        params[this.name] = name
        params[this.location] = location
        params[this.lat] = lat
        params[this.lng] = lng
        params[this.notes] = notes
        val requestBody = createRequestBody(params, jsonTypeStringForRequest)

        return userService.newJam(UUID, requestBody)
    }

    fun callUpdateJam(jamID: String, jamName: String, jamLocation: String, jamNotes: String): Call<ResponseModelKt.JamFunctions.UpdateJam> {
        val params = ArrayMap<String, Any>()
        params[this.id] = jamID
        params[this.name] = jamName
        params[this.location] = jamLocation
        params[this.notes] = jamNotes
        val requestBody = createRequestBody(params, jsonTypeStringForRequest)

        return userService.updateJam(requestBody)
    }

    fun callJoinJam(pin: String, UUID: String): Call<ResponseModelKt.JamFunctions.JoinJam> {
        val params = ArrayMap<String, Any>()
        params[this.pin] = pin
        params[this.user_id] = UUID
        val requestBody = createRequestBody(params, jsonTypeStringForRequest)

        return userService.joinJam(requestBody)
    }

    fun callGetJamDetails(context: Context, jamID: String): Call<ResponseModelKt.JamFunctions.GetJamDetails> {
        val uuid = PrefUtilsKt.Functions().retrieveUUID(context)

        return userService.getJamDetails(jamID, uuid)
    }

    fun callUploadJam(filePath: String, userID: String, fileName: String, location: String, jamID: String,  startTime: String, endTime: String): Call<ResponseModelKt.JamFunctions.UploadJam> {
        /*val params = ArrayMap<String, Any>()
        params[this.user_id] = userID
        params[this.file_name] = fileName
        params[this.location] = location
        params[this.jam_id] = jamID
        params[this.start_time] = startTime
        params[this.end_time] = endTime
        val requestBody = createRequestBody(params, jsonTypeStringForRequest)*/

        val file = File(filePath)

        //val multiPartBodyParams = createMultipartBody("", "", requestBody)
        val multiPartBodyFile = createMultipartBody("file", file.name, RequestBody.create(MediaType.parse("*/*"), file))
        val multiPartBodyUserID = MultipartBody.Part.createFormData(this.user_id, userID)
        val multiPartBodyFileName = MultipartBody.Part.createFormData(this.file_name, fileName)
        val multiPartBodyLocation = MultipartBody.Part.createFormData(this.location, location)
        val multiPartBodyJamID = MultipartBody.Part.createFormData(this.jam_id, jamID)
        val multiPartBodyStartTime = MultipartBody.Part.createFormData(this.start_time, startTime)
        val multiPartBodyEndTime = MultipartBody.Part.createFormData(this.end_time, endTime)

        //return userService.uploadJam(multiPartBodyParams, multiPartBodyFile)
        return userService.uploadJam(multiPartBodyFile, multiPartBodyUserID, multiPartBodyFileName, multiPartBodyLocation, multiPartBodyJamID, multiPartBodyStartTime, multiPartBodyEndTime)
        //return userService.uploadJam(requestBody, fileBody)
    }

    fun callRegisterUser(activity: Activity): Call<ResponseModelKt.UserFunctions.RegisterUser> {
        checkFacebookID(activity)
        val facebookID = Profile.getCurrentProfile().id
        val accessToken = AccessToken.getCurrentAccessToken().token

        //val userService = RetrofitClient().getClient().create(ApiInterface::class.java)
        val params = ArrayMap<String, Any>()
        params[this.facebook_id] = facebookID
        params[this.access_token] = accessToken
        val requestBody = createRequestBody(params, jsonTypeStringForRequest)

        return userService.registerUser(requestBody)
    }

    private fun checkFacebookID(activity: Activity) {
        val facebookID = Profile.getCurrentProfile().id
        val accessToken = AccessToken.getCurrentAccessToken().token
        if (facebookID == null || accessToken == null) {
            MainRoutine().facebookAuthorize(activity)
            //activity.startActivity(Intent(activity, MainActivity::class.java))
        }
    }

    fun callGetUserActivity(context: Context): Call<ResponseModelKt.UserFunctions.GetUserActivity> {
        return userService.getUserActivity(PrefUtilsKt.Functions().retrieveUUID(context))
    }

    fun callGetUserActivityArray(context: Context): Call<ResponseModelKt.UserFunctions.GetUserActivityArray> {
        return userService.getUserActivityArray(PrefUtilsKt.Functions().retrieveUUID(context))
    }

    fun callCompressor(context: Context, jamID: String): Call<ResponseModelKt.CompressorFunctions.Compressor> {
        val uuid = PrefUtilsKt.Functions().retrieveUUID(context)
        //val jamID = PrefUtilsKt.Functions().retrieveJamID(context)
        val params = ArrayMap<String, Any>()
        params[this.user_id] = uuid
        params[this.jam_id] = jamID
        val requestBody = createRequestBody(params, jsonTypeStringForRequest)

        return userService.compress(requestBody)
    }

    private fun createRequestBody(params: ArrayMap<String, Any>, bodyType: String): RequestBody {
        return RequestBody.create(okhttp3.MediaType.parse(bodyType), JSONObject(params).toString())
    }

    private fun createMultipartBody(name: String, fileName: String, requestBody: RequestBody): MultipartBody.Part {
        return MultipartBody.Part.createFormData(name, fileName, requestBody)
    }

}