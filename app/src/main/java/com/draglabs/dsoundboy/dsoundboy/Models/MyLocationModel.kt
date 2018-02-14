package com.draglabs.dsoundboy.dsoundboy.Models

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils

import java.io.IOException
import java.util.Locale

/**
 *
 * Holds a location for a recording
 *
 * Created by davrukin on 11/3/17.
 * @author Daniel Avrukin
 */

class MyLocationModel {

    /**
     * Gets the street address from a latitude and a longitude
     * @param latitude the latitude of the recording
     * @param longitude the longitude of the recording
     * @return if either is 0, address is ""; the address if it works; "No Address Available" if doesn't work
     */
    private fun getStreetAddress(context: Context, latitude: Double, longitude: Double): String {
        // https://stackoverflow.com/a/9409229
        if (latitude == 0.0 || longitude == 0.0) {
            return ""
        } else {
            try {
                val startTime = System.currentTimeMillis()
                val geocoder = Geocoder(context, Locale.getDefault()) // lookup may take a long time? test
                val address = geocoder.getFromLocation(latitude, longitude, 1)
                val endTime = System.currentTimeMillis()
                LogUtils.debug("Geocoder Lookup Time: ", (endTime - startTime).toString() + " ms")
                return address[0].getAddressLine(0)
                // improve with https://developer.android.com/training/location/display-address.html if doesn't work
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return "No Address Available"
    }
}
