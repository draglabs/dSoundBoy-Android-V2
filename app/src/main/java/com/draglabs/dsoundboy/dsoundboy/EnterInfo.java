package com.draglabs.dsoundboy.dsoundboy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterInfo extends AppCompatActivity {

    private EditText email;
    private EditText description;
    private EditText artistName;
    private EditText venue;

    private Button save;

    private String emailText;
    private String descriptionText;
    private String artistNameText;
    private String venueText;

    private BandInfo bandInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_info);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        save = (Button)findViewById(R.id.save);
        email = (EditText)findViewById(R.id.band_email_text);
        description = (EditText)findViewById(R.id.band_description_text);
        artistName = (EditText)findViewById(R.id.artist_name_text);
        venue = (EditText)findViewById(R.id.venue_text);
    }

    public void clickSave(View view) {
        emailText = email.getText().toString();
        descriptionText = description.getText().toString();
        artistNameText = artistName.getText().toString();
        venueText = venue.getText().toString();

        bandInfo = new BandInfo(emailText, descriptionText, artistNameText, venueText);

        Intent sendBandInfo = new Intent(this, MainActivity.class);
        //Intent sendBandInfo = getSupportParentActivityIntent();
        sendBandInfo.putExtra("callingClass", "EnterInfo");
        sendBandInfo.putExtra("emailText", emailText);
        sendBandInfo.putExtra("descriptionText", descriptionText);
        sendBandInfo.putExtra("artistNameText", artistNameText);
        sendBandInfo.putExtra("venueText", venueText);

        Toast.makeText(this, "Band Data Saved.", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Go back and click \"Submit\" when done recording.", Toast.LENGTH_SHORT).show();
        startActivity(sendBandInfo);
        //MainActivity.setBandInfo(emailText, descriptionText, artistNameText, venueText);
    }

    /*public static BandInfo getBandInfo() {
        return BandInfo;
        //return bandInfo;
    }*/

}
