package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Routines.LoginRoutine;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.HashMap;

/**
 * The new and improved login activity, with just a single button!
 */
public class NewLoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginRoutine loginRoutine;
    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;

    /**
     * The onCreate method for the new login activity
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection deprecation
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_new_login);

        HashMap<String, Object> buttons = new HashMap<>();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("User ID: ", loginResult.getAccessToken().getUserId());
                Log.d("Access Token: ", loginResult.getAccessToken().getToken());
                Log.d("Application ID: ", loginResult.getAccessToken().getApplicationId());
                loginRoutine.saveFacebookCredentials(loginResult);
                loginRoutine.authenticateUser();
            }

            @Override
            public void onCancel() {
                Log.d("FB Login Cancelled", "");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Facebook Error: ", error.toString());
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // set the access token using currentAccessToken when it's loaded or set
                accessToken = currentAccessToken;
            }
        };

        buttons.put("facebookLoginButton", loginButton);

        loginRoutine = new LoginRoutine(buttons, this, this);

        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("Access Token:", AccessToken.getCurrentAccessToken().toString());
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        }
    }

    /**
     * What happens when the login is successful, it calls back to the CallbackManager
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the data from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * If the login process is cancelled
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    /**
     * Performs the Facebook Login
     * @param view the view calling the login
     */
    public void clickFacebookLogin(View view) {
        loginRoutine.clickFacebookLogin();
    }

    /**
     * Clicking this takes the user to the DragLabs home page
     * @param view the view calling this method
     */
    public void clickLogoLink(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"));
        startActivity(browserIntent);
    }
}
