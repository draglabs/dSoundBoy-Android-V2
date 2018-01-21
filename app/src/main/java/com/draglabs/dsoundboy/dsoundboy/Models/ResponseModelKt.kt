/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Models

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONArray

/**
 * Created by davrukin on 12/30/2017.
 * @author Daniel Avrukin
 */

class ResponseModelKt {
    /*object values {
        private val id = StringsModelKt.JsonParsingKeys.ID
        private val pin = StringsModelKt.JsonParsingKeys.PIN
        private val is_current = StringsModelKt.JsonParsingKeys.IS_CURRENT
        private val name = StringsModelKt.JsonParsingKeys.NAME
        private val user_id = StringsModelKt.JsonParsingKeys.USER_ID
        private val coordinates = StringsModelKt.JsonParsingKeys.COORDINATES
        private val collaborators = StringsModelKt.JsonParsingKeys.COLLABORATORS
        private val recordings = StringsModelKt.JsonParsingKeys.RECORDINGS
        private val location = StringsModelKt.JsonParsingKeys.LOCATION
        private val start_time = StringsModelKt.JsonParsingKeys.START_TIME
        private val end_time = StringsModelKt.JsonParsingKeys.END_TIME
        private val notes = StringsModelKt.JsonParsingKeys.NOTES
    }*/

    class JamFunctions {
        data class NewJam(
            //var new_jam: Models.Jam
            @SerializedName("id") var id: String,
            @SerializedName("pin") var pin: String,
            @SerializedName("is_current") var is_current: Boolean,
            @SerializedName("name") var name: String,
            @SerializedName("user_id") var user_id: String,
            @SerializedName("coordinates") var coordinates: LongArray,
            @SerializedName("collaborators") var collaborators: Any,
            @SerializedName("recordings") var recordings: Any,
            @SerializedName("location") var location: String,
            @SerializedName("start_time") var start_time: String,
            @SerializedName("end_time") var end_time: String,
            @SerializedName("notes") var notes: String,
            @SerializedName("link") var link: String
        )

        data class UpdateJam(
            var update_jam: Models.JamMini
        )

        data class JoinJam(
            @SerializedName("id") var id: String,
            @SerializedName("pin") var pin: String, // just added to api
            @SerializedName("name") var name: String,
            @SerializedName("start_time") var start_time: String,
            @SerializedName("end_time") var end_time: String,
            @SerializedName("location") var location: String,
            @SerializedName("notes") var notes: String,
            @SerializedName("collaborators") var collaborators: Any,
            @SerializedName("link") var link: String
        )

        data class UploadJam(
            var response: String
        )

        data class GetJamDetails(
            @SerializedName("id") var id: String,
            @SerializedName("pin") var pin: String,
            @SerializedName("is_current") var is_current: Boolean,
            @SerializedName("name") var name: String,
            @SerializedName("user_id") var user_id: String,
            @SerializedName("coordinates") var coordinates: LongArray,
            @SerializedName("collaborators") var collaborators: Array<Models.Collaborator>,
            @SerializedName("recordings") var recordings: Array<Models.Recording>,
            @SerializedName("location") var location: String,
            @SerializedName("start_time") var start_time: String,
            @SerializedName("end_time") var end_time: String,
            @SerializedName("notes") var notes: String,
            @SerializedName("link") var link: String
        )

        data class GetRecordings(
            var recordings: Array<Models.Recording>
        )

        data class CurrentJam(
            var current_jam: Models.Jam
        )
    }

    class UserFunctions {
        data class RegisterUser( // date format to put where it is: yyyy-MM-dd HH:mm:ss
            @SerializedName("id") var id: String,
            @SerializedName("first_name") var first_name: String, // TODO: return string if no current jam, or jam object if one exists
            @SerializedName("last_name") var last_name: String,
            @SerializedName("fb_email") var fb_email: String,
            @SerializedName("fb_id") var fb_id: String,
            //var current_jam: JamFunctions.CurrentJam
            @SerializedName("current_jam") var current_jam: Any
        )

        data class UpdateUser(
            var message: String // api not really working now
        )

        data class GetActiveJam(
            var active_jam: Models.Jam
        )

        data class GetUserActivityArray(
            var jams: List<GetUserActivity> // how is this different from "var jams: Array<Models.JamMini>"
        )

        data class GetUserActivity(
            @SerializedName("id") var id: String,
            @SerializedName("name") var name: String,
            @SerializedName("start_time") var start_time: String,
            @SerializedName("end_time") var end_time: String,
            @SerializedName("location") var location: String,
            @SerializedName("notes") var notes: String,
            @SerializedName("collaborators") var collaborators: Array<Models.Collaborator>,
            @SerializedName("link") var link: String
        )
    }

    class CompressorFunctions {
        data class Compressor(
            var response: String
        )
    }

    class Models {
        data class Collaborator(
            // details not currently available, proposed details
            @SerializedName("id") var id: String // UUID
        )

        data class Recording(
            @SerializedName("id") var id: String,
            @SerializedName("user_id") var user_id: String,
            @SerializedName("file_name") var file_name: String,
            @SerializedName("jame_id") var jam_id: String,
            @SerializedName("start_time") var start_time: String,
            @SerializedName("end_time") var end_time: String,
            @SerializedName("notes") var notes: String,
            @SerializedName("s3url") var s3url: String
        )

        data class Jam(
            @SerializedName("id") var id: String,
            @SerializedName("pin") var pin: String,
            @SerializedName("is_current") var is_current: Boolean,
            @SerializedName("name") var name: String,
            @SerializedName("user_id") var user_id: String,
            @SerializedName("coordinates") var coordinates: LongArray,
            @SerializedName("collaborators") var collaborators: Array<Collaborator>,
            @SerializedName("recordings") var recordings: Array<Recording>,
            @SerializedName("location") var location: String,
            @SerializedName("start_time") var start_time: String,
            @SerializedName("end_time") var end_time: String,
            @SerializedName("notes") var notes: String,
            @SerializedName("link") var link: String
        )

        data class JamMini(
            @SerializedName("id") var id: String,
            @SerializedName("name") var name: String,
            @SerializedName("start_time") var start_time: String,
            @SerializedName("end_time") var end_time: String,
            @SerializedName("location") var location: String,
            @SerializedName("notes") var notes: String,
            @SerializedName("collaborators") var collaborators: Array<Models.Collaborator>,
            @SerializedName("link") var link: String
        )
    }


    /*fun getJSON(function: String): JSONObject {
        if (function == "NewJam") {
            val jsonString = Gson().toJson(JamFunctions.NewJam(Models.Jam()))
            val json = JSONObject().getJSONObject(jsonString)
            return json
        } else {
            return JSONObject()
        }
    }*/
}

