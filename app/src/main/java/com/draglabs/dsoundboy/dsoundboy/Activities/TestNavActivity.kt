package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.draglabs.dsoundboy.dsoundboy.Interfaces.ApiInterface
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.HomeRoutine
import com.draglabs.dsoundboy.dsoundboy.Routines.HomeRoutineKt
import com.draglabs.dsoundboy.dsoundboy.Routines.LoginRoutine
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.Profile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_test_nav.*
import kotlinx.android.synthetic.main.app_bar_test_nav.*
import kotlinx.android.synthetic.main.content_test_nav.*
import java.util.*

class TestNavActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //var recorderUtils = RecorderUtils(this, null, this)
    lateinit var homeRoutine: HomeRoutine
    lateinit var homeRoutineKt: HomeRoutineKt
    lateinit var startTime: Date
    lateinit var endTime: Date
    var buttons = HashMap<String, Any>()
    var disposable: Disposable? = null
    val apiInterface by lazy {
        ApiInterface.create()
    }
    //var recorder = homeRoutine.recorder

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

        registerUser()
        setListeners()
        setUserView()

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
            //APIutils.newJam(this, this, PrefUtils(this).uniqueUserID, "ActionSpot", "My Test Jam", Location("12345"))
            homeRoutine.createJam()
        }

        button_rec_new.setOnClickListener { view ->
            val buttonText = button_rec_new.text
            if (buttonText == "Rec") {
                startTime = Date()
                button_rec_new.text = "Stop"
                homeRoutine.clickRec(chronometer_new) // not confident about the order of events here
            } else {
                endTime = Date()
                button_rec_new.text = "Start"
                homeRoutine.clickStop(view, chronometer_new, startTime, endTime)
                homeRoutine = HomeRoutine(buttons, this, this, Date().time.toString(), button_rec_new)
            }
        }

        button_join_jam_new.setOnClickListener {
            homeRoutineKt.joinJam()
        }

        // new, rec, join, chronometer
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
        //APIutils.registerUser(this, this)
        val facebookID = Profile.getCurrentProfile().id
        val accessToken = AccessToken.getCurrentAccessToken().token

        disposable = apiInterface.registerUser(facebookID, accessToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result ->
                    PrefUtils.writeUUID(result.id)
                    LogUtils.debug("UUID", result.id)
                },
                {error -> LogUtils.debug("Error Registering User", error.message + "")}
            )

        LogUtils.debug("Registered User", "Done");
    }

    private fun newJam(name: String, location: String, latitude: Long, longitude: Long, notes: String) {
        disposable = apiInterface.newJam(name, location, latitude, longitude, notes)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result -> LogUtils.debug("NewJam Result", result.toString())},
                {error -> LogUtils.debug("NewJam Error", error.message + "")}
            )
    }

    fun joinJam(jamPIN: String, UUID: String) {
        disposable = apiInterface.joinJam(jamPIN, UUID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result -> LogUtils.debug("JoinJam Result", result.toString())},
                {error -> LogUtils.debug("JoinJam Error", error.message + "")}
            )
    }

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
                startActivity(Intent(this, AboutActivity::class.java))
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
        disposable?.dispose()
    }
}
