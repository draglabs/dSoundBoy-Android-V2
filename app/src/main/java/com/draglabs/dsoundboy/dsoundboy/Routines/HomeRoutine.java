package com.draglabs.dsoundboy.dsoundboy.Routines;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.Accessories.Recorder;
import com.draglabs.dsoundboy.dsoundboy.Activities.AboutActivity;
import com.draglabs.dsoundboy.dsoundboy.Activities.ContactActivity;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by davrukin on 11/3/17.
 */

public class HomeRoutine {

    private HashMap<String, Object> buttons;
    private Activity activity;
    private Context context;
    private PrefUtils prefUtils;

    public HomeRoutine(HashMap<String, Object> buttons, Activity activity, Context context) {
        this.buttons = buttons;
        this.activity = activity;
        this.context = context;
        this.prefUtils = new PrefUtils(activity);
    }

    public void clickAbout() {
        Intent intent = new Intent(context, AboutActivity.class);
        activity.startActivity(intent);
    }

    public void clickContact() {
        Intent intent = new Intent(context, ContactActivity.class);
        activity.startActivity(intent);
    }

    public void clickRec(Chronometer chronometer, Recorder recorder) {
        createJam();

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        recorder.startRecording(activity);

        Toast.makeText(context, "Started recording.", Toast.LENGTH_LONG).show();
    }

    public void clickStop(View view, Recorder recorder, Chronometer chronometer, Date recordingStartTime, Date recordingEndTime) {
        recorder.stopRecording();
        chronometer.stop();

        Toast.makeText(context, "Stopped recording.", Toast.LENGTH_LONG).show();

        submitToServer(view, recorder, recordingStartTime, recordingEndTime);
    }

    private void submitToServer(View view, Recorder recorder, Date recordingStartTime, Date recordingEndTime) {

        /*Thread thread = new Thread(() -> APIutils.jamRecordingUpload(uniqueUserID, jamID, "davrukin-test", recordingPath, "notes", recordingStartTime, recordingEndTime, view));
        thread.run();*/

        Snackbar.make(view, "Submitting Recording to Jam.", Snackbar.LENGTH_LONG).show();
        // TODO: more
        // TODO: finalize recording, close all buffers
        // TODO: go through folder to see which recording were from the current session, and upload all of those

        if (recordingStartTime == null) {
            recordingStartTime = new Date();
        }
        if (recordingEndTime == null) {
            recordingEndTime = new Date();
        }

        String recordingPath = recorder.getAudioSavePathInDevice();
        APIutils.jamRecordingUpload(
                prefUtils.getUniqueUserID(),
                prefUtils.getJamID(),
                PrefUtils.getRecordingVenue(activity),
                recordingPath, PrefUtils.getRecordingDescription(activity),
                recordingStartTime, recordingEndTime,
                view);

        /*int id = 1;
        NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context);
        notificationCompatBuilder.setContentTitle("Recording Upload").setContentText("Upload in progress").setSmallIcon(R.drawable.drag_labs_logo);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Thread(() -> {
            for (int i = 0; i < 100; i += 5) {
                notificationCompatBuilder.setProgress(100, i, false);
                notificationManager.notify(id, notificationCompatBuilder.build());

                String recordingPath = recorder.getAudioSavePathInDevice();
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

    public void createJam() {
        //LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        //locationManager.requestLocationUpdates(provider, 5000, 10, this);
        Location location = new Location(provider);
        prefUtils = new PrefUtils(activity);

        if (!prefUtils.hasJamPIN()) {
            APIutils.startJam(activity, prefUtils.getUniqueUserID(), PrefUtils.getRecordingVenue(activity), PrefUtils.getRecordingDescription(activity), location);
            // may have to implement without a while loop
        }

        /*String jamPIN = prefUtils.getJamPIN();
        Log.v("Jam PIN: ", jamPIN); // TODO: SHOW JSON RESPONSE IF ERROR AS INDEFINITE SNACKBAR OR TOAST
        String newJamPIN = jamPIN.substring(0, 3) + "-" + jamPIN.substring(3, 6) + "-" + jamPIN.substring(6, 9);
        showNewJamPinDialog(context, "Jam PIN", newJamPIN);*/
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

    private void showNewJamPinDialog(Context context, String title, String jamPIN) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(jamPIN).setTitle(title);
        builder.setNeutralButton("Okay", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String showEnterJamPinDialog(Context context, String title, String jamPIN) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(jamPIN).setTitle(title);
        builder.setNeutralButton("Okay", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        return null;
    }

    public HashMap<String, Object> getButtons() {
        return buttons;
    }
}