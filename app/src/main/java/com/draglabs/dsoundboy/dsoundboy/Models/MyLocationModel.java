package com.draglabs.dsoundboy.dsoundboy.Models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * <p>Holds a location for a recording</p>
 * <p>Created by davrukin on 11/3/17.</p>
 */

public class MyLocationModel {

    private double latitude;
    private double longitude;
    private String address;
    private Context context;

    /**
     * Default no-arg constructor for MyLocationModel, all fields set to ""
     */
    public MyLocationModel(Context context) {
        this.context = context;
        this.latitude = 0;
        this.longitude = 0;
        this.address = getStreetAddress(latitude, longitude);
    }

    /**
     * Constructor for MyLocationModel
     * @param latitude the latitude of the recording
     * @param longitude the longitude of the recording
     */
    public MyLocationModel(Context context, double latitude, double longitude) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = getStreetAddress(latitude, longitude);
    }

    /**
     * Gets the street address from a latitude and a longitude
     * @param latitude the latitude of the recording
     * @param longitude the longitude of the recording
     * @return if either is 0, address is ""; the address if it works; "No Address Available" if doesn't work
     */
    private String getStreetAddress(double latitude, double longitude) {
        // https://stackoverflow.com/a/9409229
        if (latitude == 0 || longitude == 0) {
            return "";
        } else {
            try {
                long startTime = System.currentTimeMillis();
                Geocoder geocoder = new Geocoder(context, Locale.getDefault()); // lookup may take a long time? test
                List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
                long endTime = System.currentTimeMillis();
                Log.d("Geocoder Lookup Time: ", (endTime - startTime) + " ms");
                return address.get(0).getAddressLine(0);
                // improve with https://developer.android.com/training/location/display-address.html if doesn't work
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "No Address Available";
    }

    /**
     * Gets the latitude of the recording
     * @return the latitude of the recording
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the recording
     * @param latitude the latitude of the recording
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of the recording
     * @return the longitude of the recording
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the recording
     * @param longitude the latitude of the recording
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the address of the recording
     * @return the address of the recording
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the recording
     * @param address the address of the recording
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
