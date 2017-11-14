package com.draglabs.dsoundboy.dsoundboy.Activities.Old_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.Accessories.BandInfo;
import com.draglabs.dsoundboy.dsoundboy.Activities.MainActivity;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;

public class EnterInfoActivity_old extends AppCompatActivity {

    private EditText email;
    private EditText description;
    private EditText artistName;
    private EditText venue;

    private Button save;

    private String artistEmailText;
    private String recordingDescriptionText;
    private String artistNameText;
    private String recordingVenueText;

    private BandInfo bandInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_info);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeView();
    }

    private void initializeView() {
        save = (Button)findViewById(R.id.save);
        email = (EditText)findViewById(R.id.band_email_text);
        description = (EditText)findViewById(R.id.band_description_text);
        artistName = (EditText)findViewById(R.id.artist_name_text);
        venue = (EditText)findViewById(R.id.venue_text);

        email.setText(PrefUtils.getArtistEmail(this));
        description.setText(PrefUtils.getRecordingDescription(this));
        artistName.setText(PrefUtils.getArtistName(this));
        venue.setText(PrefUtils.getRecordingVenue(this));
    }

    public void clickSave(View view) {
        clickSave();
    }

    public void clickSave() {
        artistEmailText = email.getText().toString();
        recordingDescriptionText = description.getText().toString();
        artistNameText = artistName.getText().toString();
        recordingVenueText = venue.getText().toString();

        bandInfo = new BandInfo(artistEmailText, recordingDescriptionText, artistNameText, recordingVenueText);
        PrefUtils.setBandInfo(this, bandInfo);

        Intent sendBandInfo = new Intent(this, MainActivity.class);
        sendBandInfo.putExtra("callingClass", "EnterInfoActivity");
        sendBandInfo.putExtra("artistEmailText", artistEmailText);
        sendBandInfo.putExtra("recordingDescriptionText", recordingDescriptionText);
        sendBandInfo.putExtra("artistNameText", artistNameText);
        sendBandInfo.putExtra("recordingVenueText", recordingVenueText);

        Toast.makeText(this, "Band Data Saved.", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Go back and click \"Submit\" when done recording.", Toast.LENGTH_SHORT).show();
        startActivity(sendBandInfo);
    }
}
