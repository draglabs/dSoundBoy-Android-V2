package com.draglabs.dsoundboy.dsoundboy.Utils

import android.util.Log
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModel
import com.draglabs.dsoundboy.dsoundboy.Models.StringsModel
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 *
 * Created by davrukin on 8/15/17.
 *
 * Parses incoming JSONs for relevant info
 */

@Deprecated("")
internal object JsonUtils {
    /*@Override
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean StoragePermission = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    boolean RecordPermission = (grantResults[1] == PackageManager.PERMISSION_GRANTED);

                    if (StoragePermission && RecordPermission) { // both are true
                        Toast.makeText(getApplicationContext(), "Permission Granted.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Permission Denied.", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }*/

    /* public static boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return ((result == PackageManager.PERMISSION_GRANTED) && (result1 == PackageManager.PERMISSION_GRANTED));
    }*/

    fun getItem(response: JSONObject, item: String): Any {
        val standardError = "Other Error or Wrong Error Code. Check API and App."
        val gson = Gson()
        val jsonString = response.toString()
        val responseModel = gson.fromJson(jsonString, ResponseModel::class.java)

        return if (item == StringsModel.jsonTypes.ARCHIVE_URL.type()) {
            responseModel.archive_url
        } else if (item == StringsModel.jsonTypes.COLLABORATORS.type()) {
            responseModel.collaborators
        } else if (item == StringsModel.jsonTypes.COORDINATES.type()) {
            responseModel.coordinates
        } else if (item == StringsModel.jsonTypes.CURRENT_JAM.type()) {
            responseModel.current_jam
        } else if (item == StringsModel.jsonTypes.END_TIME.type()) {
            responseModel.end_time
        } else if (item == StringsModel.jsonTypes.FB_EMAIL.type()) {
            responseModel.fb_email
        } else if (item == StringsModel.jsonTypes.FB_ID.type()) {
            responseModel.fb_id
        } else if (item == StringsModel.jsonTypes.FIRST_NAME.type()) {
            responseModel.first_name
        } else if (item == StringsModel.jsonTypes.IS_CURRENT.type()) {
            responseModel.isIs_current
        } else if (item == StringsModel.jsonTypes.JAM_ID.type()) {
            responseModel.id
        } else if (item == StringsModel.jsonTypes.JAMS.type()) {
            responseModel.current_jam
        } else if (item == StringsModel.jsonTypes.LAST_NAME.type()) {
            responseModel.last_name
        } else if (item == StringsModel.jsonTypes.LOCATION.type()) {
            responseModel.location
        } else if (item == StringsModel.jsonTypes.MESSAGE.type()) {
            responseModel.message
        } else if (item == StringsModel.jsonTypes.NOTES.type()) {
            responseModel.notes
        } else if (item == StringsModel.jsonTypes.PIN.type()) {
            responseModel.pin
        } else if (item == StringsModel.jsonTypes.RECORDINGS.type()) {
            responseModel.recordings
        } else if (item == StringsModel.jsonTypes.START_TIME.type()) {
            responseModel.start_time
        } else if (item == StringsModel.jsonTypes.USER_ID.type()) {
            responseModel.user_id
        } else if (item == StringsModel.jsonTypes.UUID.type()) {
            responseModel.user_id
        } else {
            standardError
        }
    }


    /**
     * |          Task         	|                     Post Types                     	|                                                   ResponseModel Types                                                   	|
     * |:---------------------:	|:--------------------------------------------------:	|:------------------------------------------------------------------------------------------------------------------:	|
     * | Authenticate UserModel     	| facebook_id, access_token                          	| code, user_id, first_name, last_name                                                                               	|
     * | Solo Upload Recording 	| fileName, notes, startTime, endTime, audioFile     	| code, message                                                                                                      	|
     * | Start Jam             	| user_id, jam_location, jam_name, jam_lat, jam_long 	| code, id, pin, startTime, endTime                                                                                  	|
     * | Join Jam              	| unique_id, pin                                     	| code, id, pin, startTime, endTime                                                                                  	|
     * | Jam Recording Upload  	| fileName, notes, startTime, endTime, audioFile     	| code, message                                                                                                      	|
     * | Exit Jam              	| user_id, jam_id                                    	| code, message                                                                                                      	|
     * | Get Collaborators     	| user_id, jam_id                                    	| code, collaborators array: email, name, facebook_id, id                                                            	|
     * | Get UserModel Activity     	| user_id                                            	| recordings array: s3url, fileName, jamID, startTime, endTime, notes, _id; jams array: id, name, startTime, endTime 	|
     * | Notify UserModel           	| jam_id                                             	| code, message                                                                                                      	|
     * @param callingMethod the calling method
     * @param jsonObject the json object to parse
     * @param data the data type being looked for
     * @return the information being looked for
     * @throws JSONException if there is one
     */
    @Throws(JSONException::class)
    fun getJsonObject(callingMethod: String, jsonObject: JSONObject, data: String): String {
        val code = jsonObject.getInt("code")
        val standardError = "Other Error or Wrong Error Code. Check API and App."

        val gson = Gson()
        val jsonString = jsonObject.toString()
        val responseModel = gson.fromJson(jsonString, ResponseModel::class.java)

        when (callingMethod) {
            StringsModel.NEW_JAM -> if (code == 200) {
                val jam = jsonObject.getJSONObject("jam")
                return jam.getString(data)
            } else if (code == 400) {
                if (jsonObject.get("error") === JSONObject.NULL) { // may break here
                    val message = jsonObject.getJSONObject("error").getString("message")
                    val fields = jsonObject.getJSONObject("error").getString("fields")
                    return code.toString() + ": " + message + "; " + fields
                } else {
                    return code.toString() + ": " + jsonObject.getString("error")
                }
            } else if (code == 409) {
                val error = jsonObject.getJSONObject("error")
                val message = error.getString("message")
                val jamID = error.getString("jam_id")
                return code.toString() + ": " + message + "; " + jamID
            } else {
                return standardError
            }
            StringsModel.UPDATE_JAM -> if (code == 200) {
                val jam = jsonObject.getJSONObject("jam")
                return jam.getString(data)
            } else if (code == 400) {
                if (jsonObject.get("error") === JSONObject.NULL) { // may break here
                    val message = jsonObject.getJSONObject("error").getString("message")
                    val fields = jsonObject.getJSONObject("error").getString("fields")
                    return code.toString() + ": " + message + "; " + fields
                } else {
                    return code.toString() + ": " + jsonObject.getString("error")
                }
            } else if (code == 409) {
                val error = jsonObject.getJSONObject("error")
                val message = error.getString("message")
                val jamID = error.getString("jam_id")
                return code.toString() + ": " + message + "; " + jamID
            } else {
                return standardError
            }
            StringsModel.JOIN_JAM -> if (code == 200) {
                val jam = jsonObject.getJSONObject("jam")
                return jam.getString(data)
            } else if (code == 400 || code == 403) {
                return code.toString() + ": " + jsonObject.getString("error")
            } else if (code == 409) {
                val error = jsonObject.getJSONObject("error")
                val message = error.getString("message")
                val jamID = error.getString("jam_id")
                return code.toString() + ": " + message + "; " + jamID
            } else {
                return standardError
            }
            StringsModel.JAM_RECORDING_UPLOAD -> return if (code == 201) {
                jsonObject.getString("message")
            } else if (code == 400 || code == 401) {
                code.toString() + ": " + jsonObject.getString("error")
            } else {
                standardError
            }
            StringsModel.SOLO_UPLOAD_RECORDING -> return if (code == 201) {
                jsonObject.getString("message")
            } else if (code == 400) {
                code.toString() + ": " + jsonObject.getString("error")
            } else {
                standardError
            }
            StringsModel.GET_JAM_DETAILS -> {
                if (code == 200) {
                    val jamDetails = jsonObject.getJSONArray("Enter Name Here") // TODO: What's the name of the array?
                    return jamDetails.toString()
                } else if (code == 400) {
                    return code.toString() + ": " + jsonObject.getString("error")
                }
                if (code == 200) {
                    val jamDetails = jsonObject.getJSONArray("Enter Name Here") // TODO: What's the name of the array?
                    return jamDetails.toString()
                } else if (code == 400) {
                    return code.toString() + ": " + jsonObject.getString("error")
                }
                if (code == 200 && (data == StringsModel.jsonTypes.UUID.type() || data == StringsModel.jsonTypes.FIRST_NAME.type() || data == StringsModel.jsonTypes.LAST_NAME.type())) {
                    val user = jsonObject.getJSONObject("user")
                    return user.getString(data)
                } else return if (code == 400) {
                    code.toString() + ": " + jsonObject.getString("error")
                } else {
                    standardError
                }
            }
            StringsModel.GET_RECORDINGS -> {
                if (code == 200) {
                    val jamDetails = jsonObject.getJSONArray("Enter Name Here")
                    return jamDetails.toString()
                } else if (code == 400) {
                    return code.toString() + ": " + jsonObject.getString("error")
                }
                if (code == 200 && (data == StringsModel.jsonTypes.UUID.type() || data == StringsModel.jsonTypes.FIRST_NAME.type() || data == StringsModel.jsonTypes.LAST_NAME.type())) {
                    val user = jsonObject.getJSONObject("user")
                    return user.getString(data)
                } else return if (code == 400) {
                    code.toString() + ": " + jsonObject.getString("error")
                } else {
                    standardError
                }
            }
            StringsModel.REGISTER_USER -> if (code == 200 && (data == StringsModel.jsonTypes.UUID.type() || data == StringsModel.jsonTypes.FIRST_NAME.type() || data == StringsModel.jsonTypes.LAST_NAME.type())) {
                val user = jsonObject.getJSONObject("user")
                return user.getString(data)
            } else return if (code == 400) {
                code.toString() + ": " + jsonObject.getString("error")
            } else {
                standardError
            }
            StringsModel.UPDATE_USER -> {
                if (code == 200) {
                    val jamDetails = jsonObject.getJSONArray("Enter Name Here") // TODO: What's the name of the array?
                    return jamDetails.toString()
                } else if (code == 400) {
                    return code.toString() + ": " + jsonObject.getString("error")
                }
                if (code == 200) {
                    val jamDetails = jsonObject.getJSONArray("Enter Name Here") // TODO: What's the name of the array?
                    return jamDetails.toString()
                } else if (code == 400) {
                    return code.toString() + ": " + jsonObject.getString("error")
                }
                if (code == 200) {
                    //JSONArray recordings = jsonObject.getJSONArray("recordings"); // doesn't exist anymore
                    val jams = jsonObject.getJSONArray("jams")
                    Log.v("Jams: ", jams.toString())
                    /*if (data.equals(StringsModel.jsonTypes.RECORDINGS.type())) {
                        return recordings.toString(); // TODO: maybe the recordings list is empty so nothing is being shown? arrange for that check
                    } else if (data.equals(StringsModel.jsonTypes.JAMS.type())) {
                        return jams.toString();
                    } else {
                        String recordingsString = recordings.toString();
                        String jamsString = jams.toString();
                        return recordingsString + "\n" + jamsString;
                    }*/
                    return jams.toString()
                } else return if (code == 400) {
                    code.toString() + ": " + jsonObject.getString("error")
                } else {
                    standardError
                }
            }
            StringsModel.GET_ACTIVE_JAM -> {
                if (code == 200) {
                    val jamDetails = jsonObject.getJSONArray("Enter Name Here")
                    return jamDetails.toString()
                } else if (code == 400) {
                    return code.toString() + ": " + jsonObject.getString("error")
                }
                if (code == 200) {
                    val jams = jsonObject.getJSONArray("jams")
                    Log.v("Jams: ", jams.toString())
                    return jams.toString()
                } else return if (code == 400) {
                    code.toString() + ": " + jsonObject.getString("error")
                } else {
                    standardError
                }
            }
            StringsModel.GET_USER_ACTIVITY // json type doesn't matter
            -> if (code == 200) {
                val jams = jsonObject.getJSONArray("jams")
                Log.v("Jams: ", jams.toString())
                return jams.toString()
            } else return if (code == 400) {
                code.toString() + ": " + jsonObject.getString("error")
            } else {
                standardError
            }
            StringsModel.EXIT_JAM -> return if (code == 200) {
                jsonObject.getString("message")
            } else if (code == 400) {
                code.toString() + ": " + jsonObject.getString("error")
            } else {
                standardError
            }
            StringsModel.GET_COLLABORATORS // json type doesn't matter
            -> if (code == 200) {
                val jam = jsonObject.getJSONObject("jam")
                val collaborators = jam.getJSONArray("collaborators")

                val collaboratorsArray = arrayOfNulls<String>(collaborators.length())
                for (i in 0 until collaborators.length()) {
                    val email = collaborators.getJSONObject(i).getString("email")
                    val name = collaborators.getJSONObject(i).getString("name")
                    val facebookID = collaborators.getJSONObject(i).getString("facebook_id")
                    val id = collaborators.getJSONObject(i).getString("id")
                    collaboratorsArray[i] = "{$email, $name, $facebookID, $id}"
                }

                return Arrays.toString(collaboratorsArray)
            } else return if (code == 400 || code == 401) {
                code.toString() + ": " + jsonObject.getString("error")
            } else {
                standardError
            }
            StringsModel.NOTIFY_USER -> return if (code == 200) {
                jsonObject.getString("message")
            } else if (code == 400) {
                code.toString() + ": " + jsonObject.getString("error")
            } else {
                standardError
            }
            else -> return standardError
        }
    }
}
