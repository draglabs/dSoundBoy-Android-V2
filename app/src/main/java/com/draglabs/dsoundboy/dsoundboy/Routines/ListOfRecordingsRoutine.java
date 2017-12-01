package com.draglabs.dsoundboy.dsoundboy.Routines;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.draglabs.dsoundboy.dsoundboy.Models.StringsModel;
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * <p>Contains functions for the ListOfRecordings Activity</p>
 * <p>Created by davrukin on 11/27/2017.</p>
 */
public class ListOfRecordingsRoutine {

    // TODO: streamline functions, remove unnecessary ones from the Activity and leave the needed ones here in the Routine

    /**
     * Adds rows to the table of jams, where a single row is a single jam
     * @param items all the jams
     */
    public static void addRowsToTable(Object[] items,
                                      Activity activity,
                                      Context context,
                                      MediaPlayer mediaPlayer,
                                      TableLayout tableLayout) {
        
        addRowToTable(new PrefUtils(activity).getJamID(), 0, activity, context, mediaPlayer, tableLayout);

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

    /**
     * <p>Adds a single row to the table with a vertical top-down index of where its located in the table</p>
     * <p>Sets listeners for each item that is clicked</p>
     * @param item the jam to add
     * @param index the jam's index
     */
    public static void addRowToTable(final Object item,
                                     int index,
                                     Activity activity,
                                     Context context,
                                     MediaPlayer mediaPlayer,
                                     TableLayout tableLayout) {

        final TableRow newRow = new TableRow(context);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT); // check later
        newRow.setLayoutParams(layoutParams);

        CheckBox checkBox = new CheckBox(context); // TODO: select if checked
        TextView textView = new TextView(context);
        ToggleButton toggleButton = new ToggleButton(context);
        //toggleButton.setOnClickListener(playStop);

        final ImageButton play = new ImageButton(context);
        final ImageButton pause = new ImageButton(context);
        final ImageButton previous = new ImageButton(context);

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

        textView.setOnClickListener(view -> createNotifyUserDialog(itemAsString, activity, context));

        toggleButton.setOnClickListener(view -> {
            if (!toggleButton.isChecked()) {
                toggleButton.setText("Playing track");
                if (!itemAsString.startsWith("http")) {
                    Uri uri = Uri.fromFile(new File(itemAsString));
                    playTrack(uri, mediaPlayer, activity);
                } else {
                    streamURLplay(itemAsString);
                }
            } else {
                stopTrack(mediaPlayer);
            }
        });

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {

            } else {

            }
        });

        tableLayout.addView(newRow, index); // TODO: Play, add play button
    }

    /**
     * Goes through the local directory to create a list of all the recordings that have been made
     * @return new array of strings indexed appropriately according to location in folder
     */
    public static String[] listFilesInDirectory() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings/";

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs(); // result ignored
        } else {
            Log.v("Directory: ", directory.toString());
            Log.v("Directory Length: ", Arrays.toString(directory.listFiles()));
            if (directory.list().length != 0) {
                return directory.list();
            }
        }
        return new String[]{"No saved recordings."};
    }

    /**
     * Sets the download location from where to fetch the recording on S3
     */
    // TODO: is this necessary now?
    public static void setUserActivityURLs(Activity activity,
                                           Context context,
                                           HashMap userActivity) {

        PrefUtils prefUtils = new PrefUtils(activity);
        APIutils.getUserActivity(activity, prefUtils.getUniqueUserID(), context);

        // TODO: renew prefUtils whenever a change is made, because the old one is still in memory
        prefUtils = new PrefUtils(activity);
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


    /**
     * Brings the jam IDs into the user activity data stored in PrefUtils
     */
    // TODO: is this still necessary?
    public static void setUserActivityJamIDs(Activity activity,
                                             Context context,
                                             ArrayList<String> jamIDList) {

        PrefUtils prefUtils = new PrefUtils(activity);
        APIutils.getUserActivity(activity, prefUtils.getUniqueUserID(), context);
        Log.v("Unique User ID: ", prefUtils.getUniqueUserID());

        prefUtils = new PrefUtils(activity);
        String jamIDs = prefUtils.getUserActivity();

        Log.d("Jam IDs: ", jamIDs);

        try {
            JSONArray jsonArray = new JSONArray(jamIDs);
            for (int i = 0; i < jsonArray.length(); i++) {
                jamIDList.add(jsonArray.getJSONObject(i).getString(StringsModel.jsonTypes.JAM_ID.type()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Shows a dummy alert dialog
     */
    public static void createAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    /**
     * Shows the dialog to confirm sending the jam to the email given a Jam ID
     * @param jamID the jam ID to send
     */
    // TODO: check functionality
    public static void createNotifyUserDialog(String jamID,
                                              Activity activity,
                                              Context context) { // to notify user with email

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("EmailModel jam to account email?").setTitle("EmailModel Jam");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // set a callback once something is done
            //APIutils.notifyUser(jamID, this);
            APIutils.getJamDetails(new PrefUtils(activity).getUniqueUserID(), jamID, activity, context);
            JSONObject jamDetails = null;
            try {
                jamDetails = new JSONObject(new PrefUtils(activity).getJamDetails());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //APIutils.generateXML(jamID, jamDetails, this);
            APIutils.compress(jamID, new PrefUtils(activity).getUniqueUserID(), jamDetails, context);
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Streams the url
     * @param url the url to play
     * @return new MediaPlayer object
     */
    public static MediaPlayer streamURLplay(String url) {
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

    /**
     * Plays a track given a URI
     * @param uri the uri
     */
    public static void playTrack(Uri uri,
                                 MediaPlayer mediaPlayer,
                                 Activity activity) {

        stopTrack(mediaPlayer);

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer = MediaPlayer.create(activity, uri);
        }

        mediaPlayer.prepareAsync();
        mediaPlayer.start();
    }

    /**
     * Stops playing the currently-playing track
     * @param mediaPlayer the media player
     */
    public static void stopTrack(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }

}
