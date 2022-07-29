/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.services

import android.annotation.TargetApi
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.activities.TestNavActivity
import com.draglabs.dsoundboy.dsoundboy.utils.LogUtils
import java.util.*

/**
 * Service to maintain background recording. Will show persistent notification while it occurs.
 * Created by davrukin on 3/7/2018.
 * @author Daniel Avrukin
 */
class AudioRecordingService : Service() {

    private val tag = "AudioRecordingService"
    var counter: Int = 0
    var oldTime: Long = 0

    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.info("Entering Function", "onStartCommand in $tag")
        super.onStartCommand(intent, flags, startId)
        showNotification(intent!!)
        startTimer()
        return START_STICKY
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun showNotification(intent: Intent) {
        val notificationIntent = Intent(this, TestNavActivity::class.java)
        // intent.putExtra(recorder, chronometer, etc)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        //val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, "")
            .setContentTitle("Digital Sound Boy")
            .setContentText("Currently Recording")
            .setSmallIcon(R.drawable.ic_logo)
            //.setContentIntent(pendingIntent) // currently disabled, meaning that user can't click app in notification drawer, so
                // user has to go back by going through task view
            .setTicker("Ticker Text")
            .build()

        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        LogUtils.info("Entering Function", "onDestroy in $tag")
        super.onDestroy()
        val broadcastIntent = Intent("com.draglabs.dsoundboy.dsoundboy.Services.RestartAudioRecordingService")
        sendBroadcast(broadcastIntent)
        stopForeground(true)
        stopTimerTask()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun startTimer() {
        timer = Timer()
        initializeTimerTask()
        timer.schedule(timerTask, 1000, 1000)
    }

    private fun initializeTimerTask() {
        timerTask = kotlin.concurrent.timerTask {
            LogUtils.info("In Timer", "In Timer ++++ ${counter++}")
        }
    }

    private fun stopTimerTask() {
        timer.cancel()
    }


}