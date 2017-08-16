package com.draglabs.dsoundboy.dsoundboy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        Intent sendBandInfo = new Intent(this, MainActivity.class);
        sendBandInfo.putExtra("emailText", emailText);
        sendBandInfo.putExtra("descriptionText", descriptionText);
        sendBandInfo.putExtra("artistNameText", artistNameText);
        sendBandInfo.putExtra("venueText", venueText);

        //MainActivity.setBandInfo(emailText, descriptionText, artistNameText, venueText);
    }

}
