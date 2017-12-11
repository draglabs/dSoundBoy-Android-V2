package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.draglabs.dsoundboy.dsoundboy.R;


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
        //new MainRoutine().facebookAuthorize(this);
        //new MainRoutine().googleAuthorize();
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, NewLoginActivity.class));

        /*Log.d("FB ID: " , AccessToken.getCurrentAccessToken().getUserId());
        Log.d("FB Access Token: " , AccessToken.getCurrentAccessToken().getToken());
        if (!new PrefUtils(this).hasUniqueUserID() && AccessToken.getCurrentAccessToken() == null) {
            Intent loginIntent = new Intent(this, NewLoginActivity.class);
            startActivity(loginIntent);
        } else {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        }*/

    }
}