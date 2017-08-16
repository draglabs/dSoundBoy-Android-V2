package com.draglabs.dsoundboy.dsoundboy;

import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button about;
    private Button contact;
    private Button submit;
    private Chronometer chronometer;
    private Button startStop;
    private int startStopClickCount;
    private ImageButton logoLink;
    private Button clear;
    private ImageView recordingImage;
    private Button enterInfo;

    private String emailText;
    private String descriptionText;
    private String artistNameText;
    private String venueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        about = (Button)findViewById(R.id.about);
        contact = (Button)findViewById(R.id.contact);
        submit = (Button)findViewById(R.id.submit);
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        //chronometer.setFormat("HH:MM:SS:ss");
        startStop = (Button)findViewById(R.id.start_stop);
        startStopClickCount = 0;
        logoLink = (ImageButton)findViewById(R.id.logo_link);
        clear = (Button)findViewById(R.id.clear);
        recordingImage = (ImageView)findViewById(R.id.recording_image);
        recordingImage.setVisibility(View.INVISIBLE);
        enterInfo = (Button)findViewById(R.id.enter_info);
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
        setBandInfo();
    }

    public void clickStartStop(View view) {
        startStopClickCount++;
        if (startStopClickCount % 2 != 0) {
            Toast.makeText(this, "Starting recording", Toast.LENGTH_LONG);
            chronometer.start();
            recordingImage.setVisibility(View.VISIBLE);
            // TODO: start recording program, append to file if started again
        } else {
            Toast.makeText(this, "Stopping recording", Toast.LENGTH_LONG);
            chronometer.stop();
            recordingImage.setVisibility(View.INVISIBLE);
            // TODO: stop recording program
        }
    }

    public void clickClear(View view) {
        // TODO: when to save recordings
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
        Intent intent = new Intent(this, EnterInfo.class);
        startActivity(intent);
    }

    private void setBandInfo() {
        Intent getBandInfo = getIntent();
        this.emailText = getBandInfo.getStringExtra("emailText");
        this.descriptionText = getBandInfo.getStringExtra("descriptionText");
        this.artistNameText = getBandInfo.getStringExtra("artistNameText");
        this.venueText = getBandInfo.getStringExtra("venueText");
        Toast.makeText(this, emailText + "\n" + descriptionText + "\n" + artistNameText + "\n" + venueText, Toast.LENGTH_LONG).show();
    }
}
