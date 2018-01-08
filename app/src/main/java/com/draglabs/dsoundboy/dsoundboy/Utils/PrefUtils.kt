package com.draglabs.dsoundboy.dsoundboy.Utils

import android.app.Activity
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener
import com.draglabs.dsoundboy.dsoundboy.Models.BandInfoModel
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*

/**
 * Created by davrukin on 9/18/17.
 * <br></br>
 * Tutorial link: https://freakycoder.com/android-notes-24-how-to-facebook-login-and-get-user-information-8d6df0350f1c
 */

@Deprecated("")
class PrefUtils
// TODO: add data for "Enter Band And/Or Recording Info"

(private val activity: Activity) {
    private val callbacks: MutableList<CallbackListener>

    private val UNIQUE_USER_ID = "unique_user_id"
    private val JAM_PIN = "jam_pin"
    private val JAM_ID = "jam_id"
    private val JAM_START_TIME = "jam_start_time"
    private val JAM_END_TIME = "jam_end_time"
    private val COLLABORATORS = "collaborators"
    private val USER_ACTIVITY = "user_activity"
    private val JAM_DETAILS = "jam_details"
    private val FB_ACCESS_TOKEN = "fb_access_token"

    val jamID: String?
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val jamID = sharedPreferences.getString(JAM_ID, null)
            Log.v("Gotten Jam ID: ", jamID)
            return jamID
        }

    val jamPIN: String?
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val jamPIN = sharedPreferences.getString(JAM_PIN, null)
            Log.v("Gotten Jam PIN: ", jamPIN)
            return jamPIN
        }

    val uniqueUserID: String
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val UUID = sharedPreferences.getString(UNIQUE_USER_ID, "")
            Log.d("Gotten UUID: ", UUID)
            return UUID
        }

    val jamStartTime: String?
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            return sharedPreferences.getString(JAM_START_TIME, null)
        }

    val collaborators: String?
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            return sharedPreferences.getString(COLLABORATORS, null)
        }

    // 3558, 1356(5424), 1433(5732)
    val userActivity: String?
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val userActivity = sharedPreferences.getString(USER_ACTIVITY, null)
            Log.d("Gotten User Activity: ", userActivity)
            return userActivity
        }

    val jamDetails: String?
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val jamDetails = sharedPreferences.getString(JAM_DETAILS, null)
            Log.d("Jam Details: ", jamDetails)
            return jamDetails
        }

    val jamEndTime: String?
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            return sharedPreferences.getString(JAM_END_TIME, null)
        }

    val accessToken: String?
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            return sharedPreferences.getString(FB_ACCESS_TOKEN, null)
        }

    init {
        callbacks = ArrayList()
    }

    fun addListener(callback: CallbackListener) {
        callbacks.add(callback)
    }

    fun uniqueUserIDset() {
        for (callback in callbacks) {
            callback.uniqueUserIDset()
        }
    }

    fun jamPINset() {
        for (callback in callbacks) {
            callback.jamPINset()
        }
    }

    fun jamIDset() {
        for (callback in callbacks) {
            callback.jamIDset()
        }
    }

    fun jamStartTimeSet() {
        for (callback in callbacks) {
            callback.jamStartTimeSet()
        }
    }

    fun jamEndTimeSet() {
        for (callback in callbacks) {
            callback.jamEndTimeSet()
        }
    }

    fun getCollaboratorsSet() {
        for (callback in callbacks) {
            callback.getCollaboratorsSet()
        }
    }

    fun getUserActivitySet() {
        for (callback in callbacks) {
            callback.getUserActivitySet()
        }
    }

    fun getJamDetailsSet() {
        for (callback in callbacks) {
            callback.getJamDetailsSet()
        }
    }

    fun getAccessTokenSet() {
        for (callback in callbacks) {
            callback.getAccessTokenSet()
        }
    }

    fun saveJamID(jamID: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(JAM_ID, jamID)
        editor.apply()
        Log.v("Saved Jam ID: ", jamID + "")
        jamIDset()
    }

    fun clearJamID() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove(JAM_ID)
        editor.apply()
    }

    fun saveJamPIN(jamPIN: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(JAM_PIN, jamPIN)
        editor.apply()
        Log.v("Saved Jam PIN: ", jamPIN + "")
        jamPINset()
    }

    fun hasJamPIN(): Boolean {
        return jamPIN != null
    }

    fun clearJamPIN() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove(JAM_PIN)
        editor.apply()
    }

    fun saveUniqueUserID(uniqueUserID: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(UNIQUE_USER_ID, uniqueUserID)
        editor.apply()
        Log.d("Saved UUID: ", uniqueUserID)
        uniqueUserIDset()
    }

    fun hasUniqueUserID(): Boolean { // TODO: rename isAuthorized?
        return uniqueUserID != null
    }

    fun clearUniqueUserID() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove(UNIQUE_USER_ID)
        editor.apply()
    }

    fun saveJamStartTime(jamStartTime: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(JAM_START_TIME, jamStartTime)
        editor.apply()
        Log.v("Saved Jam Start Time: ", jamStartTime + "")
        jamStartTimeSet()
    }

    fun clearJamStartTime() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove(JAM_START_TIME)
        editor.apply()
    }

    fun saveCollaborators(collaborators: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(COLLABORATORS, collaborators)
        editor.apply()
        Log.v("Saved Collaborators: ", collaborators + "")
        getCollaboratorsSet()
    }

    fun clearCollaborators() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove(COLLABORATORS)
        editor.apply()
    }

    fun saveUserActivity(userActivity: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(USER_ACTIVITY, userActivity)
        editor.apply()
        Log.v("Saved User Activity: ", userActivity + "")
        getUserActivitySet()
    }

    fun clearUserActivity() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove(USER_ACTIVITY)
        editor.apply()
    }

    fun saveJamDetails(jamDetails: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(JAM_DETAILS, jamDetails)
        editor.apply()
        getJamDetailsSet()
    }

    fun saveJamEndTime(jamEndTime: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(JAM_END_TIME, jamEndTime)
        editor.apply()
        jamEndTimeSet()
    }

    fun clearJamEndTime() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove(JAM_END_TIME)
        editor.apply()
    }

    fun saveAccessToken(token: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.putString(FB_ACCESS_TOKEN, token)
        editor.apply()
        Log.v("Saved Access Token: ", accessToken!! + "")
        getAccessTokenSet()
    }

    fun clearToken() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove("fb_access_token")
        editor.apply()
    }

    fun saveFacebookUserInfo(first_name: String, last_name: String, email: String, gender: String, profileURL: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = prefs.edit()
        editor.putString("fb_first_name", first_name)
        editor.putString("fb_last_name", last_name)
        editor.putString("fb_email", email)
        editor.putString("fb_gender", gender)
        editor.putString("fb_profileURL", profileURL)
        editor.apply() // This line is IMPORTANT !!!
        Log.d("dSoundBoy", "Shared Name : $first_name\nLast Name : $last_name\nEmailModel : $email\nGender : $gender\nProfile Pic : $profileURL")
    }

    fun getFacebookUserInfo() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        Log.d("dSoundBoy", "Name : " + prefs.getString("fb_name", null) + "\nEmailModel : " + prefs.getString("fb_email", null))
    }

    fun clearFacebookUserInfo() { // TODO: use when logging out of account
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = prefs.edit()
        editor.remove("fb_first_name")
        editor.remove("fb_last_name")
        editor.remove("fb_email")
        editor.remove("fb_gender")
        editor.remove("fb_profileURL")
        editor.apply() // This line is IMPORTANT !!!
        Log.d("PrefUtils: ", "Facebook UserModel Info Cleared")
    }

    companion object {

        fun setArtistEmail(activity: Activity, artistEmail: String) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = prefs.edit()
            editor.putString("artist_email", artistEmail)
            editor.apply()
            Log.d("Artist EmailModel: ", artistEmail)
        }

        fun getArtistEmail(activity: Activity): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            if (prefs.contains("artist_email")) {
                val artistEmail = prefs.getString("artist_email", null)
                Log.d("Artist EmailModel: ", artistEmail)
                return artistEmail
            } else {
                return "Enter an artist email."
            }
        }

        fun setRecordingDescription(activity: Activity, recordingDescription: String) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = prefs.edit()
            editor.putString("recording_description", recordingDescription)
            editor.apply()
            Log.d("Recording Description: ", recordingDescription)
        }

        fun getRecordingDescription(activity: Activity): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            if (prefs.contains("recording_description")) {
                val recordingDescription = prefs.getString("recording_description", null)
                Log.d("Recording Description: ", recordingDescription)
                return recordingDescription
            } else {
                return "Enter a recording description."
            }
        }

        fun setArtistName(activity: Activity, artistName: String) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = prefs.edit()
            editor.putString("artist_name", artistName)
            editor.apply()
            Log.d("Artist Name: ", artistName)
        }

        fun getArtistName(activity: Activity): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            if (prefs.contains("artist_name")) {
                val artistName = prefs.getString("artist_name", null)
                Log.d("Artist Name: ", artistName)
                return artistName
            } else {
                return "Enter an artist name."
            }
        }

        fun setRecordingVenue(activity: Activity, recordingVenue: String) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = prefs.edit()
            editor.putString("recording_venue", recordingVenue)
            editor.apply()
            Log.d("Recording Venue: ", recordingVenue)
        }

        fun getRecordingVenue(activity: Activity): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            if (prefs.contains("recording_venue")) {
                val recordingVenue = prefs.getString("recording_venue", null)
                Log.d("Recording Venue: ", recordingVenue)
                return recordingVenue
            } else {
                return "Enter a recording venue"
            }
        }

        fun setBandInfo(activity: Activity, artistEmail: String, recordingDescription: String, artistName: String, recordingVenue: String) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = prefs.edit()
            editor.putString("artist_email", artistEmail)
            editor.putString("recording_description", recordingDescription)
            editor.putString("artist_name", artistName)
            editor.putString("recording_venue", recordingVenue)
            editor.apply()
            Log.d("Artist EmailModel: ", artistEmail)
            Log.d("Recording Description: ", recordingDescription)
            Log.d("Artist Name: ", artistName)
            Log.d("Recording Venue: ", recordingVenue)
        }

        fun setBandInfo(activity: Activity, bandInfoModel: BandInfoModel) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = prefs.edit()
            editor.putString("artist_email", bandInfoModel.artistEmail)
            editor.putString("recording_description", bandInfoModel.recordingDescription)
            editor.putString("artist_name", bandInfoModel.artistName)
            editor.putString("recording_venue", bandInfoModel.recordingVenue)
            editor.apply()
            Log.d("Artist EmailModel: ", bandInfoModel.artistEmail)
            Log.d("Recording Description: ", bandInfoModel.recordingDescription)
            Log.d("Artist Name: ", bandInfoModel.artistName)
            Log.d("Recording Venue: ", bandInfoModel.recordingVenue)
        }

        fun getBandInfo(activity: Activity): String {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

            val artistEmail = "{Artist EmailModel: " + prefs.getString("artist_email", null) + "}\n"
            val recordingDescription = "{Recording Description: " + prefs.getString("recording_description", null) + "}\n"
            val artistName = "{Artist Name: " + prefs.getString("artist_name", null) + "}\n"
            val recordingVenue = "{Recording Venue: " + prefs.getString("recording_venue", null) + "}"

            return artistEmail + recordingDescription + artistName + recordingVenue
        }

        fun getBandData(activity: Activity): Array<String> {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

            return arrayOf(prefs.getString("artist_email", null), prefs.getString("recording_description", null), prefs.getString("artist_name", null), prefs.getString("recording_venue", null))
        }

        fun writeUUID(UUID: String) {
            val path = Environment.getExternalStorageDirectory().toString() + "/dSoundBoySettings/settings.json"
            try {
                val jsonObject = JSONObject()
                jsonObject.put("uuid", UUID)
                FileWriter(path).use { file ->
                    file.write(jsonObject.toString())
                    Log.d("Success Write UUID", jsonObject.toString())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        fun readUUID(): String? {
            val path = Environment.getExternalStorageDirectory().toString() + "/dSoundBoySettings/settings.json"
            var UUID: String? = null
            try {
                val fileReader = FileReader(path)
                val json = fileReader.toString()
                val jsonObject = JSONObject(json)
                UUID = jsonObject.getString("uuid")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            Log.d("Success Read UUID", UUID)
            return UUID
        }

        @Deprecated("")
        fun writeUUID(UUID: String, environment: Environment) {
            /*val path = environment.getExternalStorageDirectory() + "/dSoundBoySettings/settings.json"
            try {
                val jsonObject = JSONObject()
                jsonObject.put("uuid", UUID)
                FileWriter(path).use({ file ->
                    file.write(jsonObject.toString())
                    Log.d("Success Write UUID", jsonObject.toString())
                })
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }*/

        }

        @Deprecated("", ReplaceWith("null"))
        fun readUUID(environment: Environment): String? {
            /*val path = environment.getExternalStorageDirectory() + "/dSoundBoySettings/settings.json"
            var UUID: String? = null
            try {
                val fileReader = FileReader(path)
                val json = fileReader.toString()
                val jsonObject = JSONObject(json)
                UUID = jsonObject.getString("uuid")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            Log.d("Success Read UUID", UUID)
            return UUID
            */
            return null
        }
    }
}
