package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.LoginRoutineKt
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutilsKt
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.RealmUtils
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.util.*
import com.facebook.ProfileTracker

// image source: http://wallpaperswide.com/concert_crowd-wallpapers.html

/**
 * The new and improved login activity, with just a single button!
 * @author  ---------
 */
class NewLoginActivity : AppCompatActivity() {

    private var callbackManager: CallbackManager? = null
    //private var loginRoutineKt: LoginRoutineKt? = null
    private var accessToken: AccessToken? = null
    private var accessTokenTracker: AccessTokenTracker? = null
    private var profileTracker: ProfileTracker? = null

    private var fbLoginButton: LoginButton? = null
    private var signUpButton: Button? = null
    private var loginButton: Button? = null

    //private var buttons: HashMap<String, Any>? = null

    /**
     * The onCreate method for the new login activity
     * @param savedInstanceState the saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        //@Suppress("DEPRECATION")
        //FacebookSdk.sdkInitialize(this)

        setContentView(R.layout.activity_new_login)

        //buttons = HashMap()

        //loginRoutineKt = LoginRoutineKt()

        setElements()
        setListeners()
        setAccessTokenTrackers()
        /*fbLoginButton.setReadPermissions("email")
        fbLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
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
                APIutilsKt.UserFunctions.performRegisterUser(this@NewLoginActivity,this@NewLoginActivity)
            }

            override fun onCancel() {
                Log.d("FB Login Cancelled", "")
            }

            override fun onError(error: FacebookException) {
                Log.d("Facebook Error: ", error.toString())
            }
        })*/

        //buttons.put("facebookLoginButton", fbLoginButton);
    }

    private fun setElements() {
        fbLoginButton = findViewById<LoginButton>(R.id.login_button)
        signUpButton = findViewById<Button>(R.id.login_screen_button_sign_up)
        loginButton = findViewById<Button>(R.id.login_screen_button_login)
        //LoginRoutineKt().addButton("facebookLoginButton", fbLoginButton as Any)
    }

    private fun setListeners() {
        fbLoginButton!!.setOnClickListener {
            facebookLogin()
        }

        signUpButton!!.setOnClickListener {
            Toast.makeText(this, "Sign Up Button Clicked", Toast.LENGTH_LONG).show()
            // launch intent to go to login screen
            val intent = Intent(this, EmailLoginActivity::class.java)
            intent.putExtra("type", "sign_up")
            startActivity(intent)
        }

        loginButton!!.setOnClickListener {
            Toast.makeText(this, "Login Button Clicked", Toast.LENGTH_LONG).show()
            val intent = Intent(this, EmailLoginActivity::class.java)
            intent.putExtra("type", "log_in")
            startActivity(intent)
        }
    }

    private fun setAccessTokenTrackers() {
        accessTokenTracker = object: AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken?, currentAccessToken: AccessToken) {
                // set the access token using currentAccessToken when it's loaded or set
                accessToken = currentAccessToken
            }
        }

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
        callbackManager?.onActivityResult(requestCode, resultCode, data)
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
        LoginRoutineKt().clickFacebookLogin(fbLoginButton!!, this, this)
    }

    /**
     * Clicking this takes the user to the DragLabs home page
     * @param view the view calling this method
     */
    fun clickLogoLink(view: View) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"))
        startActivity(browserIntent)
    }

    private fun facebookLogin() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val fbID = loginResult.accessToken.userId
                val fbAccessToken = loginResult.accessToken.token
                val fbApplicationID = loginResult.accessToken.applicationId

                val realm = RealmUtils.Functions.startRealm()
                RealmUtils.UserModelUtils.Initialize.initializeUserModel(realm, this@NewLoginActivity, fbID, fbAccessToken)
                realm.close()

                LogUtils.debug("UserModel ID: ", fbID)
                LogUtils.debug("Access Token: ", fbAccessToken)
                LogUtils.debug("Application ID: ", fbApplicationID)
                val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                    Log.v("Main: ", response.toString())
                    LoginRoutineKt().setProfileView(`object`, this@NewLoginActivity, this@NewLoginActivity)
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email")
                graphRequest.parameters = parameters
                graphRequest.executeAsync()

                if (Profile.getCurrentProfile() == null) {
                    profileTracker = object : ProfileTracker() {
                        override fun onCurrentProfileChanged(oldProfile: Profile, currentProfile: Profile) {
                            Log.v("facebook - profile", currentProfile.firstName)
                            profileTracker?.stopTracking()
                        }
                    }
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                } else {
                    val profile = Profile.getCurrentProfile()
                    Log.v("facebook - profile", profile.firstName)
                }

                //loginRoutineKt!!.saveFacebookCredentials(loginResult)
                //loginRoutineKt!!.authenticateUser()
                APIutilsKt.UserFunctions.performRegisterUser(this@NewLoginActivity,this@NewLoginActivity)

                startActivity(Intent(this@NewLoginActivity, TestNavActivity::class.java))
            }

            override fun onCancel() {
                Log.d("FB Login Cancelled", "")
            }

            override fun onError(error: FacebookException) {
                Log.d("Facebook Error: ", error.toString())
            }
        })
    }
}
