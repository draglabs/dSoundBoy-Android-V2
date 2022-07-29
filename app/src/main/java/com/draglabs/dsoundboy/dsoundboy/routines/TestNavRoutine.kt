/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.routines

import android.app.ActivityManager
import android.content.Context
import com.draglabs.dsoundboy.dsoundboy.services.AudioRecordingService
import com.draglabs.dsoundboy.dsoundboy.utils.LogUtils

/**
 * Contains functions that run in TestNavActivity
 * Created by davrukin on 3/7/2018.
 * @author Daniel Avrukin
 */
class TestNavRoutine {

    fun isServiceRunning(serviceClass: Class<AudioRecordingService>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                LogUtils.info("isServiceRunning", "true")
                return true
            }
        }
        LogUtils.info("isServiceRunning", "false")
        return false
    }

}