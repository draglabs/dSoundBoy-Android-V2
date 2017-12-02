package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Routines.MainRoutine;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;
import com.facebook.AccessToken;


/**
 * Main Activity which loads pertinent screen depending on access
 */
public class MainActivity extends AppCompatActivity {

    /**
     * onCreate method which redirects
     * @param savedInstanceState the saved instance state
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MainRoutine().facebookAuthorize(this);
        //new MainRoutine().googleAuthorize();
        setContentView(R.layout.activity_main);

        if (new PrefUtils(this).hasUniqueUserID() && AccessToken.getCurrentAccessToken() != null) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        } else {
            Intent loginIntent = new Intent(this, NewLoginActivity.class);
            startActivity(loginIntent);
        }

    }
}