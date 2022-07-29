/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2017. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.utils

import android.content.Context
import android.util.Log

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
    fun debug(tag: String, message: String?) {
        //if (BuildConfig.DEBUG) {
            Log.d(tag, message ?: "\null")
        //}
    }

    fun verbose(tag: String, message: String?) {
        //if (BuildConfig.DEBUG) {
            Log.v(tag, message ?: "\null")
        //}
    }

    fun info(tag: String, message: String?) {
        //if (BuildConfig.DEBUG) {
            Log.i(tag, message ?: "\null")
        //}
    }

    fun warn(tag: String, message: String?) {
        //if (BuildConfig.DEBUG) {
            Log.w(tag, message ?: "\null")
        //}
    }

    fun wtf(tag: String, message: String?) {
        //if (BuildConfig.DEBUG) {
            Log.wtf(tag, message ?: "\null")
        //}
    }

    fun error(tag: String, message: String?) {
        //if (BuildConfig.DEBUG) {
            Log.e(tag, message ?: "\null")
        //}
    }

    object Types {

    }

    fun logSuccessResponse(tag: String, statusCode: Int, headers: Array<Header>, response: JSONArray) {
        /*info("Status Code: ", statusCode.toString() + "")
        info("Headers: ", Arrays.toString(headers))
        info("Response: ", response.toString())*/
        info("$tag Success Response", "Status Code: $statusCode\nHeaders: $headers\nResponse: $response")
    }

    fun logSuccessResponse(tag: String, statusCode: Int, headers: Array<Header>, response: JSONObject) {
        /*info("Status Code: ", statusCode.toString() + "")
        info("Headers: ", Arrays.toString(headers))
        info("Response: ", response.toString())*/
        info("$tag Success Response", "Status Code: $statusCode\nHeaders: $headers\nResponse: $response")
    }

    fun logSuccessResponse(tag: String, code: String, body: String, message: String) {

    }

    fun logFailureResponse(statusCode: Int, headers: Array<Header>, throwable: Throwable, response: JSONObject) {
        if (headers != null && throwable != null && response != null) {
            error("Status Code: ", "" + statusCode)
            error("Headers: ", Arrays.toString(headers) + "")
            error("Throwable: ", throwable.message!!)
            error("Response: ", response.toString())
        } else {
            error("Reason: ", "Other Failure.")
        }
    }

    fun logFailureResponse(tag: String, errorBody: String, code: Int, message: String, headers: String) {
        val failedResponseMessage = "Response: $errorBody\nCode: $code\nMessage: $message\nHeaders: $headers"
        error("$tag Failed Response", failedResponseMessage)
    }

    fun logOnFailure(t: Throwable) {
        error("onFailure Failed Message", t.message.toString())
        error("onFailure Failed Cause", t.cause.toString())
        error("onFailure Failed StackTrace", t.printStackTrace().toString())
    }

    fun logAppSettings(context: Context) {
        val wifiOnlyUploads = SystemUtils.Settings.Networking.getWifiOnlyUploadsSetting(context)
        debug("App Settings", "Printed here")
        debug("Wifi-Only Uploads", "$wifiOnlyUploads")
    }

    fun logEnteringFunction(functionName: String) {
        info("Entering Function", functionName)
    }

    /*fun logModel(action: String, model: String, vararg: String) {
        info("$action $model", "")
    }*/
}
