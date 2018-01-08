/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Models

import com.draglabs.dsoundboy.dsoundboy.Models.StringsModelKt.ApiRootPaths.JAM
import com.draglabs.dsoundboy.dsoundboy.Models.StringsModelKt.ApiRootPaths.USER
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

        val JAM = BASE_STRING + "/jam/"
        val USER = BASE_STRING + "/user/"
    }

    object JamFeatures {
        val NEW_JAM = "New Jam"
        val UPDATE_JAM = "Update Jam"
        val JOIN_JAM = "Join Jam"
        val JAM_RECORDING_UPLOAD = "Jam Recording Upload"
        val GET_JAM_DETAILS = "Get Jam Details"
        val GET_RECORDINGS = "Get Recordings"
        val REGISTER_USER = "Register UserModel"
        val UPDATE_USER = "Update UserModel"
        val GET_ACTIVE_JAM = "Get Active Jam"
        val GET_USER_ACTIVITY = "Get UserModel Activity"
    }

    object JsonParsingKeys {
        val CODE = "code"
        val JAM_ID = "jam_id"
        val UUID = "uuid"
        val ID = "id"
        val _ID = "_id"
        val USER_ID = "user_id"
        val FIRST_NAME = "first_name"
        val LAST_NAME = "last_name"
        val MESSAGE = "message"
        val START_TIME = "start_time"
        val END_TIME = "end_time"
        val PIN = "pin"
        val RECORDINGS = "recordings"
        val S3URL = "s3url"
        val NOTES = "notes"
        val JAMS = "jams"
        val DATA = "data"
        val LOCATION = "location"
        val LAT = "lat"
        val LNG = "lng"
        val COORDINATES = "coordinates"
        val IS_CURRENT = "is_current"
        val COLLABORATORS = "collaborators"
        val FILE_NAME = "file_name"
        val LINK = "link"
        val FB_EMAIL = "fb_email"
        val FB_ID = "fb_id"
        val CURRENT_JAM = "current_jam"
        val JAM_NAME = "jam_name"
        val NAME = "name"
        val FACEBOOK_ID = "facebook_id"
        val ACCESS_TOKEN = "access_token"
    }

    object ApiFunctionPaths {
        val NEW_JAM = JAM + "new"
        val UPDATE_JAM = JAM + "update"
        val JOIN_JAM = JAM + "join"
        val JAM_RECORDING_UPLOAD = JAM + "upload"
        val GET_JAM_DETAILS = JAM + "details/" // append Jam ID
        val GET_RECORDINGS = JAM + "recording/" // append Jam ID
        val REGISTER_USER = USER + "register"
        val UPDATE_USER = USER + "update"
        val GET_ACTIVE_JAM = USER + "jam/active"
        val GET_USER_ACTIVITY = USER + "activity"
    }

    @Deprecated(message = deprecationString)
    object JamFeaturesOld {
        val SOLO_UPLOAD_RECORDING = "Solo Upload Recording"
        val EXIT_JAM = "Exit Jam"
        val GET_COLLABORATORS = "Get Collaborators"
        val NOTIFY_USER = "Notify UserModel"
    }

    @Deprecated(message = deprecationString)
    object ApiFunctionPathsOld {
        val EXIT_JAM = JAM + "exit"
        val GET_COLLABORATORS = JAM + "collaborators"
        val COMPRESS = JAM + "archive"
        val NOTIFY_USER = JAM + "notifyuser"
        val SOLO_UPLOAD_RECORDING = "/soloupload/id"
    }
}
