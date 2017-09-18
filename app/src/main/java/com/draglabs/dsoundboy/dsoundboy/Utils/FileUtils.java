package com.draglabs.dsoundboy.dsoundboy.Utils;

import com.draglabs.dsoundboy.dsoundboy.Acessories.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by davrukin on 8/15/17.
 */

@SuppressWarnings("DefaultFileTemplate")
class FileUtils {

    public static void startRecording() {

    }

    public static void endRecording() {

    }

    public static byte[] shortToByte(short[] data) {
        return null;

    }

    public static void writeAudioDataToFile(String pathname) {

    }

    public static void MediaRecorderReady() {

    }

    public static String createAudioFileName(String bandName, String genreName, String venue, String email, Date date) {
        // format: BAND-NAME_GENRE-NAME_VENUE_EMAIL_DAY-MONTH-YEAR_HOUR:MINUTE:SECOND:MILLISECOND
        Locale currentLocale = Locale.getDefault();
        String pattern = "dd-MM-yyyy_HH:mm:ss:SSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, currentLocale);
        String dateString = simpleDateFormat.format(date);

        return (bandName + "_" + genreName + "_" + email + "_" + venue + "_" + dateString);
    }

    public static void requestPermission() {

    }

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

    public static void submitRecordingsToServer() {

    }

    /**
     * |          Task         	|                     Post Types                     	|                                                   Response Types                                                   	|
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
     * @param callingMethod
     * @param jsonObject
     * @param data
     * @return
     * @throws JSONException
     */
    public static String getJsonObject(String callingMethod, JSONObject jsonObject, String data) throws JSONException {
        int code = jsonObject.getInt("code");
        String standardError = "Other Error or Wrong Error Code. Check API and App.";

        switch (callingMethod) {
            case Strings.AUTHENTICATE_USER:
                if (code == 200 && (data.equals(Strings.jsonTypes.UNIQUE_ID.type()) || data.equals(Strings.jsonTypes.FIRST_NAME.type()) || data.equals(Strings.jsonTypes.LAST_NAME.type()))) {
                    JSONObject user = jsonObject.getJSONObject("user");
                    return user.getString(data);
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case Strings.SOLO_UPLOAD_RECORDING:
                if (code == 201) {
                    return jsonObject.getString("message");
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case Strings.START_JAM:
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
            case Strings.JOIN_JAM:
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
            case Strings.JAM_RECORDING_UPLOAD:
                if (code == 201) {
                    return jsonObject.getString("message");
                } else if (code == 400 || code == 401) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case Strings.EXIT_JAM:
                if (code == 200) {
                    return jsonObject.getString("message");
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case Strings.GET_COLLABORATORS: // json type doesn't matter
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
            case Strings.GET_USER_ACTIVITY: // json type doesn't matter
                if (code == 200) {
                    JSONArray recordings = jsonObject.getJSONArray("recordings");
                    JSONArray jams = jsonObject.getJSONArray("jams");

                    String recordingsString = recordings.toString();
                    String jamsString = jams.toString();
                    return recordingsString + "\n" + jamsString;
                } else if (code == 400) {
                    return code + ": " + jsonObject.getString("error");
                } else {
                    return standardError;
                }
            case Strings.NOTIFY_USER:
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
