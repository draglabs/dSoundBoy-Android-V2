package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.Models.BandInfoModel;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;

/**
 * Allows band to enter info about itself
 */
public class EnterInfoActivity extends AppCompatActivity {

    private EditText email;
    private EditText description;
    private EditText artistName;
    private EditText venue;

    /**
     * onCreate method
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_info);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeView();
    }

    /**
     * Initializes all the EditTexts
     */
    private void initializeView() {
        email = (EditText)findViewById(R.id.band_email_text);
        description = (EditText)findViewById(R.id.band_description_text);
        artistName = (EditText)findViewById(R.id.artist_name_text);
        venue = (EditText)findViewById(R.id.venue_text);

        email.setText(PrefUtils.getArtistEmail(this));
        description.setText(PrefUtils.getRecordingDescription(this));
        artistName.setText(PrefUtils.getArtistName(this));
        venue.setText(PrefUtils.getRecordingVenue(this));
    }

    /**
     * Saves band info to app storage
     * @param view the view calling this method
     */
    public void clickSave(View view) {
        String artistEmailText = email.getText().toString();
        String recordingDescriptionText = description.getText().toString();
        String artistNameText = artistName.getText().toString();
        String recordingVenueText = venue.getText().toString();

        BandInfoModel bandInfoModel = new BandInfoModel(artistEmailText, recordingDescriptionText, artistNameText, recordingVenueText);
        PrefUtils.setBandInfo(this, bandInfoModel);

        Intent sendBandInfo = new Intent(this, HomeActivity.class);

        Toast.makeText(this, "Band Data Saved.", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Go back and click \"Submit\" when done recording.", Toast.LENGTH_SHORT).show();
        startActivity(sendBandInfo);
    }
}
