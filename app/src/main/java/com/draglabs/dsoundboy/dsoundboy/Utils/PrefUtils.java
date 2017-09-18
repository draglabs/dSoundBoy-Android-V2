package com.draglabs.dsoundboy.dsoundboy.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener;

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

    public void saveJamID(String jamID) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JAM_ID, jamID);
        editor.apply();
    }

    public String getJamID() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString(JAM_ID, null);
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
    }

    public String getJamPIN() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString(JAM_PIN, null);
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
    }

    public String getUniqueUserID() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString(UNIQUE_USER_ID, null);
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
    }

    public String getUserActivity() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString(USER_ACTIVITY, null);
    }

    public void clearUserActivity() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_ACTIVITY);
        editor.apply();
    }

    public void saveJamEndTime(String jamEndTime) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JAM_END_TIME, jamEndTime);
        editor.apply();
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
        editor.putString("fb_access_token", token);
        editor.apply();
        Log.v("Getting user ID: ", getUniqueUserID() + "");
        uniqueUserIDset();
    }

    public String getToken() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString("fb_access_token", null);
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
        Log.d("dSoundBoy", "Shared Name : "+first_name+"\nLast Name : "+last_name+"\nEmail : "+email+"\nGender : "+gender+"\nProfile Pic : "+profileURL);
    }

    public void getFacebookUserInfo() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Log.d("dSoundBoy", "Name : "+prefs.getString("fb_name",null)+"\nEmail : "+prefs.getString("fb_email",null));
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
        Log.d("PrefUtils: ", "Facebook User Info Cleared");
    }
}
