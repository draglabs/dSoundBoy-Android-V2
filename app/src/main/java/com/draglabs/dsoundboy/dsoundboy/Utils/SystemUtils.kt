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

            fun checkNetworkStatus(context: Context) {
                val isConnectedToInternet = isConnectedToInternet(context)
                val isConnectedToWifi = isConnectedToWifi(context)
                val isConnectedToMobile = isConnectedToMobile(context)

                LogUtils.debug("Connection Status", "Connected to Internet: $isConnectedToInternet\nConnected to Wifi: $isConnectedToWifi\nConnected to Mobile: $isConnectedToMobile")
            }

            fun ableToUpload(context: Context): Boolean {
                val wifiOnly = Settings.Networking.getWifiOnlyUploadsSetting(context)

                val isConnectedToInternet = isConnectedToInternet(context)
                val isConnectedToWifi = isConnectedToWifi(context)
                val isConnectedToMobile = isConnectedToMobile(context)

                LogUtils.debug("Connection Status", "Connected to Internet: $isConnectedToInternet\nConnected to Wifi: $isConnectedToWifi\nConnected to Mobile: $isConnectedToMobile")

                if (wifiOnly) {
                    if (isConnectedToInternet && isConnectedToWifi) {
                        LogUtils.debug("Able to Upload", "true")
                        return true
                    } else if (isConnectedToInternet && isConnectedToMobile && !isConnectedToWifi) {
                        LogUtils.debug("Able to Upload", "false")
                        return false
                    } else {
                        LogUtils.debug("Able to Upload", "false")
                        return false
                    }
                } else {
                    LogUtils.debug("Able to Upload", "true")
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