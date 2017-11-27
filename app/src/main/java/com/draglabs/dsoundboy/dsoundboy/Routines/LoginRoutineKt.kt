package com.draglabs.dsoundboy.dsoundboy.Routines

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.draglabs.dsoundboy.dsoundboy.Activities.HomeActivity
import com.draglabs.dsoundboy.dsoundboy.Activities.LoginActivity
import com.draglabs.dsoundboy.dsoundboy.Interfaces.CallbackListener
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.util.*

/**
 * Created by davrukin on 11/2/17.
 */

class LoginRoutineKt(private val buttons: HashMap<String, Any>, private val activity: Activity, private val context: Context) : CallbackListener {
    private val mAuthTask: LoginActivity.UserLoginTask? = null

    private var prefUtils: PrefUtils? = null
    private val callbackManager: CallbackManager
    private val accessTokenTracker: AccessTokenTracker? = null
    private var accessToken: AccessToken? = null

    private var uniqueUserID: String? = null

    init {

        this.prefUtils = PrefUtils(activity)
        this.callbackManager = CallbackManager.Factory.create()

    }

    fun clickFacebookLogin() {
        //facebookLoginButton.setReadPermissions("email");
        val facebookLoginButton = buttons["facebookLoginButton"] as LoginButton
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"))
        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            internal var loginResultText = buttons["loginResultText"] as TextView
            internal var continueButton = buttons["continueButton"] as Button
            internal var mEmailSignInButton = buttons["emailSignInButton"] as Button

            override fun onSuccess(loginResult: LoginResult) {
                // app code
                accessToken = AccessToken.getCurrentAccessToken()
                val loginResultTextText = "User ID: + " + loginResult.accessToken.userId + "\nAuth Token: " + loginResult.accessToken.token
                loginResultText.text = loginResultTextText
                continueButton.isEnabled = true
                mEmailSignInButton.isEnabled = false
            }

            override fun onCancel() {
                // app code
                loginResultText.text = context.getString(R.string.login_attempt_canceled)
                mEmailSignInButton.isEnabled = true
            }

            override fun onError(error: FacebookException) {
                // app code
                loginResultText.text = context.getString(R.string.login_attempt_failed)
                mEmailSignInButton.isEnabled = true
            }
        })
    }

    fun clickContinueButton(view: View) {
        APIutils.authenticateUser(activity)
        prefUtils = PrefUtils(activity)
        prefUtils!!.addListener(this)
        uniqueUserIDset()
        uniqueUserID = prefUtils!!.uniqueUserID

        Snackbar.make(view, "Unique ID: " + prefUtils!!.uniqueUserID, Snackbar.LENGTH_LONG).show()
        val intent = Intent(activity, HomeActivity::class.java)
        intent.putExtra("callingClass", "LoginActivity") // DO IT THIS WAY, SEND BOOL VALUES THROUGH THIS INSTEAD OF SHAREDPREFERENCES!!!!!!!!!
        intent.putExtra("uniqueUserID", uniqueUserID)
        /*intent.putExtra("enterInfoEnabled", true);
        intent.putExtra("startStopEnabled", false);
        intent.putExtra("resetEnabled", false);
        intent.putExtra("submitEnabled", false);*/
        activity.startActivity(intent)
    }

    override fun uniqueUserIDset() {
        Log.v("Unique User ID: ", PrefUtils(activity).uniqueUserID + "")
        // TODO: Set it as a variable somewhere? Anyway the Jams activity sees this info too
    }

    override fun jamIDset() {
        Log.v("Jam ID: ", prefUtils!!.jamID + "")
    }

    override fun jamPINset() {
        Log.v("Jam PIN: ", prefUtils!!.jamPIN + "")
    }

    override fun jamStartTimeSet() {
        Log.v("Jam Start Time: ", prefUtils!!.jamStartTime + "")
    }

    override fun jamEndTimeSet() {
        Log.v("Jam End Time: ", prefUtils!!.jamEndTime + "")
    }

    override fun getCollaboratorsSet() {
        Log.v("Collaborators: ", prefUtils!!.collaborators + "")

    }

    override fun getUserActivitySet() {
        Log.v("User Activity: ", prefUtils!!.userActivity + "")

    }

    override fun getJamDetailsSet() {
        Log.v("Jam Details: ", prefUtils!!.jamDetails + "")
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 4
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private val REQUEST_READ_CONTACTS = 0

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
    }
}
