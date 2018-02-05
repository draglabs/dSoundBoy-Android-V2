/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Tasks

import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import com.draglabs.dsoundboy.dsoundboy.Activities.TestNavActivity
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.RecorderUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.SystemUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.experimental.delay
import omrecorder.Recorder
import java.io.File
import java.util.*

/**
 * Created by davrukin on 2/1/2018.
 * @author Daniel Avrukin
 */
class OfflineUploader {

    // add a flag to each file, says if boolean was uploaded or not
    // api call says that if there is a link, then it has been uploaded
    // before uploading the recording on clicking stop, check the setting
        // if it's offline-only, defer the file and add it to a list of files to be uploaded later

    // upon opening the testnavactivity, check the setting
        // if wifi-only is off
            // check wifi status to make sure it's on, then upload
            // read text file of files
            // if there are any to upload
                // do that in kotlin suspend/async
            // otherwise
        // if wifi-only is on
            // save recording, write the filename/filepath to a text file, and don't upload
            // upload only if wifi is on

    // instead of two text files, there are two methods
        // create json and parse with gson where each file is an element in an array
            // filename and tag
        // *map<k,v> in sharedpreferences or even in realm that's read, modified, and re-written
            // potential problem: if user uninstalls app and has recordings not uploaded, the
            // sharedpreferences won't know which ones were and weren't
            // therefore should include processing with api call to see which recordings (getrecordings)
            // and address the non-uploaded ones by recording id to re-upload them by filename
            // a more complex but also a more robust method

    suspend fun prepareUpload(context: Context, view: View, recorder: Recorder, stop: (Context, View, Recorder) -> (Unit)) {
        delay(250)
        LogUtils.debug("Preparing Upload", "Checking if Ready")
        val readyForUpload = SystemUtils.Networking.ConnectionStatus.ableToUpload(context)
        LogUtils.debug("Ready for Upload", "$readyForUpload")

        //clickStop.run {  }
        stop.invoke(context, view, recorder)


        if (readyForUpload) {
            // upload current recording {async}
            // read through json file to upload deferred files {async}
                // on success of all, clear the file
        } else {
            // defer current recording by appending to file {async}
        }
        // send notification for each action
            // say how many recordings are being deferred and how many are being uploaded
                // show a finite notification progress bar as each one goes through
        // performClickStop here
    }

    private fun doStop(context: Context, jamPinView: Button, recorder: Recorder, recorderUtils: RecorderUtils, view: View, chronometer: Chronometer, startTime: Date, endTime: Date) {
        updatePinView(context, jamPinView)

        clickStop(recorder, recorderUtils, context, view, chronometer, startTime, endTime)

        updatePinView(context, jamPinView)
    }

    private fun clickStop(recorder: Recorder, recorderUtils: RecorderUtils, context: Context, view: View, chronometer: Chronometer, startTime: Date, endTime: Date) {

    }

    private fun updatePinView(context: Context, jamPinView: Button) {

    }

    private object FileUtils {
        fun createFile(): File {
            val rootPath = "${Environment.getExternalStorageDirectory()}/dSoundBoyRecordings"
            val filename = "dSoundBoy Wifi Uploads.json"
            val path = "$rootPath/$filename"
            LogUtils.debug("Path to Uploads", path)

            return File(path)
        }

        fun readFile(): File {
            val rootPath = "${Environment.getExternalStorageDirectory()}/dSoundBoyRecordings"
            val filename = "dSoundBoy Wifi Uploads.json"
            val path = "$rootPath/$filename"
            LogUtils.debug("Path to Uploads", path)

            val file = File(path)

            if (!file.exists()) {
                return createFile()
            } else {
                return file
            }
        }

        fun writeFile(string: String, file: File) {
            file.writeText(string) // over-writes the file
        }

        fun modifyFile(string: String, file: File) {
            file.appendText(string) // appends lines to the file
        }

        fun addFileToUpload(filePath: String, jamID: String, startTime: String, endTime: String, arrayOfFiles: JsonArray): JsonArray {
            // [recording file name, jam id]
            val jsonObject = JsonObject()
            jsonObject.addProperty("file_path", filePath)
            jsonObject.addProperty("jam_id", jamID)
            jsonObject.addProperty("start_time", startTime)
            jsonObject.addProperty("end_time", endTime)
            arrayOfFiles.add(jsonObject)
            LogUtils.debug("Added File", "$jsonObject")
            LogUtils.debug("Array of Files", "$arrayOfFiles")
            return arrayOfFiles
        }

        fun constructJson(): JsonObject {
            return JsonObject()
        }

        fun convertJsonObjectToString(jsonObject: JsonObject): String {
            return jsonObject.toString()
        }

        fun clearFile(file: File) {
            file.writeText("")
        }
    }
}