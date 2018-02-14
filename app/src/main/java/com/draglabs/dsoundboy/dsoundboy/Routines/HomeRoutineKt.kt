/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Routines

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.draglabs.dsoundboy.dsoundboy.Activities.AboutActivity
import com.draglabs.dsoundboy.dsoundboy.Activities.ContactActivity
import com.draglabs.dsoundboy.dsoundboy.Activities.EnterInfoActivity
import com.draglabs.dsoundboy.dsoundboy.Activities.ListOfJamsActivity
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Utils.*
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.instacart.library.truetime.TrueTime
import io.realm.Realm
import omrecorder.Recorder
import java.util.*

/**
 * The home routine which shows all the recording screen stuff
 * Created by davrukin on 11/3/17.
 * @author Daniel Avrukin
 */
class HomeRoutineKt {

    /**
     * Opens the About Activity
     */
    fun clickAbout(context: Context, activity: Activity) {
        val intent = Intent(context, AboutActivity::class.java)
        activity.startActivity(intent)
    }

    /**
     * Opens the Contact Activity
     */
    fun clickContact(context: Context, activity: Activity) {
        val intent = Intent(context, ContactActivity::class.java)
        activity.startActivity(intent)
    }

    /**
     * Opens the Enter Info Activity
     */
    fun clickEnterInfo(context: Context, activity: Activity) {
        val intent = Intent(context, EnterInfoActivity::class.java)
        activity.startActivity(intent)
    }

    /**
     * Opens the View Recordings Activity
     */
    fun clickViewRecordings(activity: Activity) {
        // APIutils.getUserActivity(this, uniqueUserID, this);
        val intent = Intent(activity, ListOfJamsActivity::class.java)
        activity.startActivity(intent)
    }

    /**
     * Goes to the company website when the logo is clicked
     */
    fun clickLogoLink(activity: Activity) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"))
        activity.startActivity(browserIntent)
    }

    /**
     * Starts recording audio
     * @param chronometer the chronometer
     */
    fun clickRec(context: Context, recorder: Recorder, recorderUtils: RecorderUtils, chronometer: Chronometer) {
        //createJam()

        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        //recorderUtils.startRecording(activity);
        //recorderUtils.startRecorderNew(activity);
        //val time = Date().time.toString()
        //val path = PrefUtilsKt.Functions().retrieveJamName(context) + " " + time
        val path = PrefUtilsKt.Functions().retrieveLocalPath(context)
        LogUtils.debug("ClickRec Path", path)
        //recorder = recorderUtils!!.setupRecorder(path, this.recordButton!!)
        recorderUtils.startRecording(recorder)

        Toast.makeText(context, "Started recording.", Toast.LENGTH_LONG).show()
    }

    /**
     * Stops recording audio
     * @param chronometer stops the chronometer
     */
    fun clickStop(recorder: Recorder, recorderUtils: RecorderUtils, context: Context, chronometer: Chronometer) {
        //recorderUtils.stopRecording();
        recorderUtils.stopRecording(recorder)
        chronometer.stop()

        Toast.makeText(context, "Stopped recording.", Toast.LENGTH_LONG).show()

        //submitToServer(realm, jamID, context, view, startTime, endTime)
    }

    /**
     * Submits the file to the server
     * @param view the view calling the method
     * @param startTime the recording's start time
     * @param endTime the recording's end time
     */
    private fun submitToServer(realm: Realm, jamID: String, context: Context, view: View, startTime: Date?, endTime: Date?) {
        var recordingStartTime = startTime
        var recordingEndTime = endTime

        /*Thread thread = new Thread(() -> APIutils.jamRecordingUpload(uniqueUserID, jamID, "davrukin-test", recordingPath, "notes", recordingStartTime, recordingEndTime, view));
        thread.run();*/

        Snackbar.make(view, "Submitting Recording to Jam.", Snackbar.LENGTH_LONG).show()
        // TODO: more
        // TODO: finalize recording, close all buffers
        // TODO: go through folder to see which recording were from the current session, and upload all of those

        if (recordingStartTime == null) {
            recordingStartTime = Date()
        }
        if (recordingEndTime == null) {
            recordingEndTime = Date()
        }

        //prefUtils = PrefUtils(activity)
        /*APIutils.jamRecordingUpload(
                context,
                prefUtils!!.uniqueUserID,
                prefUtils!!.jamID,
                recordingPath, PrefUtils.getRecordingDescription(activity),
                recordingStartTime, recordingEndTime,
                view)*/
        val uuid = PrefUtilsKt.Functions().retrieveUUID(context)
        APIutilsKt.JamFunctions.performGetActiveJam(context) // submitting now to active jam, before it may have had old information
        val jamName = PrefUtilsKt.Functions().retrieveJamName(context)
        //val jamID = PrefUtilsKt.Functions().retrieveJamID(context)

        val recordingPath = PrefUtilsKt.Functions().retrieveLocalPath(context)

        //APIutilsKt().performUploadJam(realm, context, recordingPath, uuid, jamName, "location", jamID, startTime.toString(), endTime.toString())
        //APIutilsKt.JamFunctions.jamRecordingUpload(context, recordingPath, "hi", startTime.toString(), endTime.toString(), view)
        /*int id = 1;
        NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context);
        notificationCompatBuilder.setContentTitle("Recording Upload").setContentText("Upload in progress").setSmallIcon(R.drawable.drag_labs_logo);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Thread(() -> {
            for (int i = 0; i < 100; i += 5) {
                notificationCompatBuilder.setProgress(100, i, false);
                notificationManager.notify(id, notificationCompatBuilder.build());

                String recordingPath = recorderUtils.getAudioSavePathInDevice();
                APIutils.jamRecordingUpload(
                        prefUtils.getUniqueUserID(),
                        prefUtils.getJamID(),
                        PrefUtils.getRecordingVenue(activity),
                        recordingPath, PrefUtils.getRecordingDescription(activity),
                        recordingStartTimeServer, recordingEndTimeServer,
                        view);

                notificationCompatBuilder.setContentText("Upload complete").setProgress(0, 0, false);
                notificationManager.notify(id, notificationCompatBuilder.build());
            }

        });
        handler.post(runnable);*/

        // TODO: SET CORRECT RECORDING PATH, START, AND END TIME
    }

    /**
     * Creates a jam, shows a new jam PIN if there isn't one already, gets the device's location
     */
    fun createJam(context: Context, jam_pin_view: TextView) {
        val location = getLocation(context)
        val locationName = location[0] as String
        val lat = location[1] as Long
        val lng = location[2] as Long
        APIutilsKt.JamFunctions.performNewJam(context, PrefUtilsKt.Functions().retrieveUUID(context), generateJamName(context), locationName, lat, lng, "hi")
        LogUtils.debug("new pin", PrefUtilsKt.Functions().retrievePIN(context))
        jam_pin_view.text = PrefUtilsKt.Functions().retrievePIN(context)

        //LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        //val provider = LocationManager.GPS_PROVIDER
        //locationManager.requestLocationUpdates(provider, 5000, 10, this);
        //val location = Location(provider)
        //prefUtils = PrefUtils(activity)

        /*if (!prefUtils!!.hasJamPIN()) {
            APIutils.newJam(activity, context, prefUtils!!.uniqueUserID, PrefUtils.getRecordingVenue(activity), PrefUtils.getRecordingDescription(activity), location)
            // may have to implement without a while loop
            prefUtils = PrefUtils(activity)
            showNewJamPinDialog(context, "Jam PIN", prefUtils!!.jamPIN)
        } else {
            Log.v("Current Jam PIN: ", prefUtils!!.jamPIN)
        }*/
    }
    /**
     * Joins a jam, shows dialog to enter a jam PIN
     */
    fun joinJam(context: Context, activity: Activity, jam_pin_view: TextView) {
        showEnterJamPinDialog(context, activity) // doesn't show keyboard automatically right away, it should
        LogUtils.debug("entered pin", PrefUtilsKt.Functions().retrievePIN(context))
        jam_pin_view.text = PrefUtilsKt.Functions().retrievePIN(context)
    }

    /*public void clickJoinJam() {

        APIutils.joinJam(this, prefUtils.getUniqueUserID(), Integer.parseInt(jamPINtext.getText().toString()));
    }*/

    /*private void setBandInfo() {
        this.emailText = PrefUtils.getArtistEmail(activity);
        this.descriptionText = PrefUtils.getRecordingDescription(activity);
        this.artistNameText = PrefUtils.getArtistName(activity);
        this.venueText = PrefUtils.getArtistEmail(activity);

        Toast.makeText(this, emailText + "\n" + descriptionText + "\n" + artistNameText + "\n" + venueText, Toast.LENGTH_LONG).show();
    }*/

    /**
     * Creates the dialog to enter the new jam PIN
     * @param context the app context
     */
    @SuppressLint("InflateParams")
    private fun showEnterJamPinDialog(context: Context, activity: Activity) {
        val inflater = activity.layoutInflater
        val alertLayout = inflater.inflate(R.layout.layout_join_jam_dialog, null)
        val joinJamPin = alertLayout.findViewById<EditText>(R.id.join_jam_enter_pin_text)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enter a Jam PIN")
        builder.setView(alertLayout)
        builder.setCancelable(false)
        builder.setNegativeButton("Cancel") { _, _ ->
            Toast.makeText(context, "Cancel clicked", Toast.LENGTH_LONG).show() }
        builder.setPositiveButton("Done") { _, _ ->
            val jamPinEntered = joinJamPin.text.toString()
            PrefUtilsKt.Functions().storePIN(context, jamPinEntered)
            LogUtils.debug("Jam Pin Entered", jamPinEntered)

            //Log.d("Jam PIN Entered: ", prefUtils!!.jamPIN)
            //val UUID = checkUUID(PrefUtils(activity), activity, context)
            //APIutils.joinJam(activity, context, jamPinEntered, UUID) // TODO: show error if incorrect

            APIutilsKt.JamFunctions.performJoinJam(context, jamPinEntered, PrefUtilsKt.Functions().retrieveUUID(context)) // TODO: setup checker inside of the retriever to see if there is one
        }

        val dialog = builder.create()

        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }

        dialog.show()
        // TODO: enter jam pin here and return it
    }

    private fun generateJamName(context: Context): String {
        val name = PrefUtilsKt.Functions().retrieveFbName(context)
        val dateTimeString = FileUtils().getFormattedDate(TrueTime.now())

        val generatedName = "$name $dateTimeString"
        LogUtils.debug("Generated Jam Name", generatedName)
        return generatedName
    }

    private fun getLocation(context: Context): Array<Any> {
        // e0 = location name: String
        // e1 = lat: Long
        // e2 = lng: Long
        val e0 = "ActionSpot"
        //val e1 = PrefUtilsKt.Functions().retrieveLatitude(context)
        //val e2 = PrefUtilsKt.Functions().retrieveLongitude(context)
        val e1 = 37L
        val e2 = -22L // change these to the real location later
        return arrayOf(e0, e1, e2)
    }

    private fun checkUUID(activity: Activity, context: Context): String {
        var UUID = PrefUtilsKt.Functions().retrieveUUID(context)
        return if (UUID != "not working") {
            Log.v("Checked UUID:", UUID)
            UUID
        } else {
            APIutilsKt.UserFunctions.performRegisterUser(activity, context)
            UUID = PrefUtilsKt.Functions().retrieveUUID(context)
            Log.v("Refreshed UUID:", UUID)
            UUID
        }
    }

    fun clickWifiUploadsButton(view: View) {
        Snackbar.make(view, "Under Construction", Snackbar.LENGTH_LONG).show()

    }

    fun clickLogout() {
        /*if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback {
                AccessToken.setCurrentAccessToken(null)
                LoginManager.getInstance().logOut()
                //finish()
            }).executeAsync()
        }*/
        AccessToken.setCurrentAccessToken(null)
        LoginManager.getInstance().logOut()
    }

}
