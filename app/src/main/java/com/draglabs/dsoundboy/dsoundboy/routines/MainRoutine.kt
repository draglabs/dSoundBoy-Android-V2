package com.draglabs.dsoundboy.dsoundboy.routines

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_SIGNATURES
import android.util.Base64
import android.util.Log
import com.draglabs.dsoundboy.dsoundboy.utils.LogUtils
import com.facebook.FacebookSdk
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Created by davrukin on 11/1/17.
 * @author Daniel Avrukin
 */
class MainRoutine {

    /**
     * Authorizes with Facebook SDK
     */
    @Suppress("DEPRECATION")
    fun facebookAuthorize(activity: Context) {

        //FacebookSdk.setApplicationId(activity.getString(R.string.facebook_app_id))
        //FacebookSdk.sdkInitialize(activity)
        //AppEventsLogger.activateApp(activity)

        //if (BuildConfig.DEBUG) {
            try {
                val packageInfo = activity.packageManager.getPackageInfo("com.draglabs.dsoundboy.dsoundboy", GET_SIGNATURES)
                packageInfo.signatures.forEach { signature ->
                    val messageDigest = MessageDigest.getInstance("SHA")
                    messageDigest.update(signature.toByteArray())
                    LogUtils.debug("KeyHash: ", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT))
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        //}

        /*if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        }*/

        Log.v("App ID and App Name: ", FacebookSdk.getApplicationId() + "; " + FacebookSdk.getApplicationName())
    }
}