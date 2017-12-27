package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Routines.HomeRoutine;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;
import com.draglabs.dsoundboy.dsoundboy.Utils.RecorderUtils;
import com.facebook.AccessToken;
import com.facebook.Profile;

import java.util.Date;
import java.util.HashMap;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;

/**
 * The Home Activity after user is authenticated with Facebook and with dlsAPI (Drag Labs Server API)
 */
public class HomeActivity extends AppCompatActivity {

    private String[] bandInfo;
    private RecorderUtils recorderUtils;

    private LocationManager locationManager;

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
    private CharSequence settingsMenuDrawerTitle;
    private CharSequence settingsMenuTitle;
    private ActionBarDrawerToggle settingsMenuDrawerToggle;

    private NavigationView navigationView;

    private boolean isRecording;

    /**
     * onCreate method for the Home Activity
     * @param savedInstanceState the saved instance state
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeUIElements();
        initializeDrawer();
        initializeNavigationView();

        prefUtils = new PrefUtils(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //chronometer.setFormat("HH:MM:SS:ss");

        bandInfo = PrefUtils.getBandData(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },10);
        } else {
            recorderUtils = new RecorderUtils(this, bandInfo, this);
        }

        Toast.makeText(this, "" + Profile.getCurrentProfile(), Toast.LENGTH_LONG).show();
        System.out.println(AccessToken.getCurrentAccessToken());

        isRecording = false;
        recButton.setOnClickListener(view -> {
            if (!isRecording) {
                clickRec(view); // for future:
            } else {
                clickExitJam(view);
            }
        });
        pauseButton.setOnClickListener(view -> {
            if (isRecording) {
                clickStop(view);
            }
            else {
                Toast.makeText(this, "Please start or enter a jam", Toast.LENGTH_LONG).show();
                // TODO: maybe make async so recording starts again quickly
            }
        });
        joinButton.setOnClickListener(view -> {
            if (!isRecording) {
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

        homeRoutine = new HomeRoutine(buttons, this, this, "HomeActivity", new Button(this));
    }

    /**
     * Initializes all UI elements
     */
    private void initializeUIElements() {
        pauseButton = (ImageButton)findViewById(R.id.pause_button); // doesn't actually pause, stops recording and uploads
        recButton = (ImageButton)findViewById(R.id.rec_button);
        recButtonText = (TextView) findViewById(R.id.rec_text);
        joinButton = (ImageButton)findViewById(R.id.join_button);
        joinButtonText = (TextView)findViewById(R.id.join_text);
        chronometer = (Chronometer)findViewById(R.id.chronometer_text);
        pauseImage = (ImageView)findViewById(R.id.pause_image);
    }

    /**
     * Initializes the drawer items and does things when buttons are opened and closed
     */
    private void initializeDrawer() {
        settingsMenuItemTitles = getResources().getStringArray(R.array.menu_item_names);
        settingsMenuDrawerLayout = (DrawerLayout)findViewById(R.id.home_layout);
        settingsMenuItemList = (ListView)findViewById(R.id.navigation_view_1_list_view);
        settingsMenuTitle = settingsMenuDrawerTitle = getTitle();
        settingsMenuDrawerToggle = new ActionBarDrawerToggle(this, settingsMenuDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            /**
             * Called when a drawer has settled in a completely closed state
             * @param view the calling view
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(settingsMenuTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state
             * @param view the calling view
             */
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                getActionBar().setTitle(settingsMenuDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // set the drawer toggle as the DrawerListener
        //noinspection deprecation
        settingsMenuDrawerLayout.setDrawerListener(settingsMenuDrawerToggle);

        settingsMenuItemList.setAdapter(new ArrayAdapter<>(this, R.layout.navigation_view_1, settingsMenuItemTitles));
        settingsMenuItemList.setOnItemClickListener((parent, view, position, id) ->
                homeRoutine.selectItem(position, settingsMenuItemTitles, settingsMenuDrawerLayout, settingsMenuItemList));

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Initializes the Navigation View
     */
    private void initializeNavigationView() {
        navigationView = (NavigationView)findViewById(R.id.navigation_view_1);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            settingsMenuDrawerLayout.closeDrawers();
            return true;
        });
    }

    /**
     * Called whenever we called invalidateOptionsMenu()
     * @param menu the menu
     * @return superclass
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is open --> hide action items related to current view
        boolean drawerOpen = settingsMenuDrawerLayout.isDrawerOpen(settingsMenuItemList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * onPostCreate method
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred
        settingsMenuDrawerToggle.syncState();
    }

    /**
     * onConfigurationChanged method
     * @param configuration the new configuration
     */
    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        settingsMenuDrawerToggle.onConfigurationChanged(configuration);
    }

    /**
     * Opens Activity depending on the button clicked
     * @param item the button clicked
     * @return true if selected, none if doing something
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (settingsMenuDrawerToggle.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.string.title_activity_about:
                    homeRoutine.clickAbout();
                    return true;
                case R.string.title_activity_contact:
                    homeRoutine.clickContact();
                    return true;
                case R.string.title_activity_enter_info:
                    homeRoutine.clickEnterInfo();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } // pick activities here
        // handle other action bar items here
        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts recording
     * @param view the view calling this method
     */
    @TargetApi(Build.VERSION_CODES.N)
    public void clickRec(View view) {
        isRecording = true;
        recordingStartTime = new Date();
        homeRoutine.clickRec(chronometer);
        recButtonText.setText("Exit");
        buttons.replace("recButtonText", recButtonText);
        pauseImage.setVisibility(View.VISIBLE);

        /*String audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings/recordedAudio.wav";
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
                .record();*/
    }

    /**
     * Stops recording
     * @param view the view calling this method
     */
    @TargetApi(Build.VERSION_CODES.N)
    public void clickStop(View view) { // TODO: move all this logic into homeRoutine
        isRecording = false;
        recordingEndTime = new Date();
        homeRoutine.clickStop(view, chronometer, recordingStartTime, recordingEndTime);
        recButtonText.setText("Rec");
        buttons.replace("recButtonText", recButtonText);
        pauseImage.setVisibility(View.INVISIBLE);
        AndroidAudioRecorder.with(this);
    }


    /**
     * Exits the jam
     * @param view the view calling this method
     */
    public void clickExitJam(View view) {
        pauseImage.setVisibility(View.INVISIBLE);
        clickStop(view);
        homeRoutine.exitJam();
    }

    /**
     * Joins a jam
     * @param view the view calling this method
     */
    public void clickJoin(View view) {
        homeRoutine.joinJam();
    }

    /**
     * Confirms when file has been saved
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the intent
     */
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

    /*public void jamIDset() {
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

    private String[] createArray(String _0, String _1, String _2, String _3) {
        return new String[]{_0, _1, _2, _3};
    }*/
}