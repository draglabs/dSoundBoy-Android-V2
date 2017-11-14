package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Routines.LoginRoutine;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.HashMap;

public class NewLoginActivity extends AppCompatActivity {

    private HashMap<String, Object> buttons;
    private CallbackManager callbackManager;
    private LoginRoutine loginRoutine;
    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_new_login);

        buttons = new HashMap<>();

        loginButton = (LoginButton)findViewById(R.id.login_button);
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

        Log.d("Access Token:", AccessToken.getCurrentAccessToken().toString());

        if (AccessToken.getCurrentAccessToken() != null) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    public void clickFacebookLogin(View view) {
        loginRoutine.clickFacebookLogin();
    }

    public void clickLogoLink(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"));
        startActivity(browserIntent);
    }
}
