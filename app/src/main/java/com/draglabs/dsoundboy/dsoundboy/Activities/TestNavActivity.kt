/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.HomeRoutine
import com.draglabs.dsoundboy.dsoundboy.Routines.HomeRoutineKt
import com.draglabs.dsoundboy.dsoundboy.Routines.LoginRoutine
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutilsKt
import com.draglabs.dsoundboy.dsoundboy.Utils.LocationUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtilsKt
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import kotlinx.android.synthetic.main.activity_test_nav.*
import kotlinx.android.synthetic.main.app_bar_test_nav.*
import kotlinx.android.synthetic.main.content_test_nav.*
import java.util.*

/**
 * @author Daniel Avrukin
 */
class TestNavActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    //var recorderUtils = RecorderUtils(this, null, this)
    lateinit var homeRoutine: HomeRoutine
    lateinit var homeRoutineKt: HomeRoutineKt
    lateinit var startTime: Date
    lateinit var endTime: Date
    var buttons = HashMap<String, Any>()
    /*val apiInterface by lazy {
        ApiInterface.create()
    }*/
    val postmanUUID = "5a4b230bbe307d06c7ad5c57"
    //var recorder = homeRoutine.recorder

    /* Location Vars */
    data class LocationVars(var googleApiClient: GoogleApiClient? = null,
                            var locationManager: LocationManager? = null,
                            var location: Location? = null,
                            var locationRequest: LocationRequest? = null,
                            var listener: com.google.android.gms.location.LocationListener? = null,
                            val updateInterval: Long = 2000.toLong(),
                            val fastestInterval: Long = 2000)
    private var locationVars = LocationVars()

    var locationUtils: LocationUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(this)
        setContentView(R.layout.activity_test_nav)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //PrefUtilsKt().storeUUID(this, postmanUUID)
        //println(PrefUtilsKt().retrieveUUID(this))
        //PrefUtilsKt.Functions().deleteUUID(this)
        //PrefUtilsKt.Functions().deletePIN(this)
        clickTestAuth()
        setListeners()
        setUserView()
        initializeLocationClient()

        buttons.put("new_jam", button_new_jam_new as Any)
        buttons.put("new_recording", button_rec_new as Any)
        buttons.put("join_jam", button_join_jam_new as Any)

        homeRoutine = HomeRoutine(buttons, this, this, Date().time.toString(), button_rec_new)
        homeRoutineKt = HomeRoutineKt(buttons, this, this, Date().time.toString(), button_rec_new)

        //Log.v("API ID:", PrefUtils(this).uniqueUserID)
    }

    private fun setListeners() {
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        button_new_jam_new.setOnClickListener {
            clickNew()
        }

        button_rec_new.setOnClickListener { view ->
            clickRec(this, view)
        }

        button_join_jam_new.setOnClickListener {
            clickJoin()
        }

        button_test_auth.setOnClickListener {
            clickTestAuth()
        }

        button_log_location.setOnClickListener {
            LogUtils.debug("Latitude", PrefUtilsKt.Functions().retrieveLatitude(this))
            LogUtils.debug("Longitude", PrefUtilsKt.Functions().retrieveLongitude(this))
        }
    }

    private fun setUserView() {
        val loginRoutine = LoginRoutine(null, this, this)

        val accessToken = AccessToken.getCurrentAccessToken()
        val graphRequest = GraphRequest.newMeRequest(accessToken) { `object`, response ->
            Log.v("Main: ", response.toString())
            loginRoutine.setProfileView(`object`)
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

    private fun registerUser() {
        LogUtils.debug("Registering User", "Done");

        APIutilsKt().performRegisterUser(this)

        LogUtils.debug("Registered User", "Done");
        LogUtils.debug("New UUID", PrefUtilsKt.Functions().retrieveUUID(this))
    }

    private fun clickRec(context: Context, view: View) {
        val buttonText = button_rec_new.text
        if (buttonText == "Rec") {
            startTime = Date()
            button_rec_new.text = "Stop"
            // not confident about the order of events here
            homeRoutineKt.clickRec(chronometer_new) // no current jam, recorder button creates a new one
            // if is currently recording, new jam button stops recording, uploads it, and creates a new jam
            // use get active jam to tell if there is one currently
        } else {
            endTime = Date()
            button_rec_new.text = "Start"
            homeRoutineKt.clickStop(context, view, chronometer_new, startTime, endTime)
            //homeRoutine = HomeRoutine(buttons, this, this, Date().time.toString(), button_rec_new)
        }
    }

    private fun clickJoin() {
        homeRoutineKt.joinJam(jam_pin_view)
    }

    private fun clickNew() {
        homeRoutineKt.createJam(this, jam_pin_view)

        /*APIutilsKt().performNewJam(this, PrefUtilsKt.Functions().retrieveUUID(this), "test_jam", "ActionSpot", 37, -22, "hi")
        LogUtils.debug("new pin", PrefUtilsKt.Functions().retrievePIN(this))
        jam_pin_view.text = PrefUtilsKt.Functions().retrievePIN(this)*/
        // maybe disable button until there exists a UUID
    }

    private fun clickTestAuth() {
        val text = PrefUtilsKt.Functions().retrieveUUID(this)
        if (text == "not working") {
            registerUser()
            LogUtils.debug("Button UUID Broken, registered", text)
        } else {
            LogUtils.debug("Button UUID Working", text)
        }
    }

    // TODO: add check uuid function, to prevent crashes, or just show snackbars or toasts when errors come up, so the app doesn't crash

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.test_nav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_rec -> {
                // Handle the camera action
                // go to the default record screen
            }
            R.id.nav_recordings -> {
                startActivity(Intent(this, ListOfJamsActivity::class.java))
                // go to the previous recordings
                // allow export of jams
            }
            R.id.nav_slideshow -> {
                // optional or modify
            }
            R.id.nav_manage -> {
                // optional or modify
            }
            R.id.nav_settings -> {
                // show settings such as recording settings
            }
            R.id.nav_log_out -> {
                AccessToken.setCurrentAccessToken(null)
                startActivity(Intent(this, NewLoginActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        locationUtils?.onStart(locationVars)
    }

    override fun onStop() {
        super.onStop()
        locationUtils?.onStop(locationVars)
    }

    override fun onConnectionSuspended(p0: Int) {
        locationUtils?.onConnectionSuspended(locationVars, p0)
    }

   override fun onConnectionFailed(p0: ConnectionResult) {
       locationUtils?.onConnectionFailed(p0)
    }

    override fun onLocationChanged(p0: Location?) {
        locationUtils?.onLocationChanged(this, p0)
    }

    override fun onConnected(p0: Bundle?) {
        locationUtils?.onConnected(this, this, p0, locationVars)
    }

    private fun initializeLocationClient() {
        locationUtils?.initializeLocationClient(this, this, this, locationVars)
    }
}
