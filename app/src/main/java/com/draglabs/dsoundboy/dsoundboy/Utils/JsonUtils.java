package com.draglabs.dsoundboy.dsoundboy.Utils;

import android.util.Log;

import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModel;
import com.draglabs.dsoundboy.dsoundboy.Models.StringsModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * <p>Created by davrukin on 8/15/17.</p>
 * <p>Parses incoming JSONs for relevant info</p>
 */

@SuppressWarnings("DefaultFileTemplate")
class JsonUtils {
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

    public static Object getItem(JSONObject response, String item) {
        String standardError = "Other Error or Wrong Error Code. Check API and App.";
        Gson gson = new Gson();
        String jsonString = response.toString();
        ResponseModel responseModel = gson.fromJson(jsonString, ResponseModel.class);

        if (item.equals(StringsModel.jsonTypes.ARCHIVE_URL.type())) {
            return responseModel.getArchive_url();
        } else if (item.equals(StringsModel.jsonTypes.COLLABORATORS.type())) {
            return responseModel.getCollaborators();
        } else if (item.equals(StringsModel.jsonTypes.COORDINATES.type())) {
            return responseModel.getCoordinates();
        } else if (item.equals(StringsModel.jsonTypes.CURRENT_JAM.type())) {
            return responseModel.getCurrent_jam();
        } else if (item.equals(StringsModel.jsonTypes.END_TIME.type())) {
            return responseModel.getEnd_time();
        } else if (item.equals(StringsModel.jsonTypes.FB_EMAIL.type())) {
            return responseModel.getFb_email();
        } else if (item.equals(StringsModel.jsonTypes.FB_ID.type())) {
            return responseModel.getFb_id();
        } else if (item.equals(StringsModel.jsonTypes.FIRST_NAME.type())) {
            return responseModel.getFirst_name();
        } else if (item.equals(StringsModel.jsonTypes.IS_CURRENT.type())) {
            return responseModel.isIs_current();
        } else if (item.equals(StringsModel.jsonTypes.JAM_ID.type())) {
            return responseModel.getId();
        } else if (item.equals(StringsModel.jsonTypes.JAMS.type())) {
            return responseModel.getCurrent_jam();
        } else if (item.equals(StringsModel.jsonTypes.LAST_NAME.type())) {
            return responseModel.getLast_name();
        } else if (item.equals(StringsModel.jsonTypes.LOCATION.type())) {
            return responseModel.getLocation();
        } else if (item.equals(StringsModel.jsonTypes.MESSAGE.type())) {
            return responseModel.getMessage();
        } else if (item.equals(StringsModel.jsonTypes.NOTES.type())) {
            return responseModel.getNotes();
        } else if (item.equals(StringsModel.jsonTypes.PIN.type())) {
            return responseModel.getPin();
        } else if (item.equals(StringsModel.jsonTypes.RECORDINGS.type())) {
            return responseModel.getRecordings();
        } else if (item.equals(StringsModel.jsonTypes.START_TIME.type())) {
            return responseModel.getStart_time();
        } else if (item.equals(StringsModel.jsonTypes.USER_ID.type())) {
            return responseModel.getUser_id();
        } else if (item.equals(StringsModel.jsonTypes.UUID.type())) {
            return responseModel.getUser_id();
        } else {
            return standardError;
        }
    }


    /**
     * |          Task         	|                     Post Types                     	|                                                   ResponseModel Types                                                   	|
     |:---------------------:	|:--------------------------------------------------:	|:------------------------------------------------------------------------------------------------------------------:	|
     | Authenticate User     	| facebook_id, access_token                          	| code, user_id, first_name, last_name                                                                               	|
     | Solo Upload Recording 	| fileName, notes, startTime, endTime, audioFile     	| code, message                                                                                                      	|
     | Start Jam             	| user_id, jam_location, jam_name, jam_lat, jam_long 	| code, id, pin, startTime, endTime                                                                                  	|
     | Join Jam              	| unique_id, pin                                     	| code, id, pin, startTime, endTime                                                                                  	|
     | Jam Recording Upload  	| fileName, notes, startTime, endTime, audioFile     	| code, message                                                                                                      	|
     | Exit Jam              	| user_id, jam_id                                    	| code, message                                                                                                      	|
     | Get Collaborators     	| user_id, jam_id                                    	| code, collaborators array: email, name, facebook_id, id                                                            	|
     | Get User Activity     	| user_id                                            	| recordings array: s3url, fileName, jamID, startTime, endTime, notes, _id; jams array: id, name, startTime, endTime 	|
     | Notify User           	| jam_id                                             	| code, message                                                                                                      	|
     * @param callingMethod the calling method
     * @param jsonObject the json object to parse
     * @param data the data type being looked for
     * @return the information being looked for
     * @throws JSONException if there is one
     */
    public static String getJsonObject(String callingMethod, JSONObject jsonObject, String data) throws JSONException {
        int code = jsonObject.getInt("code");
        String standardError = "Other Error or Wrong Error Code. Check API and App.";

        Gson gson = new Gson();
        String jsonString = jsonObject.toString();
        ResponseModel responseModel = gson.fromJson(jsonString, ResponseModel.class);

        switch (callingMethod) {
            case StringsModel.NEW_JAM:
                if (code == 200) {
                    JSONObject jam = jsonObject.getJSONObject("jam");
                    return jam.getString(data);
                } else if (code == 400) {
                    if (jsonObject.get("error") == JSONObject.NULL) { // may break here
                        String message = jsonObject.getJSONObject("error").getString("message");
                        String fields = jsonObject.getJSONObject("error").getString("fields");
                        return code + ": " + message + "; " + fields;
                    } else {
                        return code + ": " + jsonObject.getString("error");
                    }
                } else if (code == 409) {
                    JSONObject error = jsonObject.getJSONObject("error");
                    String message = error.getString("message");
                    String jamID = error.getString("jam_id");
                    return code + ": " + message + "; " + jamID;
                } else {
                    return standardError;
                }
            case StringsModel.UPDATE_JAM:
                if (code == 200) {
                    JSONObject jam = jsonObject.getJSONObject("jam");
                    return jam.getString(data);
                } else if (code == 400) {
                    if (jsonObject.get("error") == JSONObject.NULL) { // may break here
                        String message = jsonObject.getJSONObject("error").getString("message");
                        String fields = jsonObject.getJSONObject("error").getString("fields");
                        return code + ": " + message + "; " + fields;
                    } else {
                        return code + ": " + jsonObject.getString("error");
                    }
                } else if (code == 409) {
                    JSONObject error = jsonObject.getJSONObject("error");
                    String message = error.getString("message");
                    String jamID = error.getString("jam_id");
                    return code + ": " + message + "; " + jamID;
                } else {
                    return standardError;
                }
            case StringsModel.JOIN_JAM:
                if (code == 200) {
                    JSONObject jam = jsonObject.getJSONObject("jam");
                    return jam.getString(data);
                } else if (code == 400 || code == 403) {
                    return code + ": " + jsonObject.getString("error");
                } else if (code == 409) {
                    JSONObject error = jsonObject.getJSONObject("error");
                    String message = error.getString("message");
                    String jamID = error.getString("jam_id");
                    return code + ": " + message + "; " + jamID;
                } else {
                    return standardError;
                }
            case StringsModel.JAM_RECORDING_UPLOAD:
                if (code == 201) {
                    return jsonObject.getString("message");
                } else if (code == 400 || code == 401) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case StringsModel.SOLO_UPLOAD_RECORDING:
                if (code == 201) {
                    return jsonObject.getString("message");
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case StringsModel.GET_JAM_DETAILS:
                if (code == 200) {
                    JSONArray jamDetails = jsonObject.getJSONArray("Enter Name Here"); // TODO: What's the name of the array?
                    return jamDetails.toString();
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                }
            case StringsModel.GET_RECORDINGS:
                if (code == 200) {
                    JSONArray jamDetails = jsonObject.getJSONArray("Enter Name Here"); // TODO: What's the name of the array?
                    return jamDetails.toString();
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                }
            case StringsModel.REGISTER_USER:
                if (code == 200 && (data.equals(StringsModel.jsonTypes.UUID.type()) || data.equals(StringsModel.jsonTypes.FIRST_NAME.type()) || data.equals(StringsModel.jsonTypes.LAST_NAME.type()))) {
                    JSONObject user = jsonObject.getJSONObject("user");
                    return user.getString(data);
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case StringsModel.UPDATE_USER:
                if (code == 200) {
                    JSONArray jamDetails = jsonObject.getJSONArray("Enter Name Here"); // TODO: What's the name of the array?
                    return jamDetails.toString();
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                }
            case StringsModel.GET_ACTIVE_JAM:
                if (code == 200) {
                    JSONArray jamDetails = jsonObject.getJSONArray("Enter Name Here"); // TODO: What's the name of the array?
                    return jamDetails.toString();
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                }
            case StringsModel.GET_USER_ACTIVITY: // json type doesn't matter
                if (code == 200) {
                    //JSONArray recordings = jsonObject.getJSONArray("recordings"); // doesn't exist anymore
                    JSONArray jams = jsonObject.getJSONArray("jams");
                    Log.v("Jams: ", jams.toString());
                    /*if (data.equals(StringsModel.jsonTypes.RECORDINGS.type())) {
                        return recordings.toString(); // TODO: maybe the recordings list is empty so nothing is being shown? arrange for that check
                    } else if (data.equals(StringsModel.jsonTypes.JAMS.type())) {
                        return jams.toString();
                    } else {
                        String recordingsString = recordings.toString();
                        String jamsString = jams.toString();
                        return recordingsString + "\n" + jamsString;
                    }*/
                    return jams.toString();
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case StringsModel.EXIT_JAM:
                if (code == 200) {
                    return jsonObject.getString("message");
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case StringsModel.GET_COLLABORATORS: // json type doesn't matter
                if (code == 200) {
                    JSONObject jam = jsonObject.getJSONObject("jam");
                    JSONArray collaborators = jam.getJSONArray("collaborators");

                    String[] collaboratorsArray = new String[collaborators.length()];
                    for (int i = 0; i < collaborators.length(); i++) {
                        String email = collaborators.getJSONObject(i).getString("email");
                        String name = collaborators.getJSONObject(i).getString("name");
                        String facebookID = collaborators.getJSONObject(i).getString("facebook_id");
                        String id = collaborators.getJSONObject(i).getString("id");
                        collaboratorsArray[i] = "{" + email + ", " + name + ", " + facebookID + ", " + id + "}";
                    }

                    return Arrays.toString(collaboratorsArray);
                } else if (code == 400 || code == 401) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case StringsModel.NOTIFY_USER:
                if (code == 200) {
                    return jsonObject.getString("message");
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            default:
                return standardError;
        }
    }
}
