/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.models

import com.draglabs.dsoundboy.dsoundboy.models.StringsModelKt.ApiRootPaths.JAM
import com.draglabs.dsoundboy.dsoundboy.models.StringsModelKt.ApiRootPaths.USER
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Holds strings used throughout the program, separate from the XML strings file
 * Created by davrukin on 1/7/2018.
 * @author Daniel Avrukin
 */
object StringsModelKt {

    private const val deprecationString = "No longer used in the API"

    object DateConversions {
        val STANDARD_DATE_FORMAT_OLD: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val STANDARD_DATE_FORMAT: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ZZZZ z", Locale.US) // TODO: is this correct?
    }

    object ApiRootPaths {
        private const val END_POINT = "http://api.draglabs.com/api"
        private const val API_VERSION = "/v2.0"
        private const val BASE_STRING = END_POINT + API_VERSION

        const val JAM = BASE_STRING + "/jam/"
        const val USER = BASE_STRING + "/user/"
    }

    object JamFeatures {
        const val NEW_JAM = "New Jam"
        const val UPDATE_JAM = "Update Jam"
        const val JOIN_JAM = "Join Jam"
        const val JAM_RECORDING_UPLOAD = "Jam Recording Upload"
        const val GET_JAM_DETAILS = "Get Jam Details"
        const val GET_RECORDINGS = "Get Recordings"
        const val REGISTER_USER = "Register UserModel"
        const val UPDATE_USER = "Update UserModel"
        const val GET_ACTIVE_JAM = "Get Active Jam"
        const val GET_USER_ACTIVITY = "Get UserModel Activity"
    }

    object JsonParsingKeys {
        const val CODE = "code"
        const val JAM_ID = "jam_id"
        const val UUID = "uuid"
        const val ID = "id"
        const val _ID = "_id"
        const val USER_ID = "user_id"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val MESSAGE = "message"
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"
        const val PIN = "pin"
        const val RECORDINGS = "recordings"
        const val S3URL = "s3url"
        const val NOTES = "notes"
        const val JAMS = "jams"
        const val DATA = "data"
        const val LOCATION = "location"
        const val LAT = "lat"
        const val LNG = "lng"
        const val COORDINATES = "coordinates"
        const val IS_CURRENT = "is_current"
        const val COLLABORATORS = "collaborators"
        const val FILE_NAME = "audioFile"
        const val LINK = "link"
        const val FB_EMAIL = "fb_email"
        const val FB_ID = "fb_id"
        const val CURRENT_JAM = "current_jam"
        const val JAM_NAME = "jam_name"
        const val NAME = "name"
        const val FACEBOOK_ID = "facebook_id"
        const val ACCESS_TOKEN = "access_token"
    }

    object ApiFunctionPaths {
        const val NEW_JAM = JAM + "new"
        const val UPDATE_JAM = JAM + "update"
        const val JOIN_JAM = JAM + "join"
        const val JAM_RECORDING_UPLOAD = JAM + "upload"
        const val GET_JAM_DETAILS = JAM + "details/" // append Jam ID
        const val GET_RECORDINGS = JAM + "recording/" // append Jam ID
        const val REGISTER_USER = USER + "register"
        const val UPDATE_USER = USER + "update"
        const val GET_ACTIVE_JAM = USER + "jam/active"
        const val GET_USER_ACTIVITY = USER + "activity"
    }

    object FacebookUserDataParams {
        const val FB_NAME = "fb_name"
        const val FB_EMAIL = "fb_email"
        const val FB_IMAGE = "fb_image"
        const val LOCAL_PATH = "local_path"
    }

    object ViewJamData {
        const val JAMS = "jams"
    }

    @Deprecated(message = deprecationString)
    object JamFeaturesOld {
        const val SOLO_UPLOAD_RECORDING = "Solo Upload Recording"
        const val EXIT_JAM = "Exit Jam"
        const val GET_COLLABORATORS = "Get Collaborators"
        const val NOTIFY_USER = "Notify UserModel"
    }

    @Deprecated(message = deprecationString)
    object ApiFunctionPathsOld {
        const val EXIT_JAM = JAM + "exit"
        const val GET_COLLABORATORS = JAM + "collaborators"
        const val COMPRESS = JAM + "archive"
        const val NOTIFY_USER = JAM + "notifyuser"
        const val SOLO_UPLOAD_RECORDING = "/soloupload/id"
    }
}
