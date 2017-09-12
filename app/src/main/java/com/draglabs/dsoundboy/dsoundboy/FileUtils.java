package com.draglabs.dsoundboy.dsoundboy;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by davrukin on 8/15/17.
 */

public class FileUtils {

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

    public static String getJsonObject(String callingMethod, JSONObject jsonObject, String data) throws JSONException {
        int code = jsonObject.getInt("code");

        if (callingMethod.equals(Strings.AUTHENTICATE_USER)) {
            if (code == 200 && (data.equals("user_id") || data.equals("first_name") || data.equals("last_name"))) {
                JSONObject user = jsonObject.getJSONObject("user");
                return user.getString(data);
            } else if (code == 400) {
                return code + " " + jsonObject.getString("error");
            } else {
                return "Other Error or Wrong Error Code. Check API and App.";
            }
        } else if (callingMethod.equals(Strings.SOLO_UPLOAD_RECORDING)) {
            if (code == 201) {
                return jsonObject.getString("message");
            } else if (code == 400) {
                return code + " " + jsonObject.getString("error");
            } else {
                return "Other Error or Wrong Error Code. Check API and App.";
            }
        } else if (callingMethod.equals(Strings.START_JAM)) {
            if (code == 200) {
                JSONObject jam = jsonObject.getJSONObject("jam");
                return jam.getString(data);
            } else if (code == 400) {
                if (jsonObject.get("error") == JSONObject.NULL) {
                    String message = jsonObject.getJSONObject("error").getString("message");
                    String fields = jsonObject.getJSONObject("error").getString("fields");
                    return code + "" + message + "; " + fields;
                } else {
                    return code + " " + jsonObject.getString("error");
                }
            } else if (code == 409) {
                JSONObject error = jsonObject.getJSONObject("error");
                String message = error.getString("message");
                String jamID = error.getString("jam_id");
                return code + " " + message + "; " + jamID;
            } else {
                return "Other Error or Wrong Error Code. Check API and App.";
            }
        } else if (callingMethod.equals(Strings.JOIN_JAM)) {
            return "Under Construction";
        } else if (callingMethod.equals(Strings.JAM_RECORDING_UPLOAD)) {
            return "Under Construction";
        } else if (callingMethod.equals(Strings.EXIT_JAM)) {
            return "Under Construction";
        } else if (callingMethod.equals(Strings.GET_COLLABORATORS)) {
            return "Under Construction";
        } else if (callingMethod.equals(Strings.GET_USER_ACTIVITY)) {
            return "Under Construction";
        } else if (callingMethod.equals(Strings.NOTIFY_USER)) {
            return "Under Construction";
        } else {
            return null;
        }
    }

}
