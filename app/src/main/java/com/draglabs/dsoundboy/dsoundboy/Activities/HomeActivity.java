package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.annotation.TargetApi;
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
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.draglabs.dsoundboy.dsoundboy.Accessories.Recorder;
import com.draglabs.dsoundboy.dsoundboy.BuildConfig;
import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Routines.HomeRoutine;
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
import java.util.HashMap;

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
    private HomeRoutine homeRoutine;

    private HashMap<String, Object> buttons;
    private ImageButton pauseButton;
    private ImageButton recButton;
    private TextView recButtonText;
    private ImageButton joinButton;
    private TextView joinButtonText;
    private ImageView pauseImage;

    private Toolbar toolbar;

    private boolean isRecording;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefUtils = new PrefUtils(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //about = (Button)findViewById(R.id.about);
        //contact = (Button)findViewById(R.id.contact);
        //submit = (Button)findViewById(R.id.submit);

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
        bandInfo = PrefUtils.getBandData(this);
        recorder = new Recorder(this, bandInfo, this);

        Toast.makeText(this, "" + Profile.getCurrentProfile(), Toast.LENGTH_LONG).show();
        //System.out.println(AccessToken.getCurrentAccessToken());

        pauseButton = (ImageButton)findViewById(R.id.pause_button); // doesn't actually pause, stops recording and uploads
        recButton = (ImageButton)findViewById(R.id.rec_button);
        recButtonText = (TextView) findViewById(R.id.rec_text);
        joinButton = (ImageButton)findViewById(R.id.join_button);
        joinButtonText = (TextView)findViewById(R.id.join_text);
        chronometer = (Chronometer)findViewById(R.id.chronometer_text);
        pauseImage = (ImageView)findViewById(R.id.pause_image);

        isRecording = false;
        recButton.setOnClickListener(view -> {
            if (isRecording == false) {
                clickRec(view); // for future:
            } else {
                clickExitJam(view);
            }
        });
        pauseButton.setOnClickListener(view -> {
            if (isRecording == true) {
                clickStop(view);
            }
            else {
                Toast.makeText(this, "Please start or enter a jam", Toast.LENGTH_LONG).show();
                // TODO: maybe make async so recording starts again quickly
            }
        });
        joinButton.setOnClickListener(view -> {
            if (isRecording == false) {
                clickJoin(view);
            } else {
                Toast.makeText(this, "Please exit current jam", Toast.LENGTH_LONG).show();
            }
        });

        buttons = new HashMap<>();

        buttons.put("recButton", recButton);
        buttons.put("recButtonText", recButtonText);
        buttons.put("joinButton", joinButton);
        buttons.put("joinButtonText", joinButtonText);
        buttons.put("chronometer", chronometer);
        buttons.put("pauseButton", pauseButton);
        buttons.put("pauseImage", pauseImage);

        homeRoutine = new HomeRoutine(buttons, this, this);
    }

    public void clickAbout(View view) {
        homeRoutine.clickAbout();
    }

    public void clickContact(View view) {
        homeRoutine.clickContact();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void clickRec(View view) {
        isRecording = true;
        recordingStartTime = new Date();
        homeRoutine.clickRec(chronometer, recorder);
        recButtonText.setText("Exit");
        buttons.replace("recButtonText", recButtonText);
        pauseImage.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void clickStop(View view) {
        isRecording = false;
        recordingEndTime = new Date();
        homeRoutine.clickStop(view, recorder, chronometer, recordingStartTime, recordingEndTime);
        recButtonText.setText("Rec");
        buttons.replace("recButtonText", recButtonText);
        pauseImage.setVisibility(View.INVISIBLE);
    }

    public void clickExitJam(View view) {
        pauseImage.setVisibility(View.INVISIBLE);
        clickStop(view);
        homeRoutine.exitJam();
    }

    public void clickJoin(View view) {
        homeRoutine.joinJam();
    }

    public void clickLogoLink(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"));
        startActivity(browserIntent);
    }

    public void clickEnterInfo(View view) {
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

    public void uniqueUserIDset() {
        String uniqueUserID = prefUtils.getUniqueUserID();
        Log.v("Unique User ID (MA): ", uniqueUserID + "");
        // TODO: Set it as a variable somewhere? Anyway the Jams activity sees this info too
    }

    /*public void clickJoinJam(View view) {
        homeRoutine.clickJoinJam();
    }*/

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

    private String[] createArray(String _0, String _1, String _2, String _3) {
        return new String[]{_0, _1, _2, _3};
    }
}