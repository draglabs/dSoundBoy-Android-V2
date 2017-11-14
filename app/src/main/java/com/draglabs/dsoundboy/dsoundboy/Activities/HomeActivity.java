package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.Manifest;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

public class HomeActivity extends AppCompatActivity implements CallbackListener {

    private Button about;
    private Button contact;
    private Button enterInfo;
    private Button viewRecordings;

    private String emailText;
    private String descriptionText;
    private String artistNameText;
    private String venueText;
    private String[] bandInfo;
    private String jamID;

    private Intent enterInfoIntent;

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

    private Chronometer chronometer;
    private ImageButton pauseButton;
    private ImageButton recButton;
    private TextView recButtonText;
    private ImageButton joinButton;
    private TextView joinButtonText;
    private ImageView pauseImage;

    private Toolbar toolbar;
    private String[] settingsMenuItemTitles;
    private ListView settingsMenuItemList;
    private DrawerLayout settingsMenuDrawerLayout;

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },10);
        } else {
            recorder = new Recorder(this, bandInfo, this);
        }

        Toast.makeText(this, "" + Profile.getCurrentProfile(), Toast.LENGTH_LONG).show();
        //System.out.println(AccessToken.getCurrentAccessToken());

        pauseButton = (ImageButton)findViewById(R.id.pause_button); // doesn't actually pause, stops recording and uploads
        recButton = (ImageButton)findViewById(R.id.rec_button);
        recButtonText = (TextView) findViewById(R.id.rec_text);
        joinButton = (ImageButton)findViewById(R.id.join_button);
        joinButtonText = (TextView)findViewById(R.id.join_text);
        chronometer = (Chronometer)findViewById(R.id.chronometer_text);
        pauseImage = (ImageView)findViewById(R.id.pause_image);

        settingsMenuItemTitles = getResources().getStringArray(R.array.menu_item_names);
        settingsMenuDrawerLayout = (DrawerLayout)findViewById(R.id.home_layout);
        settingsMenuItemList = (ListView)findViewById(R.id.navigation_view_1_list_view);

        settingsMenuItemList.setAdapter(new ArrayAdapter<>(this, R.layout.navigation_view_1, settingsMenuItemTitles));
        settingsMenuItemList.setOnItemClickListener((parent, view, position, id) ->
                homeRoutine.selectItem(position, settingsMenuItemTitles, settingsMenuDrawerLayout, settingsMenuItemList));

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

        String audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings/recordedAudio.wav";
        int color = getResources().getColor(R.color.colorDLRed1);
        AndroidAudioRecorder.with(this)
                .setFilePath(audioSavePathInDevice)
                .setColor(color)
                .setRequestCode(0)
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.MONO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(true) // set on or off by default? keep it as a setting
                .setKeepDisplayOn(true)
                .record();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Great! User has recorded and saved the audio file
                // upload it here?
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void clickStop(View view) { // TODO: move all this logic into homeRoutine
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
        //startStop.setEnabled(true);
        enterInfoIntent = new Intent(this, EnterInfoActivity.class);
        startActivity(enterInfoIntent);
    }

    public void clickLogin(View view) {
        //login.toggle();
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