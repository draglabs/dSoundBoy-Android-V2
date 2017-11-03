package com.draglabs.dsoundboy.dsoundboy.Routines;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.draglabs.dsoundboy.dsoundboy.Activities.HomeActivity;
import com.draglabs.dsoundboy.dsoundboy.Activities.LoginActivity;
import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by davrukin on 11/2/17.
 */

public class LoginRoutine implements CallbackListener {

    private HashMap<String, Object> buttons;
    private Activity activity;
    private Context context;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private LoginActivity.UserLoginTask mAuthTask = null;


    private PrefUtils prefUtils;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    private String uniqueUserID;

    public LoginRoutine(HashMap<String, Object> buttons, Activity activity, Context context) {
        this.buttons = buttons;
        this.activity = activity;
        this.context = context;

        this.prefUtils = new PrefUtils(activity);
        this.callbackManager = CallbackManager.Factory.create();

    }

    public void clickFacebookLogin(View view) {
        //facebookLoginButton.setReadPermissions("email");
        LoginButton facebookLoginButton = (LoginButton)buttons.get("facebookLoginButton");
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            TextView loginResultText = (TextView)buttons.get("loginResultText");
            Button continueButton = (Button)buttons.get("continueButton");
            Button mEmailSignInButton = (Button)buttons.get("emailSignInButton");

            @Override
            public void onSuccess(LoginResult loginResult) {
                // app code
                accessToken = AccessToken.getCurrentAccessToken();
                String loginResultTextText = "User ID: + " + loginResult.getAccessToken().getUserId() + "\nAuth Token: " + loginResult.getAccessToken().getToken();
                loginResultText.setText(loginResultTextText);
                continueButton.setEnabled(true);
                mEmailSignInButton.setEnabled(false);
            }

            @Override
            public void onCancel() {
                // app code
                loginResultText.setText(context.getString(R.string.login_attempt_canceled));
                mEmailSignInButton.setEnabled(true);
            }

            @Override
            public void onError(FacebookException error) {
                // app code
                loginResultText.setText(context.getString(R.string.login_attempt_failed));
                mEmailSignInButton.setEnabled(true);
            }
        });
    }

    public void clickContinueButton(View view) {
        APIutils.authenticateUser(activity);
        prefUtils = new PrefUtils(activity);
        prefUtils.addListener(this);
        uniqueUserIDset();
        uniqueUserID = prefUtils.getUniqueUserID();

        Snackbar.make(view, "Unique ID: " + prefUtils.getUniqueUserID(), Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.putExtra("callingClass", "LoginActivity"); // DO IT THIS WAY, SEND BOOL VALUES THROUGH THIS INSTEAD OF SHAREDPREFERENCES!!!!!!!!!
        intent.putExtra("uniqueUserID", uniqueUserID);
        /*intent.putExtra("enterInfoEnabled", true);
        intent.putExtra("startStopEnabled", false);
        intent.putExtra("resetEnabled", false);
        intent.putExtra("submitEnabled", false);*/
        activity.startActivity(intent);
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

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
