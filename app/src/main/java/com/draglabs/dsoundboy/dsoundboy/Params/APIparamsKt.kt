/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Params

import android.util.ArrayMap
import com.draglabs.dsoundboy.dsoundboy.Interfaces.ApiInterface
import com.draglabs.dsoundboy.dsoundboy.Interfaces.RetrofitClient
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import com.draglabs.dsoundboy.dsoundboy.Models.StringsModelKt
import com.facebook.AccessToken
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

    fun callNewJam(UUID: String, name: String, location: String, lat: Any, lng: Any, notes: String): Call<ResponseModelKt.JamFunctions.NewJam> {
        val params = ArrayMap<String, Any>()
        params.put(this.name, name)
        params.put(this.location, location)
        params.put(this.lat, lat)
        params.put(this.lng, lng)
        params.put(this.notes, notes)
        val requestBody = createRequestBody(params)

        return userService.newJam(UUID, requestBody)
    }

    fun callJoinJam(pin: String, UUID: String): Call<ResponseModelKt.JamFunctions.JoinJam> {
        val params = ArrayMap<String, Any>()
        params.put(this.pin, pin)
        params.put(this.user_id, UUID)
        val requestBody = createRequestBody(params)

        return userService.joinJam(requestBody)
    }

    fun callUploadJam(filePath: String, userID: String, fileName: String, location: String, jamID: String,  startTime: String, endTime: String): Call<ResponseModelKt.JamFunctions.UploadJam> {
        val params = ArrayMap<String, Any>()
        params.put(this.user_id, userID)
        params.put(this.file_name, fileName)
        params.put(this.location, location)
        params.put(this.jam_id, jamID)
        params.put(this.start_time, startTime)
        params.put(this.end_time, endTime)
        val requestBody = createRequestBody(params)

        val file = File(filePath)
        val fileBody = MultipartBody.Part.createFormData("file", file.name, RequestBody.create(MediaType.parse("audio/*"), file))

        return userService.uploadJam(requestBody, fileBody)
    }

    fun callRegisterUser(): Call<ResponseModelKt.UserFunctions.RegisterUser> {
        val facebookID = Profile.getCurrentProfile().id
        val accessToken = AccessToken.getCurrentAccessToken().token

        //val userService = RetrofitClient().getClient().create(ApiInterface::class.java)
        val params = ArrayMap<String, Any>()
        params.put(this.facebook_id, facebookID)
        params.put(this.access_token, accessToken)
        val requestBody = createRequestBody(params)

        return userService.registerUser(requestBody)
    }

    private fun createRequestBody(params: ArrayMap<String, Any>): RequestBody {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), JSONObject(params).toString())
    }

}