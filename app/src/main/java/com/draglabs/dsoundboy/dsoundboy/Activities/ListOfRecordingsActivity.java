package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.draglabs.dsoundboy.dsoundboy.Accessories.Email;
import com.draglabs.dsoundboy.dsoundboy.Accessories.Strings;
import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

    private ArrayList<Object> selectedItems;
    private ArrayList<String> jamIDs;

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
        //APIutils.getUserActivity(this, new PrefUtils(this).getUniqueUserID(), Strings.jsonTypes.JAMS.type());
    }

    private void addRowsToTable(Object[] items) {
        addRowToTable(new PrefUtils(this).getJamID(), 0);

        /*int i = 0;
        for (Object id : userActivity.keySet()) { // STREAMING ONES FIRST
            addRowToTable(userActivity.get(id), i);
            i++;
        }

        int j = i;
        i = 0;

        for (; j < items.length; j++) { // LOCAL ONES SECOND
            addRowToTable(items[i], j);
            i++;
        }*/
    }

    private void addRowToTable(final Object item, int index) {
        final TableRow newRow = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT); // check later
        newRow.setLayoutParams(layoutParams);

        CheckBox checkBox = new CheckBox(this); // TODO: select if checked
        TextView textView = new TextView(this);
        ToggleButton toggleButton = new ToggleButton(this);
        //toggleButton.setOnClickListener(playStop);

        final ImageButton play = new ImageButton(this);
        final ImageButton pause = new ImageButton(this);
        final ImageButton previous = new ImageButton(this);

        String itemAsString = (String)item;

        textView.setText(itemAsString); // TODO: make text scrollable in constrained width of textview
        toggleButton.setText(itemAsString);
        newRow.addView(checkBox);
        newRow.addView(textView);
        //newRow.addView(toggleButton);

        play.setImageResource(android.R.drawable.ic_media_play);
        pause.setImageResource(android.R.drawable.ic_media_pause);
        previous.setImageResource(android.R.drawable.ic_media_previous);

        /*newRow.addView(play);

        View.OnClickListener playPauseRecordingListener = view -> {
            if (clickCount % 2 == 0) {
                // show pause
                newRow.removeView(play);
                newRow.addView(pause);
                Uri uri = Uri.fromFile(new File((String)item));
                playTrack(uri); // TODO: buttons not visible?
            } else {
                // show play
                newRow.removeView(pause);
                newRow.addView(play);
                pauseTrack();
            }
            newRow.addView(previous);
            clickCount++;
        };

        View.OnClickListener resetRecordingListener = view -> restartTrack();

        play.setOnClickListener(playPauseRecordingListener);
        pause.setOnClickListener(playPauseRecordingListener);
        previous.setOnClickListener(resetRecordingListener);*/

        textView.setOnClickListener(view -> createNotifyUserDialog(itemAsString));

        toggleButton.setOnClickListener(view -> {
            if (!toggleButton.isChecked()) {
                toggleButton.setText("Playing track");
                if (!itemAsString.startsWith("http")) {
                    Uri uri = Uri.fromFile(new File(itemAsString));
                    playTrack(uri);
                } else {
                    streamURLplay(itemAsString);
                }
            } else {
                stopTrack(mediaPlayer);
            }
        });

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b == true) {

            } else {

            }
        });

        tableLayout.addView(newRow, index); // TODO: Play, add play button
    }

    private String[] listFilesInDirectory() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings/";

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        } else {
            Log.v("Directory: ", directory.toString());
            Log.v("Directory Length: ", directory.listFiles().toString());
            if (directory.list().length != 0) {
                return directory.list();
            }
        }
        return new String[]{"No saved recordings."};
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUserActivityURLs() {
        PrefUtils prefUtils = new PrefUtils(this);
        APIutils.getUserActivity(this, prefUtils.getUniqueUserID(), this);

        // TODO: renew prefUtils whenever a change is made, because the old one is still in memory
        prefUtils = new PrefUtils(this);
        String userActivityData = prefUtils.getUserActivity(); // TODO: CRASHES HERE
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //Map<String, Object> stringObjectMap = gson.fromJson(userActivity, new TypeToken<Map<String ,Object>>(){}.getType());
        //stringObjectMap.forEach((x, y) -> System.out.println(""));
        Log.d("User Activity: ", userActivityData);
        try {
            JSONObject jsonObject = new JSONObject(userActivityData);
            JSONArray recordings = jsonObject.getJSONArray("recordings");
            for (int i = 0; i < recordings.length(); i++) {
                JSONObject recording = recordings.getJSONObject(i);
                String recordingID = recording.getString("_id"); // add values to arraylist or other data structure? maybe key-value
                String recordingUrl = recording.getString("s3url");
                userActivity.put(recordingID, recordingUrl);
                /*for (Object id : userActivity.keySet()) {
                    String url = userActivity.get(id);
                }*/ // USE LATER WHEN GETTING
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUserActivityJamIDs() {
        PrefUtils prefUtils = new PrefUtils(this);
        APIutils.getUserActivity(this, prefUtils.getUniqueUserID(), this);
        Log.v("Unique User ID: ", prefUtils.getUniqueUserID());

        prefUtils = new PrefUtils(this);
        String jamIDs = prefUtils.getUserActivity();

        Log.d("Jam IDs: ", jamIDs);

        try {
            JSONArray jsonArray = new JSONArray(jamIDs);
            for (int i = 0; i < jsonArray.length(); i++) {
                this.jamIDs.add(jsonArray.getJSONObject(i).getString(Strings.jsonTypes.JAM_ID.type()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener playStop = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };


    private MediaPlayer streamURLplay(String url) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.start();
        return mediaPlayer;
    }

    private void streamURLstop(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void clickFab(View view) {
        fab = (FloatingActionButton)findViewById(R.id.fab);
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        String jamID = ""; // get this from the one that is checked

        APIutils.notifyUser(jamID, this);
        // TODO: on selected list items, submit to server; filename: {userID_2017-08-31_13:02:45:384.pcm}
    }

    private void playTrack(Uri uri) {
        stopTrack(mediaPlayer);

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer = MediaPlayer.create(this, uri);
        }

        mediaPlayer.prepareAsync();
        mediaPlayer.start();
    }

    private void pauseTrack() {
        mediaPlayer.pause();
    }

    private void restartTrack() {
        mediaPlayer.reset();
        mediaPlayer.start();
    }

    private void stopTrack(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }

    private void createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hello!").setTitle("World!");
        builder.setPositiveButton("Okay", (dialog, which) -> {
            // set a callback once something is done
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setNeutralButton("Remind me later", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createNotifyUserDialog(String jamID) { // to notify user with email
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Email jam to account email?").setTitle("Email Jam");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // set a callback once something is done
            //APIutils.notifyUser(jamID, this);
            APIutils.getJamDetails(new PrefUtils(this).getUniqueUserID(), jamID, this, this);
            JSONObject jamDetails = null;
            try {
                jamDetails = new JSONObject(new PrefUtils(this).getJamDetails());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //APIutils.generateXML(jamID, jamDetails, this);
            APIutils.compress(jamID, new PrefUtils(this).getUniqueUserID(), jamDetails, this);
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void uniqueUserIDset() {
        Log.v("Unique User ID: ", "Done");
    }

    public void jamPINset() {
        Log.v("Jam PIN: ", "Done");
    }

    public void jamIDset() {
        Log.v("Jam ID: ", "Done");
    }

    public void jamStartTimeSet() {
        Log.v("Jam Start Time: ", "Done");
    }

    public void jamEndTimeSet() {
        Log.v("Jam End Time: ", "Done");
    }

    public void getCollaboratorsSet() {
        Log.v("Collaborators: ", "Done");
    }

    public void getUserActivitySet() {
        Log.v("User Activity: ", "Done");
    }

    public void getJamDetailsSet() {
        Log.v("Jam Details: ", "Done");
    }
}
