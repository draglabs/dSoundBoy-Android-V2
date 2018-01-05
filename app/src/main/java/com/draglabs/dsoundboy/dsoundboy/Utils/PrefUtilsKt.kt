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
            LogUtils.debug("Deleted UUID", retrieveUUID(context))
        }
    }

    private class Tools {
        fun storeItem(context: Context, tag: String, item: String) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(tag, item)
            editor.apply()
        }

        fun deleteItem(context: Context, tag: String) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(tag, null)
            editor.apply()
        }

        fun retrieveItem(context: Context, tag: String): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(tag, "not working")
        }
    }
}