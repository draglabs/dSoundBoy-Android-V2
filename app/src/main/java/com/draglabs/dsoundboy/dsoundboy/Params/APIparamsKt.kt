/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Params

import android.util.ArrayMap
import com.draglabs.dsoundboy.dsoundboy.Interfaces.ApiInterface
import com.draglabs.dsoundboy.dsoundboy.Interfaces.RetrofitClient
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import com.facebook.AccessToken
import com.facebook.Profile
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call

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

    fun callNewJam(UUID: String, name: String, location: String, lat: Any, lng: Any, notes: String): Call<ResponseModelKt.JamFunctions.NewJam> {
        val params = ArrayMap<String, Any>()
        params.put("name", name)
        params.put("location", location)
        params.put("lat", lat)
        params.put("lng", lng)
        params.put("notes", notes)
        val requestBody = createRequestBody(params)

        return userService.newJam(UUID, requestBody)
    }

    fun callJoinJam(pin: String, UUID: String): Call<ResponseModelKt.JamFunctions.JoinJam> {
        val params = ArrayMap<String, Any>()
        params.put("pin", pin)
        params.put("user_id", UUID)
        val requestBody = createRequestBody(params)

        return userService.joinJam(requestBody)
    }

    fun callRegisterUser(): Call<ResponseModelKt.UserFunctions.RegisterUser> {
        val facebookID = Profile.getCurrentProfile().id
        val accessToken = AccessToken.getCurrentAccessToken().token

        //val userService = RetrofitClient().getClient().create(ApiInterface::class.java)
        val params = ArrayMap<String, Any>()
        params.put("facebook_id", facebookID)
        params.put("access_token", accessToken)
        val requestBody = createRequestBody(params)

        return userService.registerUser(requestBody)
    }

    private fun createRequestBody(params: ArrayMap<String, Any>): RequestBody {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), JSONObject(params).toString())
    }

}