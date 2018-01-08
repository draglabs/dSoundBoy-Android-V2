/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import android.preference.PreferenceManager
import com.draglabs.dsoundboy.dsoundboy.Models.StringsModelKt

/**
 * Created by davrukin on 1/3/2018.
 * @author Daniel Avrukin
 */
class PrefUtilsKt {

    class Functions {

        private val jam_name    = StringsModelKt.JsonParsingKeys.JAM_NAME
        private val pin         = StringsModelKt.JsonParsingKeys.PIN
        private val jam_id      = StringsModelKt.JsonParsingKeys.JAM_ID
        private val uuid        = StringsModelKt.JsonParsingKeys.UUID
        private val lat         = StringsModelKt.JsonParsingKeys.LAT
        private val lng         = StringsModelKt.JsonParsingKeys.LNG
        private val link        = StringsModelKt.JsonParsingKeys.LINK


        // TODO: GET JAM NAME FROM SERVER< AND CALL THE ONE IN THE RECYCLERVIEW THAT, LATER GET A LIST OF JAM NAMES AND USE THOSE FROM GETUSERACTIVITY

        /* JAM NAME FEATURES */

        fun storeJamName(context: Context, jamName: String) {
            Tools().storeItem(context, jam_name, jamName)
        }

        fun deleteJamName(context: Context) {
            Tools().deleteItem(context, jam_name)
        }

        fun retrieveJamName(context: Context): String {
            return Tools().retrieveItem(context, jam_name)
        }

        /* PIN FUNCTIONS */

        fun storePIN(context: Context, pin: String) {
            Tools().storeItem(context, this.pin, pin)
        }

        fun deletePIN(context: Context) {
            Tools().deleteItem(context, pin)
        }

        fun retrievePIN(context: Context): String {
            return Tools().retrieveItem(context, pin)
        }

        /* JAM ID FUNCTIONS */

        fun storeJamID(context: Context, jamID: String) {
            Tools().storeItem(context, jam_id, jamID)
        }

        fun deleteJamID(context: Context) {
            Tools().deleteItem(context, jam_id)
        }

        fun retrieveJamID(context: Context): String {
            return Tools().retrieveItem(context, jam_id)
        }

        /* UUID FUNCTIONS */

        fun storeUUID(context: Context, UUID: String) {
            Tools().storeItem(context, uuid, UUID)
        }

        fun retrieveUUID(context: Context): String {
            return Tools().retrieveItem(context, uuid)
        }

        fun deleteUUID(context: Context) {
            Tools().deleteItem(context, uuid)
        }

        /* LOCATION FUNCTIONS */

        fun storeLatitude(context: Context, lat: String) {
            Tools().storeItem(context, this.lat, lat)
        }

        fun retrieveLatitude(context: Context): String {
            return Tools().retrieveItem(context, lat)
        }

        fun deleteLatitude(context: Context) {
            Tools().deleteItem(context, lat)
        }

        fun storeLongitude(context: Context, lng: String) {
            Tools().storeItem(context, this.lng, lng)
        }

        fun retrieveLongitude(context: Context): String {
            return Tools().retrieveItem(context, lng)
        }

        fun deleteLongitude(context: Context) {
            Tools().deleteItem(context, lng)
        }

        /* LINK FUNCTIONS */

        fun storeLink(context: Context, link: String) {
            Tools().storeItem(context, this.link, link)
        }

        fun retrieveLink(context:Context): String {
            return Tools().retrieveItem(context, link)
        }

        fun deleteLink(context: Context) {
            Tools().deleteItem(context, link)
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