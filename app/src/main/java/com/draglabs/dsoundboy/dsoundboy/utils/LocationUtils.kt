/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.draglabs.dsoundboy.dsoundboy.activities.TestNavActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

/**
 * Created by davrukin on 1/5/2018.
 * @author Daniel Avrukin
 */

class LocationUtils {

    fun onStart(locationVars: TestNavActivity.LocationVars) {
        if (locationVars.googleApiClient != null) {
            locationVars.googleApiClient?.connect()
        }
    }

    fun onStop(locationVars: TestNavActivity.LocationVars) {
        if (locationVars.googleApiClient!!.isConnected) {
            locationVars.googleApiClient?.disconnect()
        }
    }

    fun onConnectionSuspended(locationVars: TestNavActivity.LocationVars, p0: Int) {
        Log.i("Location Suspension", "Location Suspended, p0: " + p0)
        locationVars.googleApiClient?.connect()
    }

    fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i("Location Conn Fail", "Connection Failed. Error: " + connectionResult.errorCode)
    }

    fun onLocationChanged(context: Context, location: Location?) {
        val msg = "Updated Location: Latitude: " + location?.longitude.toString() + location?.longitude
        val lat = location?.latitude
        val lng = location?.longitude
        PrefUtilsKt.Functions().storeLatitude(context, lat.toString())
        PrefUtilsKt.Functions().storeLongitude(context, lng.toString())
        LogUtils.debug("Location Changed", msg)
    }

    fun onConnected(activity: Activity, context: Context, bundle: Bundle?, locationVars: TestNavActivity.LocationVars) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        startLocationUpdates(context, locationVars)

        val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(bundle as Activity)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(bundle as Activity, { location: Location ->
            // got last known location, in some rare situations this can be null
            if (location != null) {
                locationVars.location = location
                PrefUtilsKt.Functions().storeLatitude(context, location.latitude.toString())
                PrefUtilsKt.Functions().storeLongitude(context, location.longitude.toString())
            }
        })
    }

    private fun startLocationUpdates(context: Context, locationVars: TestNavActivity.LocationVars) {

        // Create the location request
        locationVars.locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(locationVars.updateInterval)
                .setFastestInterval(locationVars.fastestInterval)
        // Request location updates
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        //LocationServices.FusedLocationApi.requestLocationUpdates(locationVars.googleApiClient, locationVars.locationRequest, locationVars.listener)
    }

    fun initializeLocationClient(callbacks: GoogleApiClient.ConnectionCallbacks, listener: GoogleApiClient.OnConnectionFailedListener, context: Context, locationVars: TestNavActivity.LocationVars) {
        locationVars.googleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(callbacks)
                .addOnConnectionFailedListener(listener)
                .addApi(LocationServices.API)
                .build()
        locationVars.locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkLocation(context, locationVars)
    }

    private fun checkLocation(context: Context, locationVars: TestNavActivity.LocationVars): Boolean {
        val locationEnabled = isLocationEnabled(context, locationVars)
        if (!locationEnabled) {
            showAlert(context)
        }
        return locationEnabled
    }

    private fun isLocationEnabled(context: Context, locationVars: TestNavActivity.LocationVars): Boolean {
        locationVars.locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationVars.locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationVars.locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showAlert(context: Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
                .setPositiveButton("Location Settings", { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                })
                .setNegativeButton("Cancel", { _, _ -> })
        dialog.show()
    }

}
