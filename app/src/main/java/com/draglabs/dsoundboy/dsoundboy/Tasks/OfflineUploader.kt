/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Tasks

import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import com.draglabs.dsoundboy.dsoundboy.Routines.HomeRoutineKt
import com.draglabs.dsoundboy.dsoundboy.Utils.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.realm.Realm
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import omrecorder.Recorder
import java.io.File
import java.io.FileReader
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

    suspend fun addRecordingToQueue(realm: Realm, filepath: String, jamID: String, jamName: String, startTime: String, endTime: String) {
        delay(0)
        RealmUtils.RecordingModelUtils.Store.storeRecordingModel(realm, filepath, jamID, jamName, startTime, endTime, didUpload = false)
    }

    suspend fun queueInteractor(context: Context, realm: Realm) {
        delay(0)
        // check readiness
        LogUtils.debug("Preparing Upload", "Checking if Ready")
        val readyForUpload = SystemUtils.Networking.ConnectionStatus.ableToUpload(context)
        LogUtils.debug("Ready for Upload", "$readyForUpload")

        if (readyForUpload) {
            val queue = RealmUtils.RecordingModelUtils.Retrieve.retrieveRecordingModelByUploadStatus(realm, false)
            val length = queue.size
            var index = 0
            for (item in queue) {
                doDeferredUpload(realm, context, item.filePath, item.jamName, item.jamID, item.startTime, item.endTime)
                LogUtils.debug("Queue Uploader", "$index of $length")
                index++
            }
        }
        // go through queue of pending uploads
        // uploads a file
        // once it's been uploaded, mark it as uploaded
            // once uploaded, gets automatically deleted
                // delete local file and realm record
        // repeat
    }

    suspend fun prepareUpload(realm: Realm, jamID: String, context: Context, jamPinView: Button, recorder: Recorder, recorderUtils: RecorderUtils, view: View, chronometer: Chronometer, startTime: Date, endTime: Date) {
        delay(250)
        LogUtils.debug("Preparing Upload", "Checking if Ready")
        val readyForUpload = SystemUtils.Networking.ConnectionStatus.ableToUpload(context)
        LogUtils.debug("Ready for Upload", "$readyForUpload")

        //doInitialUpload(jamID, context, jamPinView, recorder, recorderUtils, view, chronometer, startTime, endTime) // uploads the current recording that just ended

        if (readyForUpload) {
            //val jamID = PrefUtilsKt.Functions().retrieveJamID(context) // current jam id
            async { doInitialUpload(realm, jamID, context, jamPinView, recorder, recorderUtils, view, chronometer, startTime, endTime) }
            // upload current recording {async}
            val file = FileUtils.readFile()
            val jsonArray = FileUtils.readLines(file) // TODO: essentially building up a queue
            if (!jsonArray.isJsonNull) {
                for (element in jsonArray) {
                    val jsonObject = element.asJsonObject
                    val jFilePath = jsonObject["file_path"].asString
                    val jJamID = jsonObject["jam_id"].asString
                    val jJamName = jsonObject["jam_name"].asString
                    val jStartTime = jsonObject["start_time"].asString
                    val jEndTime = jsonObject["end_time"].asString
                    val jDidUpload = jsonObject["did_upload"].asBoolean

                    async { doDeferredUpload(realm, context, jFilePath, jJamName, jJamID, jStartTime, jEndTime) }
                }
            }
            // read through json file to upload deferred files {async}
                // on success of all, clear the file
        } else {
            // defer current recording by appending to file {async}
            val file = FileUtils.readFile()
            if (file.length() > 0) {
                val array = JsonArray()
                FileUtils.appendArrayToFile(array, file)
            }
        }
        // send notification for each action
            // say how many recordings are being deferred and how many are being uploaded
                // show a finite notification progress bar as each one goes through
        // performClickStop here
    }

    // read settings
        // file -> json
    // {operate on settings}
    // write settings

    private suspend fun doInitialUpload(realm: Realm, jamID: String, context: Context, jamPinView: Button, recorder: Recorder, recorderUtils: RecorderUtils, view: View, chronometer: Chronometer, startTime: Date, endTime: Date) {
        updatePinView(context, jamPinView)

        clickStop(realm, jamID, recorder, recorderUtils, context, view, chronometer, startTime, endTime)

        updatePinView(context, jamPinView)
    }

    private suspend fun doDeferredUpload(realm: Realm, context: Context, recordingPath: String, jamName: String, jamID: String, startTime: String, endTime: String) {
        val uuid = PrefUtilsKt.Functions().retrieveUUID(context)
        APIutilsKt().performUploadJam(realm, context, recordingPath, uuid, jamName, "location", jamID, startTime, endTime) // may have to switch functions?
        //APIutilsKt.JamFunctions.jamRecordingUpload(context, recordingPath, "hi", startTime.toString(), endTime.toString(), view)
        // TODO: success response from server makes didUpload true
        // TODO: once uploaded, file gets deleted locally
        // TODO: if file doesn't exist anymore, then didUpload is set to true
    }

    private fun clickStop(realm: Realm, jamID: String, recorder: Recorder, recorderUtils: RecorderUtils, context: Context, view: View, chronometer: Chronometer, startTime: Date, endTime: Date) {
        HomeRoutineKt().clickStop(realm, jamID, recorder, recorderUtils, context, view, chronometer, startTime, endTime)
    }

    private fun updatePinView(context: Context, jamPinView: Button) {
        APIutilsKt.JamFunctions.performGetActiveJam(context)
        val activeJamPIN = PrefUtilsKt.Functions().retrievePIN(context)

        if (activeJamPIN == "not working") {
            jamPinView.text = "No Jam PIN"
            Toast.makeText(context, "No current jam. Please click either New or Join to get a PIN.", Toast.LENGTH_LONG).show()
        } else {
            jamPinView.text = activeJamPIN
        }
    }

    private object FileUtils {
        fun createFile(): File {
            val rootPath = "${Environment.getExternalStorageDirectory()}/dSoundBoyRecordings"
            val filename = "dSoundBoy Wifi Uploads.json"
            val path = "$rootPath/$filename"
            LogUtils.debug("Path to Uploads", path)

            val file = File(path)
            file.createNewFile()
            file.setWritable(true)
            file.setReadable(true)

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

        fun readLines(file: File): JsonArray {
            val parser = JsonParser()
            val element = parser.parse(FileReader(file))
            return element.asJsonArray
        }

        fun writeFile(string: String, file: File) {
            file.writeText(string) // over-writes the file
        }

        fun modifyFile(string: String, file: File) {
            file.appendText(string) // appends lines to the file
        }

        fun addRecordingToArray(filePath: String, jamID: String, jamName: String, startTime: String, endTime: String, didUpload: Boolean, arrayOfFiles: JsonArray): JsonArray {
            // [recording file name, jam id]
            val jsonObject = JsonObject()
            jsonObject.addProperty("file_path", filePath)
            jsonObject.addProperty("jam_id", jamID)
            jsonObject.addProperty("jam_name", jamName)
            jsonObject.addProperty("start_time", startTime)
            jsonObject.addProperty("end_time", endTime)
            jsonObject.addProperty("did_upload", didUpload)
            arrayOfFiles.add(jsonObject)
            LogUtils.debug("Added File", "$jsonObject")
            LogUtils.debug("Array of Files", "$arrayOfFiles")
            return arrayOfFiles
        }

        /**
         * Over-write
         */
        fun writeArrayToFile(jsonArray: JsonArray, file: File): File { // over-write
            val string = jsonArray.toString()
            writeFile(string, file)
            return file
        }

        /**
         * Append
         */
        fun appendArrayToFile(jsonArray: JsonArray, file: File): File { // append
            val string = jsonArray.toString()
            modifyFile(string, file)
            return file
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