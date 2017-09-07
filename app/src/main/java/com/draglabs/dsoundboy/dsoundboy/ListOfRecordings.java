package com.draglabs.dsoundboy.dsoundboy;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ListOfRecordings extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private TableLayout tableLayout;
    private int clickCount;
    private String[] listOfFiles;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recordings);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        clickCount = 0;

        tableLayout = (TableLayout)findViewById(R.id.table_recordings);
        //addRowToTable(new RadioButton(this), 0);

        listOfFiles = listFilesInDirectory();
        addRowsToTable(listOfFiles);

        mediaPlayer = new MediaPlayer();
    }

    private void addRowsToTable(Object[] items) {
        for (int i = 0; i < items.length; i++) {

            addRowToTable(items[i], i);

            /*TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT); // check later
            row.setLayoutParams(layoutParams);

            row.addView((View)items[i]);

            tableLayout.addView(row, i);*/
        }
    }

    private void addRowToTable(final Object item, int index) {

        final TableRow newRow = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT); // check later
        newRow.setLayoutParams(layoutParams);

        CheckBox checkBox = new CheckBox(this); // TODO: select if checked
        TextView textView = new TextView(this);

        final ImageButton play = new ImageButton(this);
        final ImageButton pause = new ImageButton(this);
        final ImageButton previous = new ImageButton(this);

        //textView.setText("Hello, world");
        textView.setText((String)item);
        newRow.addView(checkBox);
        newRow.addView(textView);
        //newRow.addView((View)item);
        play.setImageResource(android.R.drawable.ic_media_play);
        pause.setImageResource(android.R.drawable.ic_media_pause);
        previous.setImageResource(android.R.drawable.ic_media_previous);

        newRow.addView(play);

        View.OnClickListener playPauseRecordingListener = new View.OnClickListener() {
            public void onClick(View view) {
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
            }
        };

        View.OnClickListener resetRecordingListener = new View.OnClickListener() {
            public void onClick(View view) {
                restartTrack();
            }
        };

        play.setOnClickListener(playPauseRecordingListener);
        pause.setOnClickListener(playPauseRecordingListener);
        previous.setOnClickListener(resetRecordingListener);

        tableLayout.addView(newRow, index); // TODO: Play, add play button
    }

    private String[] listFilesInDirectory() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings/";

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        } else {
            if (directory.list().length != 0) {
                return directory.list();
            }
        }
        return new String[]{"No saved recordings."};
    }

    public void clickFab(View view) {
        fab = (FloatingActionButton)findViewById(R.id.fab);
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        // TODO: on selected list items, submit to server; filename: {userID_2017-08-31_13:02:45:384.pcm}
    }

    private void playTrack(Uri uri) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer = MediaPlayer.create(this, uri);
        } else {
        }
        mediaPlayer.start();
    }

    private void pauseTrack() {
        mediaPlayer.pause();

    }

    private void restartTrack() {
        mediaPlayer.reset();
        mediaPlayer.start();
    }
}
