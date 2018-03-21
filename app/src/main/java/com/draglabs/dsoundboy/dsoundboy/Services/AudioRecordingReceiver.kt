/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils

/**
 * Receives action when recording is stopped or changed.
 * Created by davrukin on 3/7/2018.
 * @author Daniel Avrukin
 */
class AudioRecordingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtils.info("AudioRecordingReceiver", "Service Stopped")
        context!!.startService(Intent(context, AudioRecordingService::class.java))
    }


}