/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.google.gson.Gson
import org.joda.time.DateTime
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by davrukin on 1/8/2018.
 * @author Daniel Avrukin
 */
class FileUtils {

    private val recordingsDirectory = "/dSoundBoyRecordings/"
    private val rootPath = "${Environment.getExternalStorageDirectory()}" + recordingsDirectory
    private val extension = ".wav"

    fun generateAndSaveFilename(context: Context, date: Date): String {
        LogUtils.logEnteringFunction("generateAndSaveFilename")

        val folder = File(rootPath)
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        if (success) {
            LogUtils.debug("Folder Creation", "Success")
        } else {
            LogUtils.debug("Folder Creation", "Failure")
        }

        val name = getUsername(context) + " - " + getFormattedDate(date) + " <-> " + getJamName(context)
        val localPath = rootPath + name + extension

        PrefUtilsKt.Functions().storeLocalPath(context, localPath)
        return localPath
    }

    private fun getUsername(context: Context): String {
        LogUtils.logEnteringFunction("getUsername")
        return PrefUtilsKt.Functions().retrieveFbName(context)
    }

    //@SuppressLint("SimpleDateFormat")
    fun getTime(): String {
        val dateTime = DateTime()
        val month = dateTime.monthOfYear
        val day = dateTime.dayOfMonth
        val year = dateTime.year
        val hours = dateTime.hourOfDay
        val minutes = dateTime.minuteOfHour
        val seconds = dateTime.secondOfMinute

        //val pattern = "MM-dd-yyyy HH:mm"
        //val format = SimpleDateFormat(pattern, Locale.US)

        // return format.format(pattern)
        return "$year-$month-$day $hours:$minutes:$seconds"
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDate(date: Date): String {
        LogUtils.logEnteringFunction("getFormattedDate")

        val pattern = "yyyy-MM-dd HH:mm:ss.SS Z"
        val format = SimpleDateFormat(pattern)
        val formatted = format.format(date)
        LogUtils.debug("Formatted Date", formatted)
        return formatted
    }

    private fun getJamName(context: Context): String {
        LogUtils.logEnteringFunction("getJamName")

        return PrefUtilsKt.Functions().retrieveJamName(context)
    }

    fun serializeArrayList(list: ArrayList<JamViewModel>): String {
        return Gson().toJson(list)
    }

    fun stringToArrayList(string: String): ArrayList<JamViewModel> {
        val array = Gson().fromJson(string, JamViewModel::class.java)
        return Arrays.asList(array) as ArrayList<JamViewModel>
    }
}