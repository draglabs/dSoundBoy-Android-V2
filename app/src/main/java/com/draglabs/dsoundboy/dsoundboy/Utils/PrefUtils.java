package com.draglabs.dsoundboy.dsoundboy.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.draglabs.dsoundboy.dsoundboy.Models.BandInfoModel;
import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davrukin on 9/18/17.
 * <br>
 * Tutorial link: https://freakycoder.com/android-notes-24-how-to-facebook-login-and-get-user-information-8d6df0350f1c
 */

public class PrefUtils {

    private Activity activity;
    private List<CallbackListener> callbacks;

    private final String UNIQUE_USER_ID = "unique_user_id";
    private final String JAM_PIN = "jam_pin";
    private final String JAM_ID = "jam_id";
    private final String JAM_START_TIME = "jam_start_time";
    private final String JAM_END_TIME = "jam_end_time";
    private final String COLLABORATORS = "collaborators";
    private final String USER_ACTIVITY = "user_activity";
    private final String JAM_DETAILS = "jam_details";
    private final String FB_ACCESS_TOKEN = "fb_access_token";

    // TODO: add data for "Enter Band And/Or Recording Info"

    public PrefUtils(Activity activity) {
        this.activity = activity;
        callbacks = new ArrayList<>();
    }

    public void addListener(CallbackListener callback) {
        callbacks.add(callback);
    }

    public void uniqueUserIDset() {
        for (CallbackListener callback : callbacks) {
            callback.uniqueUserIDset();
        }
    }

    public void jamPINset() {
        for (CallbackListener callback : callbacks) {
            callback.jamPINset();
        }
    }

    public void jamIDset() {
        for (CallbackListener callback : callbacks) {
            callback.jamIDset();
        }
    }

    public void jamStartTimeSet() {
        for (CallbackListener callback : callbacks) {
            callback.jamStartTimeSet();
        }
    }

    public void jamEndTimeSet() {
        for (CallbackListener callback : callbacks) {
            callback.jamEndTimeSet();
        }
    }

    public void getCollaboratorsSet() {
        for (CallbackListener callback : callbacks) {
            callback.getCollaboratorsSet();
        }
    }

    public void getUserActivitySet() {
        for (CallbackListener callback : callbacks) {
            callback.getUserActivitySet();
        }
    }

    public void getJamDetailsSet() {
        for (CallbackListener callback : callbacks) {
            callback.getJamDetailsSet();
        }
    }

    public void getAccessTokenSet() {
        for (CallbackListener callback : callbacks) {
            callback.getAccessTokenSet();
        }
    }

    public void saveJamID(String jamID) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JAM_ID, jamID);
        editor.apply();
        Log.v("Saved Jam ID: ", getJamID() + "");
        jamIDset();
    }

    public String getJamID() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String jamID = sharedPreferences.getString(JAM_ID, null);
        Log.v("Gotten Jam ID: ", getJamID() + "");
        return jamID;
    }

    public void clearJamID() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(JAM_ID);
        editor.apply();
    }

    public void saveJamPIN(String jamPIN) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JAM_PIN, jamPIN);
        editor.apply();
        Log.v("Saved Jam PIN: ", getJamPIN() + "");
        jamPINset();
    }

    public String getJamPIN() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String jamPIN = sharedPreferences.getString(JAM_PIN, null);
        Log.v("Gotten Jam PIN: ", jamPIN);
        return jamPIN;
    }

    public boolean hasJamPIN() {
        return getJamPIN() != null;
    }

    public void clearJamPIN() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(JAM_PIN);
        editor.apply();
    }

    public void saveUniqueUserID(String uniqueUserID) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UNIQUE_USER_ID, uniqueUserID);
        editor.apply();
        Log.d("Saved UUID: ", getUniqueUserID());
        uniqueUserIDset();
    }

    public String getUniqueUserID() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String UUID = sharedPreferences.getString(UNIQUE_USER_ID, "");
        Log.d("Gotten UUID: ", UUID);
        return UUID;
    }

    public boolean hasUniqueUserID() { // TODO: rename isAuthorized?
        return getUniqueUserID() != null;
    }

    public void clearUniqueUserID() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(UNIQUE_USER_ID);
        editor.apply();
    }

    public void saveJamStartTime(String jamStartTime) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JAM_START_TIME, jamStartTime);
        editor.apply();
        Log.v("Saved Jam Start Time: ", getJamStartTime() + "");
        jamStartTimeSet();
    }

    public String getJamStartTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString(JAM_START_TIME, null);
    }

    public void clearJamStartTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(JAM_START_TIME);
        editor.apply();
    }

    public void saveCollaborators(String collaborators) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COLLABORATORS, collaborators);
        editor.apply();
        Log.v("Saved Collaborators: ", getCollaborators() + "");
        getCollaboratorsSet();
    }

    public String getCollaborators() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString(COLLABORATORS, null);
    }

    public void clearCollaborators() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(COLLABORATORS);
        editor.apply();
    }

    public void saveUserActivity(String userActivity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ACTIVITY, userActivity);
        editor.apply();
        Log.v("Saved User Activity: ", getUserActivity() + "");
        getUserActivitySet();
    }

    public String getUserActivity() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String userActivity = sharedPreferences.getString(USER_ACTIVITY, null);
        Log.d("Gotten User Activity: ", userActivity); // 3558, 1356(5424), 1433(5732)
        return userActivity;
    }

    public void clearUserActivity() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_ACTIVITY);
        editor.apply();
    }

    public String getJamDetails() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String jamDetails = sharedPreferences.getString(JAM_DETAILS, null);
        Log.d("Jam Details: ", jamDetails);
        return jamDetails;
    }

    public void saveJamDetails(String jamDetails) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JAM_DETAILS, jamDetails);
        editor.apply();
        getJamDetailsSet();
    }

    public void saveJamEndTime(String jamEndTime) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JAM_END_TIME, jamEndTime);
        editor.apply();
        jamEndTimeSet();
    }

    public String getJamEndTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString(JAM_END_TIME, null);
    }

    public void clearJamEndTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(JAM_END_TIME);
        editor.apply();
    }

    public void saveAccessToken(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FB_ACCESS_TOKEN, token);
        editor.apply();
        Log.v("Saved Access Token: ", getAccessToken() + "");
        getAccessTokenSet();
    }

    public String getAccessToken() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString(FB_ACCESS_TOKEN, null);
    }

    public void clearToken() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("fb_access_token");
        editor.apply();
    }

    public void saveFacebookUserInfo(String first_name,String last_name, String email, String gender, String profileURL) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_first_name", first_name);
        editor.putString("fb_last_name", last_name);
        editor.putString("fb_email", email);
        editor.putString("fb_gender", gender);
        editor.putString("fb_profileURL", profileURL);
        editor.apply(); // This line is IMPORTANT !!!
        Log.d("dSoundBoy", "Shared Name : " + first_name + "\nLast Name : " + last_name + "\nEmailModel : " + email + "\nGender : " + gender + "\nProfile Pic : " + profileURL);
    }

    public void getFacebookUserInfo() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Log.d("dSoundBoy", "Name : " + prefs.getString("fb_name",null) + "\nEmailModel : " + prefs.getString("fb_email",null));
    }

    public void clearFacebookUserInfo() { // TODO: use when logging out of account
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("fb_first_name");
        editor.remove("fb_last_name");
        editor.remove("fb_email");
        editor.remove("fb_gender");
        editor.remove("fb_profileURL");
        editor.apply(); // This line is IMPORTANT !!!
        Log.d("PrefUtils: ", "Facebook UserModel Info Cleared");
    }

    public static void setArtistEmail(Activity activity, String artistEmail) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("artist_email", artistEmail);
        editor.apply();
        Log.d("Artist EmailModel: ", artistEmail);
    }

    public static String getArtistEmail(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains("artist_email")) {
            String artistEmail = prefs.getString("artist_email", null);
            Log.d("Artist EmailModel: ", artistEmail);
            return artistEmail;
        } else {
            return "Enter an artist email.";
        }
    }

    public static void setRecordingDescription(Activity activity, String recordingDescription) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("recording_description", recordingDescription);
        editor.apply();
        Log.d("Recording Description: ", recordingDescription);
    }

    public static String getRecordingDescription(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains("recording_description")) {
            String recordingDescription = prefs.getString("recording_description", null);
            Log.d("Recording Description: ", recordingDescription);
            return recordingDescription;
        } else {
            return "Enter a recording description.";
        }
    }

    public static void setArtistName(Activity activity, String artistName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("artist_name", artistName);
        editor.apply();
        Log.d("Artist Name: ", artistName);
    }

    public static String getArtistName(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains("artist_name")) {
            String artistName = prefs.getString("artist_name", null);
            Log.d("Artist Name: ", artistName);
            return artistName;
        } else {
            return "Enter an artist name.";
        }
    }

    public static void setRecordingVenue(Activity activity, String recordingVenue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("recording_venue", recordingVenue);
        editor.apply();
        Log.d("Recording Venue: ", recordingVenue);
    }

    public static String getRecordingVenue(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains("recording_venue")) {
            String recordingVenue = prefs.getString("recording_venue", null);
            Log.d("Recording Venue: ", recordingVenue);
            return recordingVenue;
        } else {
            return "Enter a recording venue";
        }
    }

    public static void setBandInfo(Activity activity, String artistEmail, String recordingDescription, String artistName, String recordingVenue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("artist_email", artistEmail);
        editor.putString("recording_description", recordingDescription);
        editor.putString("artist_name", artistName);
        editor.putString("recording_venue", recordingVenue);
        editor.apply();
        Log.d("Artist EmailModel: ", artistEmail);
        Log.d("Recording Description: ", recordingDescription);
        Log.d("Artist Name: ", artistName);
        Log.d("Recording Venue: ", recordingVenue);
    }

    public static void setBandInfo(Activity activity, BandInfoModel bandInfoModel) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("artist_email", bandInfoModel.getArtistEmail());
        editor.putString("recording_description", bandInfoModel.getRecordingDescription());
        editor.putString("artist_name", bandInfoModel.getArtistName());
        editor.putString("recording_venue", bandInfoModel.getRecordingVenue());
        editor.apply();
        Log.d("Artist EmailModel: ", bandInfoModel.getArtistEmail());
        Log.d("Recording Description: ", bandInfoModel.getRecordingDescription());
        Log.d("Artist Name: ", bandInfoModel.getArtistName());
        Log.d("Recording Venue: ", bandInfoModel.getRecordingVenue());
    }

    public static String getBandInfo(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        String artistEmail = "{Artist EmailModel: " + prefs.getString("artist_email", null) + "}\n";
        String recordingDescription = "{Recording Description: " + prefs.getString("recording_description", null) + "}\n";
        String artistName = "{Artist Name: " + prefs.getString("artist_name", null) + "}\n";
        String recordingVenue = "{Recording Venue: " + prefs.getString("recording_venue", null) + "}";

        return artistEmail + recordingDescription + artistName + recordingVenue;
    }

    public static String[] getBandData(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        return new String[]{prefs.getString("artist_email", null),
                prefs.getString("recording_description", null),
                prefs.getString("artist_name", null),
                prefs.getString("recording_venue", null)};
    }

    public static void writeUUID(String UUID) {
        String path = Environment.getExternalStorageDirectory() + "/dSoundBoySettings/settings.json";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid", UUID);
            try (FileWriter file = new FileWriter(path)) {
                file.write(jsonObject.toString());
                Log.d("Success Write UUID", jsonObject.toString());
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String readUUID() {
        String path = Environment.getExternalStorageDirectory() + "/dSoundBoySettings/settings.json";
        String UUID = null;
        try {
            FileReader fileReader = new FileReader(path);
            String json = fileReader.toString();
            JSONObject jsonObject = new JSONObject(json);
            UUID = jsonObject.getString("uuid");
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        Log.d("Success Read UUID", UUID);
        return UUID;
    }
}
