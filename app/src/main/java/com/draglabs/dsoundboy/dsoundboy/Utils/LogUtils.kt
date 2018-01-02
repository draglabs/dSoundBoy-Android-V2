/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2017. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.util.Log

import com.draglabs.dsoundboy.dsoundboy.BuildConfig

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

}
