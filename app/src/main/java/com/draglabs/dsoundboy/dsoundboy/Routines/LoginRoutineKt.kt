package com.draglabs.dsoundboy.dsoundboy.Routines

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.draglabs.dsoundboy.dsoundboy.Activities.TestNavActivity
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutilsKt
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtilsKt
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.facebook.login.widget.ProfilePictureView
import org.json.JSONObject
import java.util.*

/**
 * Created by davrukin on 11/2/17.
 * @author Daniel Avrukin
 */

class LoginRoutineKt {

    //private val mAuthTask: LoginActivity.UserLoginTask? = null
    //private var buttons: HashMap<String, Any>? = null
    private var activity: Activity? = null
    private var context: Context? = null

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val accessTokenTracker: AccessTokenTracker? = null
    private var accessToken: AccessToken? = null

    fun clickFacebookLogin(facebookLoginButton: LoginButton, activity: Activity, context: Context) {
        //this.buttons = buttons
        this.activity = activity
        this.context = context

        //facebookLoginButton.setReadPermissions("email");
        //val facebookLoginButton = buttons!!["facebookLoginButton"] as LoginButton
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"))
        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            /*internal var loginResultText = buttons["loginResultText"] as TextView
            internal var continueButton = buttons["continueButton"] as Button
            internal var mEmailSignInButton = buttons["emailSignInButton"] as Button*/

            override fun onSuccess(loginResult: LoginResult) {
                // app code
                accessToken = AccessToken.getCurrentAccessToken()
                val loginResultTextText = "UserModel ID: + " + loginResult.accessToken.userId + "\nAuth Token: " + loginResult.accessToken.token
                /*loginResultText.text = loginResultTextText
                continueButton.isEnabled = true
                mEmailSignInButton.isEnabled = false*/
                val request = GraphRequest.newMeRequest(accessToken) {`object`, response ->
                    var facebookID = ""
                    var facebookEmail = ""
                    var facebookName = ""

                    try {
                        if (`object`.has("id")) {
                            facebookID = `object`.getString("id")
                        }
                        if (`object`.has("email")) {
                            facebookEmail = `object`.getString("email")
                        }
                        if (`object`.has("name")) {
                            facebookName = `object`.getString("name")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    LogUtils.debug("GraphRequest Parameters", "facebookID: $facebookID\nfacebookEmail: $facebookEmail\nfacebookName: $facebookName")
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,email,name")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                // app code
                /*loginResultText.text = context.getString(R.string.login_attempt_canceled)
                mEmailSignInButton.isEnabled = true*/
            }

            override fun onError(error: FacebookException) {
                // app code
                /*loginResultText.text = context.getString(R.string.login_attempt_failed)
                mEmailSignInButton.isEnabled = true*/
            }
        })
    }

    fun facebookLogin() {
        //var callbackManager = CallbackManager.Factory.create()

    }

    fun setProfileView(response: JSONObject, activity: Activity, context: Context) {
        val name = response.getString("name")
        var email: String? = null
        if (response.has("email")) {
            email = response.getString("email")
        }
        val id = response.getString("id")

        if (email != null) {
            PrefUtilsKt.Functions().storeFbName(context, name)
            PrefUtilsKt.Functions().storeFbEmail(context, email)
            PrefUtilsKt.Functions().storeFbImage(context, id)
            val pictureView = activity.findViewById<ProfilePictureView>(R.id.user_picture)
            if (pictureView != null) {
                pictureView.presetSize = ProfilePictureView.NORMAL
                pictureView.profileId = id
            }

            val userNameTextView = activity.findViewById<TextView>(R.id.user_name)
            val userEmailTextView = activity.findViewById<TextView>(R.id.user_email)

            if (userNameTextView != null) {
                if (userNameTextView.text == null) {
                    userNameTextView.text = "John Smith"
                } else {
                    userNameTextView.text = name
                }
            }

            if (userEmailTextView != null) {
                if (userEmailTextView.text == null) {
                    userEmailTextView.text = "john.smith@contoso.net"
                } else {
                    userEmailTextView.text = email
                }
            }
        } else {
            PrefUtilsKt.Functions().storeFbName(context, name)
            //PrefUtilsKt.Functions().storeFbEmail(context, email)
            PrefUtilsKt.Functions().storeFbImage(context, id)
            val pictureView = activity.findViewById<ProfilePictureView>(R.id.user_picture)
            pictureView.presetSize = ProfilePictureView.NORMAL
            pictureView.profileId = id

            val userNameTextView = activity.findViewById<TextView>(R.id.user_name)
            val userEmailTextView = activity.findViewById<TextView>(R.id.user_email)
            userNameTextView.text = name
            userEmailTextView.text = name
        }
    }

    fun saveFacebookCredentials(loginResult: LoginResult) {
        //PrefUtilsKt.Functions().storeFbAccessToken // is this necessary?
    }

    /*fun addButton(string: String, `object`: Any) {
        buttons!![string] = `object`
    }*/

    fun clickContinueButton(view: View, activity: Activity, context: Context) {
        APIutilsKt.UserFunctions.performRegisterUser(activity, context)

        Snackbar.make(view, "Unique ID: " + PrefUtilsKt.Functions().retrieveUUID(context), Snackbar.LENGTH_LONG).show()
        val intent = Intent(activity, TestNavActivity::class.java)

        activity.startActivity(intent)
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
        private const val REQUEST_READ_CONTACTS = 0

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
    }
}
