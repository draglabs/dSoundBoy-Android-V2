package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;

import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Routines.ListOfRecordingsRoutine;
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Lists all the recordings from the user and his/her jams
 */
public class ListOfRecordingsActivity extends AppCompatActivity implements CallbackListener {

    private Toolbar toolbar;
    private FloatingActionButton fab; // TODO: selected items will be sent as zip in an email, view jams
    private TableLayout tableLayout;
    private int clickCount;
    private String[] listOfFiles;
    private MediaPlayer mediaPlayer;
    private HashMap userActivity;
    private boolean isPlaying = false;
    private boolean isInitiallyPlaying = false;

    private ArrayList<Object> selectedItems; // updated, not queried
    private ArrayList<String> jamIDs;

    /**
     * onCreate method for the ListOfRecordings Activity
     * @param savedInstanceState the saved instance state
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recordings);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        tableLayout = (TableLayout)findViewById(R.id.table_recordings);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        clickCount = 0;

        //setUserActivityJamIDs();

        userActivity = new HashMap();
        //setUserActivityURLs(); // TODO: CRASHES HERE, implement later

        listOfFiles = listFilesInDirectory();
        addRowsToTable(listOfFiles);

        mediaPlayer = new MediaPlayer();

        selectedItems = new ArrayList<>();
        jamIDs = new ArrayList<>();
        //APIutils.getUserActivity(this, new PrefUtils(this).getUniqueUserID(), StringsModel.jsonTypes.JAMS.type());
    }

    /**
     * Adds rows to the table of jams, where a single row is a single jam
     * @param items all the jams
     */
    private void addRowsToTable(Object[] items) {
        ListOfRecordingsRoutine.addRowsToTable(items, this, this, mediaPlayer, tableLayout);
    }

    /**
     * <p>Adds a single row to the table with a vertical top-down index of where its located in the table</p>
     * <p>Sets listeners for each item that is clicked</p>
     * @param item the jam to add
     * @param index the jam's index
     */
    private void addRowToTable(final Object item, int index) {
        ListOfRecordingsRoutine.addRowToTable(item, index, this, this, mediaPlayer, tableLayout);
    }

    /**
     * Goes through the local directory to create a list of all the recordings that have been made
     * @return new array of strings indexed appropriately according to location in folder
     */
    private String[] listFilesInDirectory() {
        return ListOfRecordingsRoutine.listFilesInDirectory();
    }

    /**
     * Sets the download location from where to fetch the recording on S3
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUserActivityURLs() {
        ListOfRecordingsRoutine.setUserActivityURLs(this, this, userActivity);
    }

    /**
     * Brings the jam IDs into the user activity data stored in PrefUtils
     */
    private void setUserActivityJamIDs() {
        ListOfRecordingsRoutine.setUserActivityJamIDs(this, this, jamIDs);
    }

    /**
     * Listens to when the recording is being played and does stuff, probably not needed
     */
    private View.OnClickListener playStop = view -> {

    };

    /**
     * Streams the url
     * @param url the url to play
     * @return new MediaPlayer object
     */
    private MediaPlayer streamURLplay(String url) {
        return ListOfRecordingsRoutine.streamURLplay(url);
    }

    /**
     * Stops the url audio stream
     * @param mediaPlayer the MediaPlayer object
     */
    private void streamURLstop(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    /**
     * Listener for when the Floating Action Button is clicked
     * @param view the view calling this method
     */
    public void clickFab(View view) {
        fab = (FloatingActionButton)findViewById(R.id.fab);
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        String jamID = ""; // get this from the one that is checked

        APIutils.notifyUser(jamID, this);
        // TODO: on selected list items, submit to server; filename: {userID_2017-08-31_13:02:45:384.pcm}
    }

    /**
     * Plays a track given a URI
     * @param uri the uri
     */
    private void playTrack(Uri uri) {
        ListOfRecordingsRoutine.playTrack(uri, mediaPlayer, this);
    }

    /**
     * Pauses the currently-playing track
     */
    private void pauseTrack() {
        mediaPlayer.pause();
    }

    /**
     * Restarts the currently-playing track
     */
    private void restartTrack() {
        mediaPlayer.reset();
        mediaPlayer.start();
    }

    /**
     * Stops playing the currently-playing track
     * @param mediaPlayer the media player
     */
    private void stopTrack(MediaPlayer mediaPlayer) {
        ListOfRecordingsRoutine.stopTrack(mediaPlayer);
    }

    /**
     * Shows a dummy alert dialog
     */
    private void createAlertDialog() {
        ListOfRecordingsRoutine.createAlertDialog(this);
    }

    /**
     * Shows the dialog to confirm sending the jam to the email given a Jam ID
     * @param jamID the jam ID to send
     */
    private void createNotifyUserDialog(String jamID) { // to notify user with email
        ListOfRecordingsRoutine.createNotifyUserDialog(jamID, this, this);
    }

    /**
     * Callback for the setting of the dlsAPI user ID and logs it
     */
    public void uniqueUserIDset() {
        Log.v("Unique User ID: ", "Done");
    }

    /**
     * Callback for the setting of the jam PIN and logs it
     */
    public void jamPINset() {
        Log.v("Jam PIN: ", "Done");
    }

    /**
     * Callback for the setting of the jam ID and logs it
     */
    public void jamIDset() {
        Log.v("Jam ID: ", "Done");
    }

    /**
     * Callback for the setting of the jam's start time and logs it
     */
    public void jamStartTimeSet() {
        Log.v("Jam Start Time: ", "Done");
    }

    /**
     * Callback for the setting of the jam's end time and logs it
     */
    public void jamEndTimeSet() {
        Log.v("Jam End Time: ", "Done");
    }

    /**
     * Callback for the setting of the jam's collaborators and logs them
     */
    public void getCollaboratorsSet() {
        Log.v("Collaborators: ", "Done");
    }

    /**
     * Callback for the setting of the user's activity and logs it
     */
    public void getUserActivitySet() {
        Log.v("User Activity: ", "Done");
    }

    /**
     * Callback for the setting of the jam's details and logs them
     */
    public void getJamDetailsSet() {
        Log.v("Jam Details: ", "Done");
    }

    /**
     * Callback for the setting of the Facebook access token and logs it
     */
    @Override
    public void getAccessTokenSet() {
        Log.v("Access Token: ", "Done");
    }
}
