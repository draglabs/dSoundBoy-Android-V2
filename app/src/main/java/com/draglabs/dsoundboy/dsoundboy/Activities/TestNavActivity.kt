/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Activities

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
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
import com.draglabs.dsoundboy.dsoundboy.Routines.TestNavRoutine
import com.draglabs.dsoundboy.dsoundboy.Services.AudioRecordingService
import com.draglabs.dsoundboy.dsoundboy.Services.LocationTrackingService
import com.draglabs.dsoundboy.dsoundboy.Tasks.OfflineUploader
import com.draglabs.dsoundboy.dsoundboy.Utils.*
import com.facebook.AccessToken
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
import java.io.File
import java.util.*
import kotlin.concurrent.thread

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

    private lateinit var activeJamPIN: String

    private lateinit var audioRecordingService: AudioRecordingService
    private lateinit var audioRecordingServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_nav)
        setSupportActionBar(toolbar)

        initialize()
        LogUtils.debug("FB Access Token", "${AccessToken.getCurrentAccessToken()}")
    }

    private fun initialize() {
        LogUtils.logEnteringFunction("initialize")

        initializeUIElements()
        initializeServices()
        initializeTasks()

        clickTestAuth()
        initializeListeners()
        initializeLocationClient()

        //initializeTrueTime()

        updatePinView()
    }

    private fun initializeUIElements() {
        LogUtils.logEnteringFunction("initializeUIElements")

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        jamPinView = findViewById<Button>(R.id.jam_pin_view)
        jamPinView.bringToFront()

        rec = findViewById<Button>(R.id.button_rec_new)
        stop = findViewById<Button>(R.id.button_stop_new)
        stop.visibility = View.GONE

        buttons["new_jam"] = button_new_jam_new as Any
        buttons["new_recording"] = button_rec_new as Any
        buttons["join_jam"] = button_join_jam_new as Any
    }

    private fun initializeServices() {
        LogUtils.logEnteringFunction("initializeServices")

        gestureDetector = GestureDetectorCompat(this, MyGestureListener())
        realm = Realm.getDefaultInstance()
        var locationService = startService(Intent(this, LocationTrackingService::class.java))
        recorderUtils = RecorderUtils(this, this)

        //initializeRecordingService() // should this start here or only when recording?
    }

    private fun initializeRecordingService() {
        audioRecordingService = AudioRecordingService()
        audioRecordingServiceIntent = Intent(this, audioRecordingService.javaClass)
        if (!TestNavRoutine().isServiceRunning(audioRecordingService.javaClass, this)) {
            startService(audioRecordingServiceIntent)
            LogUtils.info("AudioRecordingService Status", "Started")
        }
    }

    private fun initializeTasks() {
        LogUtils.logEnteringFunction("initializeTasks")

        async { setUserView() }
        async { OfflineUploader().queueInteractor(this@TestNavActivity) }

        val updatePinViewHandler = Handler()
        val runnableCode = object : Runnable {
            override fun run() {
                LogUtils.debug("Handlers", "Called on main thread, updating PIN view")

                updatePinView()

                updatePinViewHandler.postDelayed(this, 2000)
            }
        }
        updatePinViewHandler.post(runnableCode)
    }

    /*private fun initializeTrueTime() {
        thread { TrueTime.build().initialize() }
    }*/

    private fun initializeListeners() {
        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        button_new_jam_new.setOnClickListener {
            clickNew()
            clickRec(it)
        }

        button_rec_new.setOnClickListener {
            clickRec(it)
        }

        button_stop_new.setOnClickListener {
            clickStop(this, recorder)
        }

        button_join_jam_new.setOnClickListener {
            clickJoin()
            clickRec(it)
        }

        jamPinView.setOnClickListener {
            updatePinView()
            LogUtils.logAppSettings(this@TestNavActivity)
        }

        jamPinView.setOnDragListener { view, event ->
            return@setOnDragListener jamPinViewDragged(event)
        }

        /*button_test_auth.setOnClickListener {
            clickTestAuth()
        }

        button_log_location.setOnClickListener {
            LogUtils.debug("Latitude", PrefUtilsKt.Functions().retrieveLatitude(this))
            LogUtils.debug("Longitude", PrefUtilsKt.Functions().retrieveLongitude(this))
        }*/
    }

    private fun jamPinViewDragged(event: DragEvent): Boolean {
        return if (event.action == MotionEvent.ACTION_UP) {
            LogUtils.debug("Motion Event", "Action Up")
            val intent = Intent(this, EditJamActivity::class.java)
            intent.putExtra("jamID", PrefUtilsKt.Functions().retrieveJamID(this))
            intent.putExtra("jamName", PrefUtilsKt.Functions().retrieveJamName(this))
            intent.putExtra("jamLocation", "ActionSpot")
            intent.putExtra("jamNotes", "hi") // edit later, maybe get from Realm
            startActivity(intent)
            true
        } else {
            false
        }
    }

    private fun initiateOfflineUploader() {
        LogUtils.logEnteringFunction("initiateOfflineUploader")

        //endTime = Date()
        val jamID = PrefUtilsKt.Functions().retrieveJamID(this) // current jam id
        val jamName = PrefUtilsKt.Functions().retrieveFbName(this)
        //OfflineUploader().prepareUpload(realm, jamID, this, jamPinView, recorder, recorderUtils, view, chronometer_new, startTime, endTime)
        val formattedStartTime = FileUtils().getFormattedDate(startTime)
        val formattedEndTime = FileUtils().getFormattedDate(endTime)
        OfflineUploader().addRecordingToQueue(realm, filename, jamID, jamName, formattedStartTime, formattedEndTime)
    } // driedSpaghetti

    private fun doPermissionsCheckWriteExternalStorage() {
        LogUtils.logEnteringFunction("doPermissionsCheckWriteExternalStorage")

        // delay(300)
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        LogUtils.debug("Permission Check", "$permissionCheck")
        if (permissionCheck != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            showPermissionsDialog()
        }
        File("${Environment.getExternalStorageDirectory()}/test.txt").writeText("abc")
        File("${Environment.getExternalStorageDirectory()}/test.txt").delete()
    }

    private fun showPermissionsDialog() {
        LogUtils.logEnteringFunction("showPermissionsDialog")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Please fix app permissions")
        builder.setMessage("After clicking \"Okay\", please enable all listed permissions for proper app functioning. Once done, back out and click \"Done\". Thank you.")
        builder.setCancelable(false)
        builder.setPositiveButton("Okay") { dialogInterface, _ ->
            LogUtils.debug("showPermissionsDialog", "Okay Clicked")
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
            dialogInterface.dismiss()
        }
        builder.setNeutralButton("Done") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun doPermissionsCheckRead() {
        LogUtils.logEnteringFunction("doPermissionsCheckRead")

        val read = isReadStoragePermissionGranted
        LogUtils.debug("Permission Code: ", "read: $read")
    }

    private fun doPermissionsCheckWrite() {
        LogUtils.logEnteringFunction("doPermissionsCheckWrite")

        val write = isWriteStoragePermissionGranted
        LogUtils.debug("Permission Code: ", "write: $write")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        LogUtils.logEnteringFunction("onTouchEvent")

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
        LogUtils.logEnteringFunction("setUserView")

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

    /*private object InitialProcesses {

    }

    private object UIProcesses {
        fun clickJoin() {
            LogUtils.logEnteringFunction("clickJoin")

            doPermissionsCheckWriteExternalStorage()
            //doPermissionsCheckRead()
            //doPermissionsCheckWrite()

            HomeRoutineKt().joinJam(this, this, jam_pin_view)
            updatePinView()
        }
    }*/

    private fun registerUser() {
        LogUtils.logEnteringFunction("registerUser")

        APIutilsKt.UserFunctions.performRegisterUser(this, this)

        LogUtils.debug("Registered User", "Done")
        LogUtils.debug("New UUID", PrefUtilsKt.Functions().retrieveUUID(this))
    }

    private fun clickRec(view: View) {
        LogUtils.logEnteringFunction("clickRec")

        updatePinView()

        if (activeJamPIN.length > 4) {
            Snackbar.make(view, "Please create or join a jam", Snackbar.LENGTH_LONG).show()
        } else {
            startTime = Date()
            filename = FileUtils().generateAndSaveFilename(this, startTime)
            recorder = recorderUtils.setupRecorder(filename, findViewById(R.id.image_mic))

            //if (button_rec_new.text == "Rec") {
            //startTime = Date()
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

            rec.visibility = View.GONE
            stop.visibility = View.VISIBLE

            updatePinView()
            initializeRecordingService()
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun clickStop(context: Context, recorder: Recorder) {
        LogUtils.logEnteringFunction("clickStop")

        endTime = Date() // not TrueTime.now()

        updatePinView()

        initiateOfflineUploader()
        async { OfflineUploader().queueInteractor(this@TestNavActivity) }
        rec.visibility = View.VISIBLE
        stop.visibility = View.GONE

        HomeRoutineKt().clickStop(recorder, recorderUtils, context, chronometer_new)

        updatePinView()
        //audioRecordingService.stopForeground(Service.STOP_FOREGROUND_DETACH)
        //audioRecordingService.stopService(audioRecordingServiceIntent)
        stopService(audioRecordingServiceIntent)
    }

    private fun clickJoin() {
        LogUtils.logEnteringFunction("clickJoin")

        doPermissionsCheckWriteExternalStorage()
        //doPermissionsCheckRead()
        //doPermissionsCheckWrite()

        //HomeRoutineKt().joinJam(this, this, jam_pin_view, rec, stop, recorder, recorderUtils, chronometer_new)
        HomeRoutineKt().joinJam(this, this, jam_pin_view)
        updatePinView()
    }

    private fun clickNew() {
        LogUtils.logEnteringFunction("clickNew")

        doPermissionsCheckWriteExternalStorage()
        //doPermissionsCheckRead()
        //doPermissionsCheckWrite()

        HomeRoutineKt().createJam(this, jam_pin_view)
        updatePinView()

        /*APIutilsKt().performNewJam(this, PrefUtilsKt.Functions().retrieveUUID(this), "test_jam", "ActionSpot", 37, -22, "hi")
        LogUtils.debug("new pin", PrefUtilsKt.Functions().retrievePIN(this))
        jam_pin_view.text = PrefUtilsKt.Functions().retrievePIN(this)*/
        // maybe disable button until there exists a UUID
    }

    private fun clickTestAuth() {
        LogUtils.logEnteringFunction("clickTestAuth")

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
        LogUtils.logEnteringFunction("updatePinView")

        /*val pin = PrefUtilsKt.Functions().retrievePIN(this)
        val currentJamID = PrefUtilsKt.Functions().retrieveJamID(this)
        //val currentJamDetails = APIutilsKt.JamFunctions.getJamDetails(this, currentJamID)
        LogUtils.debug("UpdatePinView", "pin $pin, jamID $currentJamID")
        val newPIN = RealmUtils.JamViewModelUtils.Retrieve.retrieveJamPinWithID(currentJamID)
        LogUtils.debug("UpdatePinView", "newPIN $newPIN")*/ // TODO: actually do GetActiveJam

        //APIutilsKt.JamFunctions.performGetActiveJam(this) // add this back in later maybe
        //val activeJamID = PrefUtilsKt.Functions().retrieveJamID(this)
        activeJamPIN = PrefUtilsKt.Functions().retrievePIN(this)

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
        LogUtils.logEnteringFunction("onBackPressed")

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            realm.close()
        } else {
            realm.close()
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        LogUtils.logEnteringFunction("onCreateOptionsMenu")

        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.test_nav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        LogUtils.logEnteringFunction("onOptionsItemSelected")

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
        LogUtils.logEnteringFunction("onNavigationItemSelected")

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
        LogUtils.logEnteringFunction("onStart")

        locationUtils?.onStart(locationVars)
        super.onStart()
        /*if (jams.size == 0) {
            getJams()
        }*/
    }

    override fun onStop() {
        LogUtils.logEnteringFunction("onStop")

        locationUtils?.onStop(locationVars)
        //realm.close()
        super.onStop()
    }

    override fun onDestroy() {
        //if (this@TestNavActivity::audioRecordingServiceIntent.isInitialized) {
          //  stopService(audioRecordingServiceIntent)
        //}
        LogUtils.info("AudioRecordingService Status", "Stopped")
        super.onDestroy()
    }

    override fun onConnectionSuspended(p0: Int) {
        LogUtils.logEnteringFunction("onConnectionSuspended")

        locationUtils?.onConnectionSuspended(locationVars, p0)
    }

   override fun onConnectionFailed(p0: ConnectionResult) {
       LogUtils.logEnteringFunction("onConnectionFailed")

       locationUtils?.onConnectionFailed(p0)
    }

    override fun onLocationChanged(p0: Location?) {
        LogUtils.logEnteringFunction("onLocationChanged")

        locationUtils?.onLocationChanged(this, p0)
    }

    override fun onConnected(p0: Bundle?) {
        LogUtils.logEnteringFunction("onConnected")

        locationUtils?.onConnected(this, this, p0, locationVars)
    }

    private fun initializeLocationClient() {
        LogUtils.logEnteringFunction("initializeLocationClient")

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
