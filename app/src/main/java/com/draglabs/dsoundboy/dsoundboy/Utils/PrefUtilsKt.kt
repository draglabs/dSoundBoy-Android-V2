/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by davrukin on 1/3/2018.
 * @author Daniel Avrukin
 */
class PrefUtilsKt {

    class Functions {

        // TODO: GET JAM NAME FROM SERVER< AND CALL THE ONE IN THE RECYCLERVIEW THAT, LATER GET A LIST OF JAM NAMES AND USE THOSE FROM GETUSERACTIVITY

        /* JAM NAME FEATURES */

        fun storeJamName(context: Context, jamName: String) {
            Tools().storeItem(context, "jam_name", jamName)
        }

        fun deleteJamName(context: Context) {
            Tools().deleteItem(context, "jam_name")
        }

        fun retrieveJamName(context: Context): String {
            return Tools().retrieveItem(context, "jam_name")
        }

        /* PIN FUNCTIONS */

        fun storePIN(context: Context, pin: String) {
            Tools().storeItem(context, "pin", pin)
        }

        fun deletePIN(context: Context) {
            Tools().deleteItem(context, "pin")
        }

        fun retrievePIN(context: Context): String {
            return Tools().retrieveItem(context, "pin")
        }

        /* JAM ID FUNCTIONS */

        fun storeJamID(context: Context, pin: String) {
            Tools().storeItem(context, "jam_id", pin)
        }

        fun deleteJamID(context: Context) {
            Tools().deleteItem(context, "jam_id")
        }

        fun retrieveJamID(context: Context): String {
            return Tools().retrieveItem(context, "jam_id")
        }

        /* UUID FUNCTIONS */

        fun storeUUID(context: Context, UUID: String) {
            Tools().storeItem(context, "UUID", UUID)
        }

        fun retrieveUUID(context: Context): String {
            return Tools().retrieveItem(context, "UUID")
        }

        fun deleteUUID(context: Context) {
            Tools().deleteItem(context, "UUID")
        }

        /* LOCATION FUNCTIONS */

        fun storeLatitude(context: Context, latitude: String) {
            Tools().storeItem(context, "latitude", latitude)
        }

        fun retrieveLatitude(context: Context): String {
            return Tools().retrieveItem(context, "latitude")
        }

        fun deleteLatitude(context: Context) {
            Tools().deleteItem(context, "latitude")
        }

        fun storeLongitude(context: Context, longitude: String) {
            Tools().storeItem(context, "longitude", longitude)
        }

        fun retrieveLongitude(context: Context): String {
            return Tools().retrieveItem(context, "longitude")
        }

        fun deleteLongitude(context: Context) {
            Tools().deleteItem(context, "longitude")
        }

        fun dofun(function: String, itemType: String, item: String) {
            when (function) {
                "store" -> when (itemType) {
                    "jam_name" -> {

                    }
                    "pin" -> {

                    }
                    "jam_pin" -> {

                    }
                    "uuid" -> {

                    }
                    "lat" -> {

                    }
                    "lng" -> {

                    }
                    else -> {

                    }
                }
                "retrieve" -> when (itemType) {
                    "jam_name" -> {

                    }
                    "pin" -> {

                    }
                    "jam_pin" -> {

                    }
                    "uuid" -> {

                    }
                    "lat" -> {

                    }
                    "lng" -> {

                    }
                    else -> {

                    }
                }
                "delete" -> when (itemType) {
                    "jam_name" -> {

                    }
                    "pin" -> {

                    }
                    "jam_pin" -> {

                    }
                    "uuid" -> {

                    }
                    "lat" -> {

                    }
                    "lng" -> {

                    }
                    else -> {

                    }
                }
                else -> {

                }
            }
        }

    }

    private class Tools {
        fun storeItem(context: Context, tag: String, item: String) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(tag, item)
            editor.apply()
            LogUtils.debug("Stored item", "tag: $tag; item: $item")
        }

        fun deleteItem(context: Context, tag: String) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(tag, null)
            editor.apply()
            LogUtils.debug("Deleted item", "tag: $tag")

        }

        fun retrieveItem(context: Context, tag: String): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            LogUtils.debug("Retrieved item", "tag: $tag")
            return preferences.getString(tag, "not working")
        }
    }
}