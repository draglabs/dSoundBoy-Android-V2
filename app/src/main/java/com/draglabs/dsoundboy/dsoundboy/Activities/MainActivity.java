package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

        //startActivity(new Intent(this, TestNavActivity.class));
        //startActivity(new Intent(this, NewLoginActivity.class));

        PrefUtils prefUtils = new PrefUtils(this);
        String UUID = prefUtils.getUniqueUserID();

        if ((UUID == null || UUID.equals("")) && AccessToken.getCurrentAccessToken() == null) {
            Intent loginIntent = new Intent(this, NewLoginActivity.class);
            startActivity(loginIntent);
        } else {
            Log.d("FB ID: " , AccessToken.getCurrentAccessToken().getUserId());
            Log.d("FB Access Token: " , AccessToken.getCurrentAccessToken().getToken());
            Intent homeIntent = new Intent(this, TestNavActivity.class);
            startActivity(homeIntent);
        }
    }
}