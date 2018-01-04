/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Models

import com.google.gson.annotations.SerializedName

/**
 * Created by davrukin on 12/30/2017.
 * @author Daniel Avrukin
 */

class ResponseModelKt {

    class JamFunctions {
        data class NewJam(
            var new_jam: Models.Jam
        )

        data class UpdateJam(
            var update_jam: Models.JamMini
        )

        data class JoinJam(
            var join_jam: Models.JamMini
        )

        data class UploadJam(
            var response: String
        )

        data class GetJamDetails(
            var jam: Models.Jam
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

        data class GetUserActivity(
            var jams: Array<Models.Jam>
        )
    }

    class Models {
        data class Collaborator(
            // details not currently available, proposed details
            var id: String // UUID
        )

        data class Recording(
            var id: String,
            var user_id: String,
            var file_name: String,
            var jam_id: String,
            var start_time: String,
            var end_time: String,
            var notes: String,
            var s3url: String
        )

        data class Jam(
            var id: String,
            var pin: String,
            var is_current: Boolean,
            var name: String,
            var user_id: String,
            var coordinates: LongArray,
            var collaborators: Array<Collaborator>,
            var recordings: Array<Recording>,
            var location: String,
            var start_time: String,
            var end_time: String,
            var notes: String,
            var archive_url: String
        )

        data class JamMini(
            var id: String,
            var name: String,
            var start_time: String,
            var end_time: String,
            var location: String,
            var notes: String,
            var collaborators: Array<Models.Collaborator>
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
