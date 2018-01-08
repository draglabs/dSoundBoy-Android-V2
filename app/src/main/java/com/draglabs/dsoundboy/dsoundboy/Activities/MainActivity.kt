package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.MainRoutine
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtilsKt
import com.facebook.AccessToken


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

        //startActivity(new Intent(this, TestNavActivity.class));
        //startActivity(new Intent(this, NewLoginActivity.class));

        //val prefUtils = PrefUtils(this)
        //val UUID = prefUtils.uniqueUserID
        val UUID = PrefUtilsKt.Functions().retrieveUUID(this)
        val accessToken = AccessToken.getCurrentAccessToken()

        if ((UUID == "not working") && accessToken == null) {
            val loginIntent = Intent(this, NewLoginActivity::class.java)
            startActivity(loginIntent)
        } else {
            Log.d("FB ID: ", AccessToken.getCurrentAccessToken().userId)
            Log.d("FB Access Token: ", AccessToken.getCurrentAccessToken().token)
            val homeIntent = Intent(this, TestNavActivity::class.java)
            startActivity(homeIntent)
        }
    }
}