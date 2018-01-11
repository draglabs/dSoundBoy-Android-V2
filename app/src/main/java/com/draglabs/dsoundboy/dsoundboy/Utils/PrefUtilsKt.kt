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

    private object Vars {

        val fb_name             = StringsModelKt.FacebookUserDataParams.FB_NAME
        val fb_email            = StringsModelKt.FacebookUserDataParams.FB_EMAIL
        val fb_image            = StringsModelKt.FacebookUserDataParams.FB_IMAGE
        val local_path          = StringsModelKt.FacebookUserDataParams.LOCAL_PATH

        val jam_name            = StringsModelKt.JsonParsingKeys.JAM_NAME
        val pin                 = StringsModelKt.JsonParsingKeys.PIN
        val jam_id              = StringsModelKt.JsonParsingKeys.JAM_ID
        val uuid                = StringsModelKt.JsonParsingKeys.UUID
        val lat                 = StringsModelKt.JsonParsingKeys.LAT
        val lng                 = StringsModelKt.JsonParsingKeys.LNG
        val link                = StringsModelKt.JsonParsingKeys.LINK

        val jams                = StringsModelKt.ViewJamData.JAMS
    }

    class Functions {

        // TODO: GET JAM NAME FROM SERVER< AND CALL THE ONE IN THE RECYCLERVIEW THAT, LATER GET A LIST OF JAM NAMES AND USE THOSE FROM GETUSERACTIVITY

        /* JAM NAME FEATURES */

        fun storeJamName(context: Context, jamName: String) {
            Tools().storeItem(context, Vars.jam_name, jamName)
        }

        fun deleteJamName(context: Context) {
            Tools().deleteItem(context, Vars.jam_name)
        }

        fun retrieveJamName(context: Context): String {
            return Tools().retrieveItem(context, Vars.jam_name)
        }

        /* PIN FUNCTIONS */

        fun storePIN(context: Context, pin: String) {
            Tools().storeItem(context, Vars.pin, pin)
        }

        fun deletePIN(context: Context) {
            Tools().deleteItem(context, Vars.pin)
        }

        fun retrievePIN(context: Context): String {
            return Tools().retrieveItem(context, Vars.pin)
        }

        /* JAM ID FUNCTIONS */

        fun storeJamID(context: Context, jamID: String) {
            Tools().storeItem(context, Vars.jam_id, jamID)
        }

        fun deleteJamID(context: Context) {
            Tools().deleteItem(context, Vars.jam_id)
        }

        fun retrieveJamID(context: Context): String {
            return Tools().retrieveItem(context, Vars.jam_id)
        }

        /* UUID FUNCTIONS */

        fun storeUUID(context: Context, UUID: String) {
            Tools().storeItem(context, Vars.uuid, UUID)
        }

        fun retrieveUUID(context: Context): String {
            return Tools().retrieveItem(context, Vars.uuid)
        }

        fun deleteUUID(context: Context) {
            Tools().deleteItem(context, Vars.uuid)
        }

        /* LOCATION FUNCTIONS */

        fun storeLatitude(context: Context, lat: String) {
            Tools().storeItem(context, Vars.lat, lat)
        }

        fun retrieveLatitude(context: Context): String {
            return Tools().retrieveItem(context, Vars.lat)
        }

        fun deleteLatitude(context: Context) {
            Tools().deleteItem(context, Vars.lat)
        }

        fun storeLongitude(context: Context, lng: String) {
            Tools().storeItem(context, Vars.lng, lng)
        }

        fun retrieveLongitude(context: Context): String {
            return Tools().retrieveItem(context, Vars.lng)
        }

        fun deleteLongitude(context: Context) {
            Tools().deleteItem(context, Vars.lng)
        }

        /* LINK FUNCTIONS */

        fun storeLink(context: Context, link: String) {
            Tools().storeItem(context, Vars.link, link)
        }

        fun retrieveLink(context:Context): String {
            return Tools().retrieveItem(context, Vars.link)
        }

        fun deleteLink(context: Context) {
            Tools().deleteItem(context, Vars.link)
        }

        fun hasLink(context: Context): Boolean {
            val link = Tools().retrieveItem(context, Vars.link)
            return !(link == "not working" || link == "deleted")
        }

        /** FACEBOOK DATA FUNCTIONS */

        fun storeFbName(context: Context, fb_name: String) {
            Tools().storeItem(context, Vars.fb_name, fb_name)
        }

        fun retrieveFbName(context:Context): String {
            return Tools().retrieveItem(context, Vars.fb_name)
        }

        fun deleteFbName(context: Context) {
            Tools().deleteItem(context, Vars.fb_name)
        }

        fun storeFbEmail(context: Context, fb_email: String) {
            Tools().storeItem(context, Vars.fb_email, fb_email)
        }

        fun retrieveFbEmail(context:Context): String {
            return Tools().retrieveItem(context, Vars.fb_email)
        }

        fun deleteFbEmail(context: Context) {
            Tools().deleteItem(context, Vars.fb_email)
        }

        fun storeFbImage(context: Context, fb_image: String) {
            Tools().storeItem(context, Vars.fb_image, fb_image)
        }

        fun retrieveFbImage(context:Context): String {
            return Tools().retrieveItem(context, Vars.fb_image)
        }

        fun deleteFbImage(context: Context) {
            Tools().deleteItem(context, Vars.fb_image)
        }

        /* FILE FUNCTIONS */

        fun storeLocalPath(context: Context, localPath: String) {
            Tools().storeItem(context, Vars.local_path, localPath)
        }

        fun retrieveLocalPath(context:Context): String {
            return Tools().retrieveItem(context, Vars.local_path)
        }

        fun deleteLocalPath(context: Context) {
            Tools().deleteItem(context, Vars.local_path)
        }

        /* JAM VIEW FUNCTIONS */

        fun storeJams(context: Context, jams: String) {
            Tools().storeItem(context, Vars.jams, jams)
        }

        fun retrieveJams(context:Context): String {
            return Tools().retrieveItem(context, Vars.jams)
        }

        fun deleteJams(context: Context) {
            Tools().deleteItem(context, Vars.jams)
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
            val oldItem = preferences.getString(tag, "not working")
            editor.putString(tag, "deleted")
            editor.apply()
            LogUtils.debug("Deleted item", "tag: $tag; old item: $oldItem")

        }

        fun retrieveItem(context: Context, tag: String): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val item = preferences.getString(tag, "not working")
            LogUtils.debug("Retrieved item", "tag: $tag; item: $item")
            return item
        }
    }
}