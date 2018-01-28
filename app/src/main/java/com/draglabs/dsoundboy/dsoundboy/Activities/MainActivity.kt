package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.MainRoutine
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtilsKt
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import io.realm.Realm


/**
 * Main Activity which loads pertinent screen depending on access
 * @author Daniel Avrukin
 */
class MainActivity : AppCompatActivity() {

    /**
     * onCreate method which redirects
     * @param savedInstanceState the saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainRoutine().facebookAuthorize(this)
        //new MainRoutine().googleAuthorize();
        setContentView(R.layout.activity_main)

        Realm.init(this)

        //startActivity(new Intent(this, TestNavActivity.class));
        //startActivity(new Intent(this, NewLoginActivity.class));

        //val prefUtils = PrefUtils(this)
        //val UUID = prefUtils.uniqueUserID
        val UUID = PrefUtilsKt.Functions().retrieveUUID(this)
        val accessToken = AccessToken.getCurrentAccessToken()

        if ((UUID == "not working") && accessToken == null) {
            val loginIntent = Intent(this, NewLoginActivity::class.java)
            startActivity(loginIntent)
        } else if (accessToken == null) {
            MainRoutine().facebookAuthorize(this)
        } else {
            if (accessToken.token == null || accessToken.userId == null) {
                MainRoutine().facebookAuthorize(this)
            }
            LogUtils.debug("FB ID: ", accessToken.userId)
            LogUtils.debug("FB Access Token: ", accessToken.token.toString())
            val homeIntent = Intent(this, TestNavActivity::class.java)
            startActivity(homeIntent)
        }
    }

    /**
     * Goes to the same activity for a redirect when the logo is clicked on the splash screen
     */
    fun clickLogo(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Overrides system onBackPressed to go back to the home screen when on the splash screen
     */
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}