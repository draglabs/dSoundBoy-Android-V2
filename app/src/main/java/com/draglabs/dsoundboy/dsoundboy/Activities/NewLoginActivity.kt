package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.LoginRoutineKt
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutilsKt
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.util.*

/**
 * The new and improved login activity, with just a single button!
 * @author Daniel Avrukin
 */
class NewLoginActivity : AppCompatActivity() {

    private var callbackManager: CallbackManager? = null
    private var loginRoutineKt: LoginRoutineKt? = null
    private var accessToken: AccessToken? = null
    private var accessTokenTracker: AccessTokenTracker? = null

    /**
     * The onCreate method for the new login activity
     * @param savedInstanceState the saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))

        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(this)
        callbackManager = CallbackManager.Factory.create()

        setContentView(R.layout.activity_new_login)

        val buttons = HashMap<String, Any>()

        loginRoutineKt = LoginRoutineKt(buttons, this, this)

        val loginButton = findViewById<View>(R.id.login_button) as LoginButton
        loginButton.setReadPermissions("email")
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("UserModel ID: ", loginResult.accessToken.userId)
                Log.d("Access Token: ", loginResult.accessToken.token)
                Log.d("Application ID: ", loginResult.accessToken.applicationId)

                val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                    Log.v("Main: ", response.toString())
                    loginRoutineKt!!.setProfileView(`object`)
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email")
                graphRequest.parameters = parameters
                graphRequest.executeAsync()

                //loginRoutineKt!!.saveFacebookCredentials(loginResult)
                //loginRoutineKt!!.authenticateUser()
                APIutilsKt().performRegisterUser(this@NewLoginActivity)
            }

            override fun onCancel() {
                Log.d("FB Login Cancelled", "")
            }

            override fun onError(error: FacebookException) {
                Log.d("Facebook Error: ", error.toString())
            }
        })

        accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken, currentAccessToken: AccessToken) {
                // set the access token using currentAccessToken when it's loaded or set
                accessToken = currentAccessToken
            }
        }

        //buttons.put("facebookLoginButton", loginButton);
        loginRoutineKt!!.addButton("facebookLoginButton", loginButton)

        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("Access Token:", AccessToken.getCurrentAccessToken().toString())
            val homeIntent = Intent(this, TestNavActivity::class.java)
            startActivity(homeIntent)
        }
    }

    /**
     * What happens when the login is successful, it calls back to the CallbackManager
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the data from the intent
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * If the login process is cancelled
     */
    public override fun onDestroy() {
        super.onDestroy()
        accessTokenTracker!!.stopTracking()
    }

    /**
     * Performs the Facebook Login
     * @param view the view calling the login
     */
    fun clickFacebookLogin(view: View) {
        loginRoutineKt!!.clickFacebookLogin()
    }

    /**
     * Clicking this takes the user to the DragLabs home page
     * @param view the view calling this method
     */
    fun clickLogoLink(view: View) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"))
        startActivity(browserIntent)
    }
}
