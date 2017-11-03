package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.draglabs.dsoundboy.dsoundboy.Accessories.Recorder;
import com.draglabs.dsoundboy.dsoundboy.BuildConfig;
import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.viralypatel.sharedpreferenceshelper.lib.SharedPreferencesHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements CallbackListener {

    private Button about;
    private Button contact;
    private Button submit;
    private Chronometer chronometer;
    private Button startStop;
    private int startStopClickCount;
    private ImageButton logoLink;
    private Button reset;
    private ImageView recordingImage;
    private Button enterInfo;
    private ToggleButton login;
    private Button viewRecordings;
    private Button createJam;
    private Button joinJam;
    private Button exitJam;
    private EditText jamPINtext;
    //private Button start; // is still startStop
    private Button stop;

    private String emailText;
    private String descriptionText;
    private String artistNameText;
    private String venueText;
    private String[] bandInfo;
    private String jamID;

    private Intent enterInfoIntent;

    private int activityMainViewCount;

    private Recorder recorder;

    private LocationManager locationManager;

    private String uniqueUserID;
    private int jamPIN;

    private PrefUtils prefUtils;

    private String recordingPath;
    private Date recordingStartTime;
    private String recordingStartTimeServer;
    private Date recordingEndTime;
    private String recordingEndTimeServer;

    private NotificationManager notificationManager;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefUtils = new PrefUtils(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //about = (Button)findViewById(R.id.about);
        //contact = (Button)findViewById(R.id.contact);
        //submit = (Button)findViewById(R.id.submit);

        chronometer = (Chronometer)findViewById(R.id.chronometer_text);
        //chronometer.setFormat("HH:MM:SS:ss");

        /*viewRecordings = (Button)findViewById(R.id.button_view_recordings);
        createJam = (Button)findViewById(R.id.button_create_jam);
        createJam.setEnabled(false);
        joinJam = (Button)findViewById(R.id.button_join_jam);
        joinJam.setEnabled(false);
        exitJam = (Button)findViewById(R.id.button_exit_jam);
        exitJam.setEnabled(false);
        jamPINtext = (EditText)findViewById(R.id.text_jam_pin);
        stop = (Button)findViewById(R.id.stop);*/

        setBandInfo();
        bandInfo = createArray(emailText, descriptionText, artistNameText, venueText); // can't be null values below, maybe instantiate elsewhere?
        recorder = new Recorder(this, bandInfo, this);

        Toast.makeText(this, "" + Profile.getCurrentProfile(), Toast.LENGTH_LONG).show();
        //System.out.println(AccessToken.getCurrentAccessToken());
    }

    public void clickAbout(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void clickContact(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    public void clickSubmit(View view) {
        Snackbar.make(view, "Submitting Recording to Jam.", Snackbar.LENGTH_LONG).show();
        // TODO: more
        // TODO: finalize recording, close all buffers
        // TODO: go through folder to see which recording were from the current session, and upload all of those
        setBandInfo();

        if (recordingStartTime == null) {
            recordingStartTime = new Date();
        }
        if (recordingEndTime == null) {
            recordingEndTime = new Date();
        }

        int id = 1;
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this);
        notificationCompatBuilder.setContentTitle("Recording Upload").setContentText("Upload in progress").setSmallIcon(R.drawable.drag_labs_logo);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Thread(() -> {
            for (int i = 0; i < 100; i += 5) {
                notificationCompatBuilder.setProgress(100, i, false);
                notificationManager.notify(id, notificationCompatBuilder.build());

                recordingPath = recorder.getAudioSavePathInDevice();
                APIutils.jamRecordingUpload(
                        prefUtils.getUniqueUserID(),
                        prefUtils.getJamID(),
                        PrefUtils.getRecordingVenue(this),
                        recordingPath, PrefUtils.getRecordingDescription(this),
                        recordingStartTimeServer, recordingEndTimeServer,
                        view);

                notificationCompatBuilder.setContentText("Upload complete").setProgress(0, 0, false);
                notificationManager.notify(id, notificationCompatBuilder.build());
            }

        });
        handler.post(runnable);

        // TODO: SET CORRECT RECORDING PATH, START, AND END TIME
    }

    public void clickStart(View view) { // MAKE INTO A LARGE TOGGLE BUTTON? WITH TWO IMAGES FOR EACH MODE
        recordingStartTime = new Date();
        reset.setEnabled(true);
        submit.setEnabled(false);

        Toast.makeText(this, "Started recording.", Toast.LENGTH_LONG).show();

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        recorder.startRecording(this);
        recordingImage.setVisibility(View.VISIBLE);
        stop.setEnabled(true);
    }

    public void clickStop(View view) {
        recordingEndTime = new Date();
        recorder.stopRecording();
        chronometer.stop();

        Toast.makeText(this, "Stopped recording.", Toast.LENGTH_LONG).show();

        Thread thread = new Thread(() -> APIutils.jamRecordingUpload(uniqueUserID, jamID, "davrukin-test", recordingPath, "notes", recordingStartTime, recordingEndTime, view));
        thread.run();

        recordingImage.setVisibility(View.INVISIBLE);
        submit.setEnabled(true);
        startStop.setEnabled(true);

        clickSubmit(view);

    }

    public void clickLogoLink(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"));
        startActivity(browserIntent);
    }

    public void clickEnterInfo(View view) {
        // TODO: intent to open new activity with text fields; not necessary to record; will set title to default
        startStop.setEnabled(true);
        enterInfoIntent = new Intent(this, EnterInfoActivity.class);
        startActivity(enterInfoIntent);
    }

    public void clickLogin(View view) {
        login.toggle();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void clickViewRecordings(View view) {
       // APIutils.getUserActivity(this, uniqueUserID, this);

        Intent intent = new Intent(this, ListOfRecordingsActivity.class);
        startActivity(intent);
    }

    public void clickCreateJam(View view) {
        Snackbar.make(view, "Function under construction.", Snackbar.LENGTH_LONG).show();

        //LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        //locationManager.requestLocationUpdates(provider, 5000, 10, this);
        Location location = new Location(provider);
        prefUtils = new PrefUtils(this);
        APIutils.startJam(this, prefUtils.getUniqueUserID(), PrefUtils.getRecordingVenue(this), PrefUtils.getRecordingDescription(this), location);
        String jamPIN = prefUtils.getJamPIN();
        if (prefUtils.hasJamPIN()) { // what did I want to do here?
            Log.v("Jam PIN: ", jamPIN); // TODO: SHOW JSON RESPONSE IF ERROR AS INDEFINITE SNACKBAR OR TOAST
        } else {

        }
        jamPINtext.setText(jamPIN);
        recordingStartTimeServer = prefUtils.getJamStartTime();
        recordingEndTimeServer = prefUtils.getJamEndTime();
        exitJam.setEnabled(true); // TODO: fix lack of jam pin refreshes, maybe the callback occurs after the display is updated
    }

    public void clickExitJam(View view) {
        jamPINtext.setText(getString(R.string.jam_pin));
        APIutils.exitJam(prefUtils.getUniqueUserID(), prefUtils.getJamID());
    }

    public void uniqueUserIDset() {
        String uniqueUserID = prefUtils.getUniqueUserID();
        Log.v("Unique User ID (MA): ", uniqueUserID + "");
        // TODO: Set it as a variable somewhere? Anyway the Jams activity sees this info too
    }

    public void clickJoinJam(View view) {
        Snackbar.make(view, "Function under construction.", Snackbar.LENGTH_LONG).show();

        APIutils.joinJam(this, prefUtils.getUniqueUserID(), Integer.parseInt(jamPINtext.getText().toString()));
    }

    public void jamIDset() {
        Log.v("Jam ID: ", prefUtils.getJamID() + "");
    }

    public void jamPINset() {
        Log.v("Jam PIN: ", prefUtils.getJamPIN() + "");
    }

    public void jamStartTimeSet() {
        Log.v("Jam Start Time: ", prefUtils.getJamStartTime() + "");
    }

    public void jamEndTimeSet() {
        Log.v("Jam End Time: ", prefUtils.getJamEndTime() + "");
    }

    public void getCollaboratorsSet() {
        Log.v("Collaborators: ", prefUtils.getCollaborators() + "");
    }

    public void getUserActivitySet() {
        Log.v("User Activity: ", prefUtils.getUserActivity() + "");
    }

    @Override
    public void getJamDetailsSet() {
        Log.v("Jam Details: ", prefUtils.getJamDetails() + " ");
    }

    private void setBandInfo() {
        this.emailText = PrefUtils.getArtistEmail(this);
        this.descriptionText = PrefUtils.getRecordingDescription(this);
        this.artistNameText = PrefUtils.getArtistName(this);
        this.venueText = PrefUtils.getArtistEmail(this);

        Toast.makeText(this, emailText + "\n" + descriptionText + "\n" + artistNameText + "\n" + venueText, Toast.LENGTH_LONG).show();
    }

    private String[] createArray(String _0, String _1, String _2, String _3) {
        return new String[]{_0, _1, _2, _3};
    }
}