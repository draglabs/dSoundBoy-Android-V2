/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.HomeRoutineKt
import com.draglabs.dsoundboy.dsoundboy.Routines.LoginRoutineKt
import com.draglabs.dsoundboy.dsoundboy.Services.LocationTrackingService
import com.draglabs.dsoundboy.dsoundboy.Tasks.OfflineUploader
import com.draglabs.dsoundboy.dsoundboy.Utils.*
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_test_nav.*
import kotlinx.android.synthetic.main.app_bar_test_nav.*
import kotlinx.android.synthetic.main.content_test_nav.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import omrecorder.Recorder
import java.util.*

/**
 * @author Daniel Avrukin
 */
class TestNavActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    //var recorderUtils = RecorderUtils(this, null, this)
    //lateinit var homeRoutineKt: HomeRoutineKt
    private lateinit var startTime: Date
    private lateinit var endTime: Date
    private lateinit var filename: String
    private var buttons = HashMap<String, Any>()
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

    private var locationUtils: LocationUtils? = null

    //private var jams: RealmResults<JamViewModel>? = null

    //private var realm = RealmUtils().startRealm()

    private lateinit var jamPinView: Button
    private lateinit var rec: Button
    private lateinit var stop: Button

    private lateinit var recorder: Recorder
    private lateinit var recorderUtils: RecorderUtils

    private lateinit var gestureDetector: GestureDetectorCompat

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(this)
        setContentView(R.layout.activity_test_nav)
        setSupportActionBar(toolbar)

        //getJams()

        //recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        //linearLayoutManager = LinearLayoutManager(this)
        //recyclerView.layoutManager = linearLayoutManager
        //customAdapter = CustomAdapter(this, jams)
        //recyclerView.adapter = customAdapter

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //PrefUtilsKt().storeUUID(this, postmanUUID)
        //println(PrefUtilsKt().retrieveUUID(this))
        //PrefUtilsKt.Functions().deleteUUID(this)
        //PrefUtilsKt.Functions().deletePIN(this)
        //filename = FileUtils().generateAndSaveFilename(this)
        recorderUtils = RecorderUtils(this, this)
        //recorder = recorderUtils.setupRecorder(filename, findViewById(R.id.image_mic))

        initialize()

        buttons["new_jam"] = button_new_jam_new as Any
        buttons["new_recording"] = button_rec_new as Any
        buttons["join_jam"] = button_join_jam_new as Any

        //homeRoutineKt = HomeRoutineKt(buttons, this, this,   filename, button_rec_new)

        //Log.v("API ID:", PrefUtils(this).uniqueUserID)
        realm = Realm.getDefaultInstance()
        var service = startService(Intent(this, LocationTrackingService::class.java))
        LogUtils.debug("FB Access Token", "${AccessToken.getCurrentAccessToken()}")
    }

    private fun initialize() {
        jamPinView = findViewById<Button>(R.id.jam_pin_view)
        jamPinView.bringToFront()

        gestureDetector = GestureDetectorCompat(this, MyGestureListener())

        clickTestAuth()
        setListeners()
        async { setUserView() }
        initializeLocationClient()
        updatePinView()

        rec = findViewById<Button>(R.id.button_rec_new)
        stop = findViewById<Button>(R.id.button_stop_new)

        //stop.isEnabled = false
        stop.visibility = View.GONE

        //doPermissionsCheckWriteExternalStorage()

        //realm = Realm.getDefaultInstance()
        async { OfflineUploader().queueInteractor(this@TestNavActivity) }
    }

    private fun setListeners() {
        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        button_new_jam_new.setOnClickListener {
            //doPermissionsCheckWriteExternalStorage()
            doPermissionsCheckRead()
            doPermissionsCheckWrite()
            clickNew()
        }

        button_rec_new.setOnClickListener { view ->
            filename = FileUtils().generateAndSaveFilename(this)
            recorder = recorderUtils.setupRecorder(filename, findViewById(R.id.image_mic))
            clickRec(this, view, recorder)
            rec.visibility = View.GONE
            stop.visibility = View.VISIBLE
        }

        button_stop_new.setOnClickListener { view ->
            LogUtils.debug("Button Clicked", "Stop")
            clickStop(this, view, recorder)
            initiateOfflineUploader()
            async { OfflineUploader().queueInteractor(this@TestNavActivity) }
            rec.visibility = View.VISIBLE
            stop.visibility = View.GONE
        }

        button_join_jam_new.setOnClickListener {
            //doPermissionsCheckWriteExternalStorage()
            doPermissionsCheckRead()
            doPermissionsCheckWrite()
            clickJoin()
        }

        jamPinView.setOnClickListener {
            updatePinView()
            LogUtils.logAppSettings(this@TestNavActivity)
        }

        jamPinView.setOnDragListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                LogUtils.debug("Motion Event", "Action Up")
                val intent = Intent(this, EditJamActivity::class.java)
                intent.putExtra("jamID", PrefUtilsKt.Functions().retrieveJamID(this))
                intent.putExtra("jamName", PrefUtilsKt.Functions().retrieveJamName(this))
                intent.putExtra("jamLocation", "ActionSpot")
                intent.putExtra("jamNotes", "hi") // edit later, maybe get from Realm
                startActivity(intent)
                return@setOnDragListener true
            } else {
                return@setOnDragListener false
            }
        }

        /*button_test_auth.setOnClickListener {
            clickTestAuth()
        }

        button_log_location.setOnClickListener {
            LogUtils.debug("Latitude", PrefUtilsKt.Functions().retrieveLatitude(this))
            LogUtils.debug("Longitude", PrefUtilsKt.Functions().retrieveLongitude(this))
        }*/
    }

    private fun initiateOfflineUploader() {
        LogUtils.debug("Entering Function", "initiateOfflineUploader")
        //endTime = Date()
        val jamID = PrefUtilsKt.Functions().retrieveJamID(this) // current jam id
        val jamName = PrefUtilsKt.Functions().retrieveFbName(this)
        //OfflineUploader().prepareUpload(realm, jamID, this, jamPinView, recorder, recorderUtils, view, chronometer_new, startTime, endTime)
        OfflineUploader().addRecordingToQueue(realm, filename, jamID, jamName, startTime.toString(), endTime.toString())
    } // driedSpaghetti

    private fun doPermissionsCheckWriteExternalStorage() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        LogUtils.debug("Permission Check: ", "$permissionCheck")
    }

    private fun doPermissionsCheckRead() {
        val read = isReadStoragePermissionGranted
        LogUtils.debug("Permission Code: ", "read: $read")
    }

    private fun doPermissionsCheckWrite() {
        val write = isWriteStoragePermissionGranted
        LogUtils.debug("Permission Code: ", "write: $write")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
        /*val action = MotionEventCompat.getActionMasked(event)
        if (action == MotionEvent.ACTION_UP) {
            LogUtils.debug("Motion Event", "Action Up")
            val intent = Intent(this, EditJamActivity::class.java)
            intent.putExtra("jamID", PrefUtilsKt.Functions().retrieveJamID(this))
            intent.putExtra("jamName", PrefUtilsKt.Functions().retrieveJamName(this))
            intent.putExtra("jamLocation", "ActionSpot")
            startActivity(intent)
            return true
        } else {
            return super.onTouchEvent(event)
        }*/
    }

    private class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val DEBUG_TAG = "Gestures"

        override fun onDown(e: MotionEvent?): Boolean {
            LogUtils.debug("Action", "OnDown, event: $e")
            return super.onDown(e)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            LogUtils.debug("Action", "OnFling, e1: $e1, e2: $e2")
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            LogUtils.debug("Action", "OnSingleTapUp, event: $e")
            return super.onSingleTapUp(e)
        }
    }

    private suspend fun setUserView() {
        delay(1500) // this is here because it would cause the app to crash since it didn't retrieve the data from the server right away
        //val loginRoutineKt = LoginRoutineKt(buttons, this, this)

        val accessToken = AccessToken.getCurrentAccessToken()
        val graphRequest = GraphRequest.newMeRequest(accessToken) { `object`, response ->
            if (`object` != null) {
                LogUtils.debug("Main: ", response.toString())
                LoginRoutineKt().setProfileView(`object`, this, this) // TODO: save data even for when offline
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

    private fun registerUser() {
        LogUtils.debug("Registering User", "Done")

        APIutilsKt.UserFunctions.performRegisterUser(this, this)

        LogUtils.debug("Registered User", "Done")
        LogUtils.debug("New UUID", PrefUtilsKt.Functions().retrieveUUID(this))
    }

    private fun clickRec(context: Context, view: View, recorder: Recorder) {
        updatePinView()
        //if (button_rec_new.text == "Rec") {
            startTime = Date()
            // not confident about the order of events here
            HomeRoutineKt().clickRec(this, recorder, recorderUtils, chronometer_new) // no current jam, recorder button creates a new one
            ////button_rec_new.text = getString(R.string.stop_recording_text) // "Stop"
            // if is currently recording, new jam button stops recording, uploads it, and creates a new jam
            // use get active jam to tell if there is one currently
        /*} else {
            endTime = Date()
            HomeRoutineKt().clickStop(recorder, recorderUtils, context, view, chronometer_new, startTime, endTime)
            button_rec_new.text = getString(R.string.start_recording_text) // "Rec"
            //homeRoutine = HomeRoutine(buttons, this, this, Date().time.toString(), button_rec_new)
        }*/
        updatePinView()
    }

    private fun clickStop(context: Context, view: View, recorder: Recorder) {
        updatePinView()

        endTime = Date()
        //val jamID = PrefUtilsKt.Functions().retrieveJamID(context)
        HomeRoutineKt().clickStop(recorder, recorderUtils, context, chronometer_new)

        updatePinView()
    }

    private fun clickJoin() {
        HomeRoutineKt().joinJam(this, this, jam_pin_view)
        updatePinView()
    }

    private fun clickNew() {
        HomeRoutineKt().createJam(this, jam_pin_view)
        updatePinView()

        /*APIutilsKt().performNewJam(this, PrefUtilsKt.Functions().retrieveUUID(this), "test_jam", "ActionSpot", 37, -22, "hi")
        LogUtils.debug("new pin", PrefUtilsKt.Functions().retrievePIN(this))
        jam_pin_view.text = PrefUtilsKt.Functions().retrievePIN(this)*/
        // maybe disable button until there exists a UUID
    }

    private fun clickTestAuth() {
        APIutilsKt.UserFunctions.performRegisterUser(this, this)
        val text = PrefUtilsKt.Functions().retrieveUUID(this)
        if (text == "not working") {
            registerUser()
            LogUtils.debug("Button UUID Broken, registered", text)
        } else {
            LogUtils.debug("Button UUID Working", text)
        }
    }

    // TODO: add check uuid function, to prevent crashes, or just show snackbars or toasts when errors come up, so the app doesn't crash

    private fun updatePinView() {
        /*val pin = PrefUtilsKt.Functions().retrievePIN(this)
        val currentJamID = PrefUtilsKt.Functions().retrieveJamID(this)
        //val currentJamDetails = APIutilsKt.JamFunctions.getJamDetails(this, currentJamID)
        LogUtils.debug("UpdatePinView", "pin $pin, jamID $currentJamID")
        val newPIN = RealmUtils.JamViewModelUtils.Retrieve.retrieveJamPinWithID(currentJamID)
        LogUtils.debug("UpdatePinView", "newPIN $newPIN")*/ // TODO: actually do GetActiveJam

        APIutilsKt.JamFunctions.performGetActiveJam(this)
        //val activeJamID = PrefUtilsKt.Functions().retrieveJamID(this)
        val activeJamPIN = PrefUtilsKt.Functions().retrievePIN(this)

        if (activeJamPIN == "not working") {
            jamPinView.text = "No Jam PIN"
            //Toast.makeText(this, "API currently offline. Please check again later.", Toast.LENGTH_LONG).show()
            Toast.makeText(this, "No current jam. Please click either New or Join to get a PIN.", Toast.LENGTH_LONG).show()
            // TODO: check if currently online by running check on server, if it is successful, tell user to create a new jam
        } else {
            jamPinView.text = activeJamPIN // TODO: Instead of retrieving the PIN, perform an API call to get the current jams pin
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            realm.close()
        } else {
            realm.close()
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
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_rec -> {
                // Handle the camera action
                // go to the default record screen
            }
            R.id.nav_jams -> {
                startActivity(Intent(this, ListOfJamsActivity::class.java))
                // go to the previous recordings
                // allow export of jams
            }
            R.id.nav_authorize -> {
                clickTestAuth()
                // optional or modify
            }
            R.id.nav_log_location -> {
                LogUtils.debug("Latitude", PrefUtilsKt.Functions().retrieveLatitude(this))
                LogUtils.debug("Longitude", PrefUtilsKt.Functions().retrieveLongitude(this))
            }
            R.id.nav_band_info -> {
                // optional or modify
                startActivity(Intent(this, EnterInfoActivity::class.java))
            }
            R.id.nav_instructions -> {
                startActivity(Intent(this, InstructionsActivity::class.java))
            }
            R.id.nav_settings -> {
                // show settings such as recording settings
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_log_out -> {
                HomeRoutineKt().clickLogout()
                Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, NewLoginActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        locationUtils?.onStart(locationVars)
        super.onStart()
        /*if (jams.size == 0) {
            getJams()
        }*/
    }

    override fun onStop() {
        locationUtils?.onStop(locationVars)
        //realm.close()
        super.onStop()
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

    //permission is automatically granted on sdk<23 upon installation
    private val isReadStoragePermissionGranted: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    LogUtils.debug("Permissions", "Permission is granted1")
                    true
                } else {

                    LogUtils.debug("Permissions", "Permission is revoked1")
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 3)
                    false
                }
            } else {
                LogUtils.debug("Permissions", "Permission is granted1")
                true
            }
        }

    //permission is automatically granted on sdk<23 upon installation
    private val isWriteStoragePermissionGranted: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    LogUtils.debug("Permissions", "Permission is granted2")
                    true
                } else {

                    LogUtils.debug("Permissions", "Permission is revoked2")
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                    false
                }
            } else {
                LogUtils.debug("Permissions", "Permission is granted2")
                true
            }
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                LogUtils.debug("Permissions", "External storage2")
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtils.debug("Permissions", "Permission: " + permissions[0] + "was " + grantResults[0])
                    //resume tasks needing this permission
                    //downloadPdfFile()
                } else {
                    //progress.dismiss()
                }
            }
            3 -> {
                LogUtils.debug("Permissions", "External storage1")
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtils.debug("Permissions", "Permission: " + permissions[0] + "was " + grantResults[0])
                    //resume tasks needing this permission
                    //SharePdfFile()
                } else {
                    //progress.dismiss()
                }
            }
        }
    }
}
