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

    fun storeUUID(context: Context, UUID: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("UUID", UUID)
        editor.apply()
    }

    fun retrieveUUID(context: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("UUID", "not working")
    }

    fun deleteUUID(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("UUID", null)
        editor.apply()
        LogUtils.debug("Deleted UUID", retrieveUUID(context))
    }
}