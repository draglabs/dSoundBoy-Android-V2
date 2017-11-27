package com.draglabs.dsoundboy.dsoundboy.Routines;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.Activities.HomeActivity;
import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener;
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by davrukin on 11/2/17.
 */

public class LoginRoutine implements CallbackListener {

    private HashMap<String, Object> buttons;
    private Activity activity;
    private Context context;

    private PrefUtils prefUtils;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    public LoginRoutine(HashMap<String, Object> buttons, Activity activity, Context context) {
        this.buttons = buttons;
        this.activity = activity;
        this.context = context;
        this.prefUtils = new PrefUtils(activity);
        this.callbackManager = CallbackManager.Factory.create();
    }

    public void clickFacebookLogin() {
        LoginButton facebookLoginButton = (LoginButton)buttons.get("facebookLoginButton");
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //TextView loginResultText = (TextView)buttons.get("loginResultText");
            //Button continueButton = (Button)buttons.get("continueButton");
            //Button mEmailSignInButton = (Button)buttons.get("emailSignInButton");

            @Override
            public void onSuccess(LoginResult loginResult) {
                // app code
                accessToken = AccessToken.getCurrentAccessToken();
                String loginResultTextText = "User ID: + " + loginResult.getAccessToken().getUserId() + "\nAuth Token: " + loginResult.getAccessToken().getToken();
                Log.d("Login Result: ", loginResultTextText);
                //loginResultText.setText(loginResultTextText);
                //continueButton.setEnabled(true);
                //mEmailSignInButton.setEnabled(false);
                authenticateUser();
            }

            @Override
            public void onCancel() {
                // app code
                //loginResultText.setText(context.getString(R.string.login_attempt_canceled));
                //mEmailSignInButton.setEnabled(true);
                Log.d("onCancel:", "Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                // app code
                //loginResultText.setText(context.getString(R.string.login_attempt_failed));
                //mEmailSignInButton.setEnabled(true);
                Log.d("onError: ", "Login Error");
            }
        });
    }

    public void authenticateUser() {
        APIutils.authenticateUser(activity);
        prefUtils = new PrefUtils(activity);
        prefUtils.addListener(this);
        uniqueUserIDset();
        String uniqueUserID = prefUtils.getUniqueUserID();

        Toast.makeText(context, "Unique ID: " + prefUtils.getUniqueUserID(), Toast.LENGTH_LONG).show();
        Log.d("Unique ID: ", uniqueUserID);
        Intent intent = new Intent(activity, HomeActivity.class);
        //intent.putExtra("callingClass", "LoginActivity"); // DO IT THIS WAY, SEND BOOL VALUES THROUGH THIS INSTEAD OF SHAREDPREFERENCES!!!!!!!!!
        //intent.putExtra("uniqueUserID", uniqueUserID);
        activity.startActivity(intent);
    }

    public void saveFacebookCredentials(LoginResult loginResult) {
        prefUtils = new PrefUtils(activity); // add listener like above?
        prefUtils.saveAccessToken(loginResult.getAccessToken().getToken());
    }

    public void uniqueUserIDset() {
        Log.v("Unique User ID: ", new PrefUtils(activity).getUniqueUserID() + "");
        // TODO: Set it as a variable somewhere? Anyway the Jams activity sees this info too
    }

    public void jamIDset() {
        Log.v("Jam ID: ", prefUtils.getJamID() + "");
    }

    public void jamPINset() {
        Log.v("Jam PIN: ", prefUtils.getJamPIN() + "");
    }

    public void jamStartTimeSet() {
        Log.v("Jam Start Time: ", prefUtils.getJamStartTime() + "");
    }

    public void jamEndTimeSet() {
        Log.v("Jam End Time: ", prefUtils.getJamEndTime() + "");
    }

    public void getCollaboratorsSet() {
        Log.v("Collaborators: ", prefUtils.getCollaborators() + "");

    }

    public void getUserActivitySet() {
        Log.v("User Activity: ", prefUtils.getUserActivity() + "");

    }

    public void getJamDetailsSet() {
        Log.v("Jam Details: ", prefUtils.getJamDetails() + "");
    }
}
