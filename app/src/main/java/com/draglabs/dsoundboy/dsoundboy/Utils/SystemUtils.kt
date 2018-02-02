/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.preference.PreferenceManager

/**
 * Created by davrukin on 2/1/2018.
 * @author Daniel Avrukin
 */
object SystemUtils {

    object Settings {
        object Networking {
            fun getWifiOnlyUploadsSetting(context: Context): Boolean {
                val preference = PreferenceManager.getDefaultSharedPreferences(context)
                return preference.getBoolean("wifi_switch", false)
            }
        }
    }

    object Networking {
        object ConnectionStatus {
            fun isConnectedToInternet(context: Context): Boolean {
                val activeNetworkInfo = getActiveNetworkInfo(context)
                return (activeNetworkInfo != null) && (activeNetworkInfo.isConnectedOrConnecting)
            }

            fun ableToUpload(context: Context, wifiOnly: Boolean): Boolean {
                val isConnectedToInternet = isConnectedToInternet(context)
                val isConnectedToWifi = isConnectedToWifi(context)
                val isConnectedToMobile = isConnectedToMobile(context)

                if (wifiOnly) {
                    if (isConnectedToInternet && isConnectedToWifi) {
                        return true
                    } else if (isConnectedToInternet && isConnectedToMobile && !isConnectedToWifi) {
                        return false
                    } else {
                        return false
                    }
                } else {
                    return true
                }
            }

            fun isConnectedToWifi(context: Context): Boolean {
                val activeNetworkInfo = getActiveNetworkInfo(context)
                return activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
            }

            fun isConnectedToMobile(context: Context): Boolean {
                val activeNetworkInfo = getActiveNetworkInfo(context)
                return (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) ||
                        (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE_DUN) ||
                        (activeNetworkInfo.type == ConnectivityManager.TYPE_WIMAX)
            }

            private fun getActiveNetworkInfo(context: Context): NetworkInfo {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                return connectivityManager.activeNetworkInfo
            }
        }

    }



}