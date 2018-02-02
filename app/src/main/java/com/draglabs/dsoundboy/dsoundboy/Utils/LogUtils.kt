/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2017. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import android.util.Log

import com.draglabs.dsoundboy.dsoundboy.BuildConfig
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by davrukin on 12/30/2017.
 * Tools for debugging, mainly so the debug messages aren't seen in Release config
 * https://medium.com/@elye.project/debug-messages-your-responsible-to-clear-it-before-release-1a0f872d66f
 */

object LogUtils {

    /**
     * Doesn't log to console if release mode
     */
    fun debug(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun logSuccessResponse(statusCode: Int, headers: Array<Header>, response: JSONArray) {
        LogUtils.debug("Status Code: ", statusCode.toString() + "")
        LogUtils.debug("Headers: ", Arrays.toString(headers))
        LogUtils.debug("Response: ", response.toString())
    }

    fun logSuccessResponse(statusCode: Int, headers: Array<Header>, response: JSONObject) {
        LogUtils.debug("Status Code: ", statusCode.toString() + "")
        LogUtils.debug("Headers: ", Arrays.toString(headers))
        LogUtils.debug("Response: ", response.toString())
    }

    fun logFailureResponse(statusCode: Int, headers: Array<Header>, throwable: Throwable, response: JSONObject) {
        if (headers != null && throwable != null && response != null) {
            Log.v("Status Code: ", "" + statusCode)
            Log.v("Headers: ", Arrays.toString(headers) + "")
            Log.v("Throwable: ", throwable.message)
            Log.v("Response: ", response.toString())
        } else {
            Log.v("Reason: ", "Other Failure.")
        }
    }

    fun logOnFailure(t: Throwable) {
        LogUtils.debug("onFailure Failed Message", t.message.toString())
        LogUtils.debug("onFailure Failed Cause", t.cause.toString())
        LogUtils.debug("onFailure Failed StackTrace", t.printStackTrace().toString())
    }

    fun logAppSettings(context: Context) {
        val wifiOnlyUploads = SystemUtils.Settings.Networking.getWifiOnlyUploadsSetting(context)
        LogUtils.debug("App Settings", "Printed here")
        LogUtils.debug("Wifi-Only Uploads", "$wifiOnlyUploads")
    }

}
