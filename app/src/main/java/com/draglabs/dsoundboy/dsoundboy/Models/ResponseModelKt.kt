/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2017. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Models

/**
 * Created by davrukin on 12/30/2017.
 */

class ResponseModelKt {

    class JamFunctions {
        data class NewJam(
            val new_jam: Models.Jam
        )

        data class CurrentJam(
            val current_jam: Models.Jam
        )

        data class JoinJam(
            val id: String,
            val name: String,
            val start_time: String,
            val end_time: String,
            val location: String,
            val notes: String,
            val collaborators: Models.Collaborator
        )
    }

    class UserFunctions {
        data class RegisterUser(
            val id: String,
            val first_name: String,
            val last_name: String,
            val fb_email: String,
            val fb_id: String,
            val current_jam: JamFunctions.CurrentJam
        )
    }

    class Models {
        data class Collaborator(
            // details not currently available, proposed details
            var id: String // UUID
        )

        data class Recording(
            val id: String,
            val user_id: String,
            val file_name: String,
            val jam_id: String,
            val start_time: String,
            val end_time: String,
            val notes: String,
            val s3url: String
        )

        data class Jam(
            val id: String,
            val pin: String,
            val is_current: Boolean,
            val name: String,
            val user_id: String,
            val coordinates: LongArray,
            val collaborators: Collaborator,
            val recordings: Recording,
            val location: String,
            val start_time: String,
            val end_time: String,
            val notes: String
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
