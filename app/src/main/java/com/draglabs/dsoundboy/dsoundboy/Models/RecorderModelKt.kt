/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Models

import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.support.annotation.RequiresApi
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import java.util.*

/**
 * The recording settings for the audio recorder
 * Created by davrukin on 1/7/17.
 * @author Daniel Avrukin
 */

class RecorderModelKt {

    var startTime: Date? = null
    var endTime: Date? = null
    var duration: Long = 0
        get() {
            if (field > 0) {
                return field
            } else if (startTime != null && endTime != null) {
                this.duration = endTime!!.time - startTime!!.time
                return field
            } else {
                return 0
            }
        }
    var audioSavePathInDevice: String? = null
    var mediaRecorder: MediaRecorder? = null
    var random: Random? = null
    var bandData: Array<String>? = null
    var audioRecord: AudioRecord? = null
    var isRecording = false
    val extension = ".aac"
    var pathname: String? = null
    val RANDOM_AUDIO_FILE_NAME: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val bufferElementsToRec: Int = 1024
    val bytesPerElement: Int = 2

    var recorderAudioSource: Int = 0
    var recorderOutputFormat: Int = 0
    var recordingEncodingBitRate: Int = 0
    var recordingSampleRate: Int = 0
    var recordingChannels: Int = 0

    @RequiresApi(LOLLIPOP)
    var recordingAudioEncoding: Int = 0

    constructor() {
        startTime = null
        endTime = null
        random = Random()
        recorderAudioSource = MediaRecorder.AudioSource.MIC
        recorderOutputFormat = MediaRecorder.OutputFormat.MPEG_4
        recordingEncodingBitRate = 16
        recordingSampleRate = 44100
        recordingChannels = 1
        recordingAudioEncoding = MediaRecorder.AudioEncoder.AAC
        //this.mediaRecorder = mediaRecorderReady();
    }

    constructor(audioSavePathInDevice: String, bandData: Array<String>, audioRecord: AudioRecord, pathname: String) {
        this.startTime = null
        this.endTime = null
        this.audioSavePathInDevice = audioSavePathInDevice
        this.random = Random()
        this.bandData = bandData
        this.recorderAudioSource = MediaRecorder.AudioSource.MIC
        this.recorderOutputFormat = MediaRecorder.OutputFormat.MPEG_4
        this.recordingEncodingBitRate = 24 // was 192000
        this.recordingSampleRate = 44100
        this.recordingChannels = 1
        this.recordingAudioEncoding = MediaRecorder.AudioEncoder.AAC
        this.audioRecord = audioRecord
        this.pathname = pathname
        this.mediaRecorder = mediaRecorderReady(recorderAudioSource, recorderOutputFormat, recordingEncodingBitRate,
                recordingSampleRate, recordingChannels, recordingAudioEncoding)
    }

    fun setRecorderOptions(recorderAudioSource: Int, recorderOutputFormat: Int, recordingEncodingBitRate: Int,
                           recordingSampleRate: Int, recordingChannels: Int, recordingAudioEncoding: Int) {
        this.recorderAudioSource = recorderAudioSource
        this.recorderOutputFormat = recorderOutputFormat
        this.recordingEncodingBitRate = recordingEncodingBitRate
        this.recordingSampleRate = recordingSampleRate
        this.recordingChannels = recordingChannels
        this.recordingAudioEncoding = recordingAudioEncoding
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun mediaRecorderReady(): MediaRecorder {
        this.mediaRecorder = MediaRecorder()

        mediaRecorder!!.setAudioSource(recorderAudioSource)
        mediaRecorder!!.setOutputFormat(recorderOutputFormat)
        mediaRecorder!!.setAudioEncoder(recordingAudioEncoding)
        mediaRecorder!!.setAudioChannels(recordingChannels)
        mediaRecorder!!.setAudioEncodingBitRate(recordingEncodingBitRate)
        mediaRecorder!!.setAudioSamplingRate(recordingSampleRate)
        mediaRecorder!!.setOutputFile(audioSavePathInDevice)
        LogUtils.debug("recorderAudioSource: ", "" + recorderAudioSource)
        LogUtils.debug("recorderOutputFormat: ", "" + recorderOutputFormat)
        LogUtils.debug("recorderAudioEncoding: ", "" + recordingAudioEncoding)
        LogUtils.debug("recordingChannels: ", "" + recordingChannels)
        LogUtils.debug("recorderEncodingBtRte: ", "" + recordingEncodingBitRate)
        LogUtils.debug("recordingSampleRate: ", "" + recordingSampleRate)
        LogUtils.debug("audioSavePathInDev: ", "" + audioSavePathInDevice!!)

        return mediaRecorder as MediaRecorder
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun mediaRecorderReady(recorderAudioSource: Int, recorderOutputFormat: Int, recordingEncodingBitRate: Int,
                                   recordingSampleRate: Int, recordingChannels: Int, recordingAudioEncoding: Int): MediaRecorder {

        mediaRecorder = MediaRecorder()

        mediaRecorder!!.setAudioSource(recorderAudioSource)
        mediaRecorder!!.setOutputFormat(recorderOutputFormat)
        mediaRecorder!!.setAudioEncoder(recordingAudioEncoding)
        mediaRecorder!!.setAudioChannels(recordingChannels)
        mediaRecorder!!.setAudioEncodingBitRate(recordingEncodingBitRate)
        mediaRecorder!!.setAudioSamplingRate(recordingSampleRate)
        mediaRecorder!!.setOutputFile(audioSavePathInDevice)

        return mediaRecorder as MediaRecorder
    }
}
