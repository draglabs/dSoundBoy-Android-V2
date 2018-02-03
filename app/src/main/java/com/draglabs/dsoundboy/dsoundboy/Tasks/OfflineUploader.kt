/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Tasks

import android.content.Context
import android.os.Environment
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.SystemUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File

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

    suspend fun prepareUpload(context: Context) {
        val readyForUpload = SystemUtils.Networking.ConnectionStatus.ableToUpload(context)
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

        fun addFileToUpload(filePath: String, jamID: String, arrayOfFiles: JsonArray): JsonArray {
            // [recording file name, jam id]
            val jsonObject = JsonObject()
            jsonObject.addProperty(filePath, jamID)
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