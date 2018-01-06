package com.draglabs.dsoundboy.dsoundboy.Routines

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.*
import com.draglabs.dsoundboy.dsoundboy.Activities.AboutActivity
import com.draglabs.dsoundboy.dsoundboy.Activities.ContactActivity
import com.draglabs.dsoundboy.dsoundboy.Activities.EnterInfoActivity
import com.draglabs.dsoundboy.dsoundboy.Activities.ListOfJamsActivity
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Utils.*
import omrecorder.Recorder
import java.util.*

/**
 * The home routine which shows all the recording screen stuff
 * Created by davrukin on 11/3/17.
 * @author Daniel Avrukin
 */
class HomeRoutineKt(val buttons: HashMap<String, Any>, private val activity: Activity, private val context: Context, path: String, recordButton: Button) {

        /**
     * Constructor for the Home Routine
     * @param buttons the buttons seen on the UI
     * @param activity the activity calling the routine
     * @param context the context calling the routine
    */

        /**
         * Returns the UI elements
         * @return buttons
         */

    private var prefUtils: PrefUtils? = null
    var recorderUtils: RecorderUtils? = null
    val recorder: Recorder

    init {
        this.prefUtils = PrefUtils(activity)
        this.recorderUtils = RecorderUtils(context, null, activity)
        this.recorder = recorderUtils!!.setupRecorder(path, recordButton)
    }

    /**
     * Opens the About Activity
     */
    fun clickAbout() {
        val intent = Intent(context, AboutActivity::class.java)
        activity.startActivity(intent)
    }

    /**
     * Opens the Contact Activity
     */
    fun clickContact() {
        val intent = Intent(context, ContactActivity::class.java)
        activity.startActivity(intent)
    }

    /**
     * Opens the Enter Info Activity
     */
    fun clickEnterInfo() {
        val intent = Intent(context, EnterInfoActivity::class.java)
        activity.startActivity(intent)
    }

    /**
     * Opens the View Recordings Activity
     */
    fun clickViewRecordings() {
        // APIutils.getUserActivity(this, uniqueUserID, this);
        val intent = Intent(activity, ListOfJamsActivity::class.java)
        activity.startActivity(intent)
    }

    /**
     * Goes to the company website when the logo is clicked
     */
    fun clickLogoLink() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"))
        activity.startActivity(browserIntent)
    }

    /**
     * Starts recording audio
     * @param chronometer the chronometer
     */
    fun clickRec(chronometer: Chronometer) {
        //createJam()

        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        //recorderUtils.startRecording(activity);
        //recorderUtils.startRecorderNew(activity);
        recorderUtils!!.startRecording(recorder)

        Toast.makeText(context, "Started recording.", Toast.LENGTH_LONG).show()
    }

    /**
     * Stops recording audio
     * @param view the view calling the method
     * @param chronometer stops the chronometer
     * @param recordingStartTime submits the recording's start time to the server
     * @param recordingEndTime submits the recording's end time to the server
     */
    fun clickStop(context: Context, view: View, chronometer: Chronometer, startTime: Date, endTime: Date) {
        //recorderUtils.stopRecording();
        recorderUtils!!.stopRecording(recorder)
        chronometer.stop()

        Toast.makeText(context, "Stopped recording.", Toast.LENGTH_LONG).show()

        submitToServer(context, view, RecorderUtils(context, null, activity), startTime, endTime)
    }

    /**
     * Submits the file to the server
     * @param view the view calling the method
     * @param recorderUtils the recorder settings
     * @param recordingStartTime the recording's start time
     * @param recordingEndTime the recording's end time
     */
    private fun submitToServer(context: Context, view: View, recorderUtils: RecorderUtils, startTime: Date?, endTime: Date?) {
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
        val recordingPath = recorderUtils.audioSavePathInDevice
        /*APIutils.jamRecordingUpload(
                context,
                prefUtils!!.uniqueUserID,
                prefUtils!!.jamID,
                recordingPath, PrefUtils.getRecordingDescription(activity),
                recordingStartTime, recordingEndTime,
                view)*/

        val uuid = PrefUtilsKt.Functions().retrieveUUID(context)
        val jamName = PrefUtilsKt.Functions().retrieveJamName(context)
        val jamID = PrefUtilsKt.Functions().retrieveJamID(context)
        APIutilsKt().performUploadJam(context, recordingPath, uuid, jamName, "location", jamID, startTime.toString(), endTime.toString())

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
        APIutilsKt().performNewJam(context, PrefUtilsKt.Functions().retrieveUUID(context), "test_jam", "ActionSpot", 37, -22, "hi")
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
    fun joinJam(jam_pin_view: TextView) {
        showEnterJamPinDialog(context) // doesn't show keyboard automatically right away, it should
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
    private fun showEnterJamPinDialog(context: Context) {
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

            APIutilsKt().performJoinJam(context, jamPinEntered, PrefUtilsKt.Functions().retrieveUUID(context)) // TODO: setup checker inside of the retriever to see if there is one
        }

        val dialog = builder.create()
        dialog.show()
        // TODO: enter jam pin here and return it
    }

    private fun checkUUID(prefUtils: PrefUtils, activity: Activity, context: Context): String {
        return if (prefUtils.hasUniqueUserID()) {
            Log.v("Checked UUID:", prefUtils.uniqueUserID)
            prefUtils.uniqueUserID
        } else {
            APIutils.registerUser(activity, context)
            Log.v("Refreshed UUID:", prefUtils.uniqueUserID)
            prefUtils.uniqueUserID
        }
    }



}
