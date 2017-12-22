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
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.LoginRoutine
import com.facebook.AccessToken
import com.facebook.GraphRequest
import kotlinx.android.synthetic.main.activity_test_nav.*
import kotlinx.android.synthetic.main.app_bar_test_nav.*

class TestNavActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_nav)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        setUserView()
    }

    private fun setUserView() {
        var loginRoutine = LoginRoutine(null, this, this)

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
            R.id.nav_gallery -> {
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
}
