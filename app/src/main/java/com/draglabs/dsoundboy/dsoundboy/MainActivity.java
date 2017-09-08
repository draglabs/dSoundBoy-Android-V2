package com.draglabs.dsoundboy.dsoundboy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.viralypatel.sharedpreferenceshelper.lib.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity {

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

    private String emailText;
    private String descriptionText;
    private String artistNameText;
    private String venueText;
    private String[] bandInfo;

    private Intent enterInfoIntent;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private Bundle bundle;

    private int activityMainViewCount;

    private SharedPreferencesHelper sharedPreferencesHelper;

    private Recorder recorder;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences sharedPreferences = this.getSharedPreferences("com.draglabs.dsoundboy.dsoundboy", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        //SharedPreferences.Editor sharedPreferencesEditor = getPreferences(MODE_PRIVATE).edit();
        bundle = getIntent().getExtras();
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        FacebookSdk.setApplicationId("147689855771285");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //AccessToken.setCurrentAccessToken(null);
        //Profile.setCurrentProfile(null);

        about = (Button)findViewById(R.id.about);
        contact = (Button)findViewById(R.id.contact);
        submit = (Button)findViewById(R.id.submit);
        //submit.setEnabled(false);
        toggleButton(submit);
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        //chronometer.setFormat("HH:MM:SS:ss");
        startStop = (Button)findViewById(R.id.start_stop);
        startStopClickCount = 0;
        //startStop.setEnabled(false);
        toggleButton(startStop);
        logoLink = (ImageButton)findViewById(R.id.logo_link);
        reset = (Button)findViewById(R.id.clear);
        //reset.setEnabled(false);
        toggleButton(reset);
        recordingImage = (ImageView)findViewById(R.id.recording_image);
        recordingImage.setVisibility(View.INVISIBLE);
        enterInfo = (Button)findViewById(R.id.enter_info);
        enterInfo.setEnabled(false);
        login = (ToggleButton)findViewById(R.id.login_activity_button);
        viewRecordings = (Button)findViewById(R.id.button_view_recordings);
        createJam = (Button)findViewById(R.id.button_create_jam);
        joinJam = (Button)findViewById(R.id.button_join_jam);

        // TODO: FIX! WHY ISN'T THIS WORKING?!?!?!?!?!
        //sharedPreferencesEditor.putInt(getResources().getResourceName(R.integer.activity_main_launch_count), 1);
        sharedPreferencesEditor.putInt("activity_main_launch_count", 1);
        sharedPreferencesEditor.apply();
        //sharedPreferencesHelper.putInt(getResources().getResourceName(R.integer.activity_main_launch_count), 1);
        activityMainViewCount = getResources().getInteger(R.integer.activity_main_launch_count);
        Toast.makeText(this, "value: " + activityMainViewCount, Toast.LENGTH_LONG).show();

        // check activity_main_launch_count values

        setBandInfo();
        bandInfo = createArray(emailText, descriptionText, artistNameText, venueText); // can't be null values below, maybe instantiate elsewhere?
        recorder = new Recorder(this, bandInfo, MainActivity.this);

        String callingClass = null;
        if (getIntent().getStringExtra("callingClass") != null) {
            callingClass = getIntent().getStringExtra("callingClass");

            if (callingClass.equals("LoginActivity")) {
                //enterInfo.setEnabled(getIntent().getBooleanExtra());
                enterInfo.setEnabled(true);
                startStop.setEnabled(false);
                reset.setEnabled(false);
                submit.setEnabled(false);
                login.setEnabled(false); // necessary?
                login.setChecked(true);
            }
            if (callingClass.equals("EnterInfo")) {
                enterInfo.setEnabled(false);
                startStop.setEnabled(true);
                reset.setEnabled(false);
                submit.setEnabled(false);
                login.setEnabled(false); // necessary?
            }
        }
        Toast.makeText(this, "" + Profile.getCurrentProfile(), Toast.LENGTH_LONG).show();
        //System.out.println(AccessToken.getCurrentAccessToken());
        if (AccessToken.getCurrentAccessToken() != null) {
            login.setEnabled(true); // necessary?
            login.setChecked(true);
            enterInfo.setEnabled(true);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public void clickAbout(View view) {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    public void clickContact(View view) {
        Intent intent = new Intent(this, Contact.class);
        startActivity(intent);
    }

    public void clickSubmit(View view) {
        Snackbar.make(view, "Submitting Recording to Jam.", Snackbar.LENGTH_LONG).show();
        // TODO: more
        // TODO: finalize recording, close all buffers
        // TODO: go through folder to see which recording were from the current session, and upload all of those
        setBandInfo();

        String pathname = null;
        int resultCodeUpload = APIutils.uploadToS3(pathname);
        int resultCodeJamSolo = APIutils.soloUpload(Profile.getCurrentProfile().getId());
    }

    public void clickStartStop(View view) {
        startStopClickCount++;
        reset.setEnabled(true);
        submit.setEnabled(true);
        // TODO: uncomment following line once Recorder object is added here and comment out lines after
        //recorder.startStopRecording(startStopClickCount, chronometer, recordingImage);
        if (startStopClickCount % 2 != 0) {
            //recorder.startStopRecording(startStopClickCount, chronometer, recordingImage);
            recorder.startRecording();

            Toast.makeText(this, "Started recording.", Toast.LENGTH_LONG).show();
            chronometer.start();
            recordingImage.setVisibility(View.VISIBLE);
            submit.setEnabled(false);
            // TODO: start recording program, append to file if started again
        } else {
            //recorder.startStopRecording(startStopClickCount, chronometer, recordingImage);
            recorder.stopRecording();

            Toast.makeText(this, "Stopped recording.", Toast.LENGTH_LONG).show();
            chronometer.stop();
            recordingImage.setVisibility(View.INVISIBLE);
            submit.setEnabled(true);
            // TODO: stop recording program
        }
    }

    public void clickReset(View view) {
        // TODO: when to save recordings
        recorder.resetRecording();

        chronometer.stop();
        recordingImage.setVisibility(View.INVISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        startStopClickCount++;
    }

    public void clickLogoLink(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"));
        startActivity(browserIntent);
    }

    public void clickEnterInfo(View view) {
        // TODO: intent to open new activity with text fields; not necessary to record; will set title to default
        startStop.setEnabled(true);
        enterInfoIntent = new Intent(this, EnterInfo.class);
        startActivity(enterInfoIntent);
    }

    public void clickLogin(View view) {
        login.toggle();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void clickViewRecordings(View view) {
        Intent intent = new Intent(this, ListOfRecordings.class);
        startActivity(intent);
    }

    public void clickCreateJam(View view) {
        Snackbar.make(view, "Function under construction.", Snackbar.LENGTH_LONG).show();

        //LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        //locationManager.requestLocationUpdates(provider, 5000, 10, this);
        Location location = new Location(provider);
        int resultCodeStartJam = APIutils.startJam(location);
    }

    public void clickJoinJam(View view) {
        Snackbar.make(view, "Function under construction.", Snackbar.LENGTH_LONG).show();

        int resultCodeJoinJam = APIutils.joinJam();
    }

    private void setBandInfo() {
        /*Intent enterInfoIntent = getIntent();
        this.emailText = enterInfoIntent.getStringExtra("emailText");
        this.descriptionText = enterInfoIntent.getStringExtra("descriptionText");
        this.artistNameText = enterInfoIntent.getStringExtra("artistNameText");
        this.venueText = enterInfoIntent.getStringExtra("venueText");*/
        if (bundle == null) {
            Toast.makeText(this, "Please Enter Info.", Toast.LENGTH_LONG);
        } else {
            this.emailText = bundle.getString("emailText");
            this.descriptionText = bundle.getString("descriptionText");
            this.artistNameText = bundle.getString("artistNameText");
            this.venueText = bundle.getString("venueText");
            //BandInfo bandInfo = EnterInfo.getBandInfo();

            Toast.makeText(this, emailText + "\n" + descriptionText + "\n" + artistNameText + "\n" + venueText, Toast.LENGTH_LONG).show();
        }
    }

    private String[] createArray(String _0, String _1, String _2, String _3) {
        return new String[]{_0, _1, _2, _3};
    }

    private int testButtonValue(Button buttonToTest) {
        if (buttonToTest.getText().equals("Start/Stop")) {
            return R.bool.button_start_stop_enabled;
        }
        if (buttonToTest.getText().equals("Reset")) {
            return R.bool.button_reset_enabled;
        }
        if (buttonToTest.getText().equals("Submit")) {
            return R.bool.button_reset_enabled;
        }
        else {
            return 0;
        }
    }

    private void setButtonValue(Button buttonToSet, boolean value) {
        //SharedPreferences sharedPreferences = this.getSharedPreferences("com.draglabs.dsoundbody.dsoundboy", Context.MODE_PRIVATE);
        //SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (buttonToSet.getText().equals("Start/Stop")) {
            String buttonValueName = this.getResources().getResourceEntryName(R.bool.button_start_stop_enabled);
            //sharedPreferencesEditor.putBoolean(R.bool.button_start_stop_enabled, value);
            sharedPreferencesEditor.putBoolean(buttonValueName, value);
            sharedPreferencesEditor.apply();
        }
        if (buttonToSet.getText().equals("Reset")) {
            String buttonValueName = this.getResources().getResourceEntryName(R.bool.button_reset_enabled);
            boolean buttonValue = this.getResources().getBoolean(R.bool.button_reset_enabled);
            //sharedPreferencesEditor.putBoolean(buttonValue, value);
            sharedPreferencesEditor.putBoolean(buttonValueName, value);
            sharedPreferencesEditor.apply();
        }
        if (buttonToSet.getText().equals("Submit")) {
            String buttonValueName = this.getResources().getResourceEntryName(R.bool.button_submit_enabled);
            sharedPreferencesEditor.putBoolean(buttonValueName, value);
            sharedPreferencesEditor.apply();
        }
    }

    private void toggleButton(Button buttonToToggle) {
        if (testButtonValue(buttonToToggle) == 0 && !buttonToToggle.isEnabled()) {
            setButtonValue(buttonToToggle, true);
            buttonToToggle.setEnabled(true);
        }
        else {
            setButtonValue(buttonToToggle, false);
            buttonToToggle.setEnabled(false);
        }
    }

}