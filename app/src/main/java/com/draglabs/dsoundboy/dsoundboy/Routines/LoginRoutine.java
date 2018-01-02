package com.draglabs.dsoundboy.dsoundboy.Routines;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.Activities.TestNavActivity;
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
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

/**
 * <p>Performs login tasks</p>
 * <p>Created by davrukin on 11/2/17.</p>
 */
public class LoginRoutine implements CallbackListener {

    private HashMap<String, Object> buttons;
    private Activity activity;
    private Context context;

    private PrefUtils prefUtils;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    /**
     * The constructor for the Login Routine
     * @param buttons the buttons
     * @param activity the activity calling the routine
     * @param context the app's context
     */
    public LoginRoutine(HashMap<String, Object> buttons, Activity activity, Context context) {
        this.buttons = buttons;
        this.activity = activity;
        this.context = context;
        this.prefUtils = new PrefUtils(activity);
        this.callbackManager = CallbackManager.Factory.create();
    }

    /**
     * The Facebook authentication routine
     */
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
                String loginResultTextText = "UserModel ID: + " + loginResult.getAccessToken().getUserId() + "\nAuth Token: " + loginResult.getAccessToken().getToken();
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

    /**
     * Authenticates the user with the dlsAPI
     */
    public void authenticateUser() {
        APIutils.registerUser(activity, context);
        prefUtils = new PrefUtils(activity);
        prefUtils.addListener(this);
        uniqueUserIDset();
        String uniqueUserID = prefUtils.getUniqueUserID();

        Toast.makeText(context, "Unique ID: " + prefUtils.getUniqueUserID(), Toast.LENGTH_LONG).show();
        //Log.d("Unique ID: ", uniqueUserID);
        Intent intent = new Intent(activity, TestNavActivity.class);
        //intent.putExtra("callingClass", "LoginActivity"); // DO IT THIS WAY, SEND BOOL VALUES THROUGH THIS INSTEAD OF SHAREDPREFERENCES!!!!!!!!!
        //intent.putExtra("uniqueUserID", uniqueUserID);
        activity.startActivity(intent);
    }

    /**
     * Saves the Facebook credentials in PrefUtils
     * @param loginResult the login result
     */
    public void saveFacebookCredentials(LoginResult loginResult) {
        prefUtils = new PrefUtils(activity); // add listener like above?
        prefUtils.saveAccessToken(loginResult.getAccessToken().getToken());
    }

    /**
     * Sets the profile view in the app so that the user sees their picture, name, and email
     * @param response the json response from Facebook
     */
    public void setProfileView(JSONObject response) {
        try {
            String name = response.getString("name");
            String email = response.getString("email");
            ProfilePictureView pictureView = (ProfilePictureView)activity.findViewById(R.id.user_picture);
            pictureView.setPresetSize(ProfilePictureView.NORMAL);
            pictureView.setProfileId(response.getString("id"));

            TextView userNameTextView = (TextView)activity.findViewById(R.id.user_name);
            TextView userEmailTextView = (TextView)activity.findViewById(R.id.user_email);
            userNameTextView.setText(name);
            userEmailTextView.setText(email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback for the setting of the dlsAPI user ID and logs it
     */
    public void uniqueUserIDset() {
        Log.v("Unique UserModel ID: ", new PrefUtils(activity).getUniqueUserID() + "");
        // TODO: Set it as a variable somewhere? Anyway the Jams activity sees this info too
    }

    /**
     * Gets the buttons
     * @return the buttons
     */
    public HashMap<String, Object> getButtons() {
        return buttons;
    }

    /**
     * Adds a button
     * @param string the string key to add
     * @param object the object by the key
     */
    public void addButton(String string, Object object) {
        buttons.put(string, object);
    }

    /**
     * Callback for the setting of the jam ID and logs it
     */
    public void jamIDset() {
        Log.v("Jam ID: ", prefUtils.getJamID() + "");
    }

    /**
     * Callback for the setting of the jam PIN and logs it
     */
    public void jamPINset() {
        Log.v("Jam PIN: ", prefUtils.getJamPIN() + "");
    }

    /**
     * Callback for the setting of the jam's start time and logs it
     */
    public void jamStartTimeSet() {
        Log.v("Jam Start Time: ", prefUtils.getJamStartTime() + "");
    }

    /**
     * Callback for the setting of the jam's end time and logs it
     */
    public void jamEndTimeSet() {
        Log.v("Jam End Time: ", prefUtils.getJamEndTime() + "");
    }

    /**
     * Callback for the setting of the jam's collaborators and logs them
     */
    public void getCollaboratorsSet() {
        Log.v("Collaborators: ", prefUtils.getCollaborators() + "");

    }

    /**
     * Callback for the setting of the user's activity and logs it
     */
    public void getUserActivitySet() {
        Log.v("UserModel Activity: ", prefUtils.getUserActivity() + "");

    }

    /**
     * Callback for the setting of the jam's details and logs them
     */
    public void getJamDetailsSet() {
        Log.v("Jam Details: ", prefUtils.getJamDetails() + "");
    }

    /**
     * Callback for the setting of the Facebook access token and logs it
     */
    @Override
    public void getAccessTokenSet() {
        Log.v("Access Token: ", prefUtils.getAccessToken() + "");

    }
}
