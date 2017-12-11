package com.draglabs.dsoundboy.dsoundboy.Params;

import android.location.Location;

import com.draglabs.dsoundboy.dsoundboy.Models.StringsModel;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <p>Created by davrukin on 11/17/17.</p>
 * <p>Holds and returns parameters for all the API functions.</p>
 */

public class APIparams {

    /**
     * Used for the Start Jam API
     * @param uniqueID the API's unique user ID
     * @param jamLocation the physical location of the user, latitude and longitude extracted
     * @param jamName the name of the jam
     * @param location the name of the location of the jam
     * @param notes the jam notes
     * @return requestParams
     */
    public static RequestParams newJam(String uniqueID,
                                       String jamLocation,
                                       String jamName,
                                       Location location,
                                       String notes) {

        double jamLatitude = location.getLatitude();
        double jamLongitude = location.getLongitude();

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_location", jamLocation);
        requestParams.put("jam_name", jamName);
        requestParams.put("jam_lat", jamLatitude);
        requestParams.put("jam_long", jamLongitude);
        requestParams.put("notes", notes);

        return requestParams;
    }

    /**
     * Used for the Update Jam API
     * @param name the name of the jam
     * @param jamID the jam ID
     * @param location the place name where the jam was recorded
     * @param notes the jam notes
     * @return requestParams
     */
    public static RequestParams updateJam(String name,
                                          String jamID,
                                          String location,
                                          String notes) {

        RequestParams requestParams = new RequestParams();

        requestParams.put("name", name);
        requestParams.put("id", jamID);
        requestParams.put("location", location);
        requestParams.put("notes", notes);

        return requestParams;
    }

    /**
     * Used for the Join Jam API
     * @param UUID the dlsAPI unique user ID
     * @param pin the jam PIN
     * @return requestParams
     */
    public static RequestParams joinJam(String UUID,
                                        int pin) {

        RequestParams requestParams = new RequestParams();

        requestParams.put("user_id", UUID);
        requestParams.put("pin", pin);

        return requestParams;
    }

    /**
     * Used for the Jam Recording Upload API
     * @param uniqueID the dlsAPI UUID
     * @param jamID the jam ID
     * @param filename the name of the recorded file
     * @param location the location name of the jam
     * @param startTime the start time of the jam
     * @param endTime the end time of the jam
     * @return requestParams
     */
    public static RequestParams jamRecordingUpload(String uniqueID,
                                                   String jamID,
                                                   String filename,
                                                   String location,
                                                   Date startTime,
                                                   Date endTime) {

        RequestParams requestParams = new RequestParams();

        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);
        requestParams.put("file_name", filename);
        requestParams.put("location", location);
        requestParams.put("start_time", StringsModel.STANDARD_DATE_FORMAT.format(startTime));
        requestParams.put("end_time", StringsModel.STANDARD_DATE_FORMAT.format(endTime));

        return requestParams;
    }

    /**
     * Used for the Jam Recording Upload API
     * @param uniqueID the dlsAPI UUID
     * @param jamID the jam ID
     * @param path the path to the file
     * @param location the location name of the jam
     * @param startTime the start time of the jam
     * @param endTime the end time of the jam
     * @return requestParams
     */
    public static RequestParams jamRecordingUpload(String uniqueID,
                                                   String jamID,
                                                   String path,
                                                   String location,
                                                   String startTime,
                                                   String endTime) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);
        requestParams.put("location", location);
        requestParams.put("start_time", startTime);
        requestParams.put("end_time", endTime);
        try {
            requestParams.put("file_name", new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return requestParams;
    }

    /**
     * Used for the Get Jam Details API
     * @return empty requestParams
     */
    public static RequestParams getJamDetails() {
        return new RequestParams();
    }

    /**
     * Used for the Get Recordings API
     * @return empty requestParams
     */
    public static RequestParams getRecordings() {
        return new RequestParams();
    }

    /**
     * Used for the Authenticate User API with the Jam API
     * @param facebookID the user's Facebook ID
     * @param facebookAccessToken the Access Token from the Facebook API
     * @return requestParams
     */
    public static RequestParams registerUser(String facebookID,
                                             String facebookAccessToken) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("facebook_id", facebookID);
        requestParams.put("access_token", facebookAccessToken);

        return requestParams;
    }

    /**
     * Used for the Update User API
     * @return empty requestParams
     */
    public static RequestParams updateUser() {
        return new RequestParams();
    }

    /**
     * Used for the Get Active Jam API
     * @return empty requestParams
     */
    public static RequestParams getActiveJam() {
        return new RequestParams();
    }

    /**
     * Used for the Get User Activity API
     * @return empty requestParams
     */
    public static RequestParams getUserActivity() {
        return new RequestParams();
    }

    /**
     * Used for the Solo Recording Upload API
     * @param uniqueID the API's unique user ID
     * @param filename the name of the recorded file
     * @param notes notes from the recording
     * @param startTime the start time of the recording
     * @param endTime the end time of the recording
     * @return requestParams
     */
    public static RequestParams soloUpload(String uniqueID,
                                    String filename,
                                    String notes,
                                    Date startTime,
                                    Date endTime) {

        // DATE FORMAT: 2017-07-27T23:48:48
        //              yyyy-MM-dd'T'HH:mm:ss
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US); // TODO: SAVE/ACCESS START/END TIMES
        // TODO: Date ___time = Calendar.getInstance().getTime();

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("fileName", filename);
        requestParams.put("notes", notes);
        requestParams.put("start_time", dateFormat.format(startTime));
        requestParams.put("end_time", dateFormat.format(endTime));

        return requestParams;
    }

    /**
     * Used for the Exit Jam API
     * @param uniqueID the API's unique user ID
     * @param jamID the jam ID
     * @return requestParams
     */
    public static RequestParams exitJam(String uniqueID,
                                 String jamID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);

        return requestParams;
    }

    /**
     * Used for the Get Collaborators API
     * @param uniqueID the API's unique user ID
     * @param jamID the jam ID
     * @return requestParams
     */
    public static RequestParams getCollaborators(String uniqueID,
                                          String jamID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);

        return requestParams;
    }

    /**
     * Used for the Notify User API
     * @param jamID the jam ID
     * @return requestParams
     */
    public static RequestParams notifyUser(String jamID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("jam_id", jamID);

        return requestParams;
    }

    /**
     * Used for the Generate XML API
     * @param jamID the jam ID
     * @return requestParams
     */
    public static RequestParams generateXML(String jamID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("jamid", jamID);

        return requestParams;
    }

    /**
     * Used for the Compressor API
     * @param jamID the jam ID
     * @param userID the API's unique user ID
     * @return requestParams
     */
    public static RequestParams compress(String jamID,
                                         String userID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("jam_id", jamID);
        requestParams.put("user_id", userID);

        return requestParams;
    }

    /**
     * Creates the request parameters for a file upload
     * @param filename the file name
     * @param path the local file path
     * @return the request parameters
     */
    public static RequestParams createRequestParamsForUpload(String filename,
                                                             String path) {
        File file = new File(path);
        RequestParams requestParams = new RequestParams();
        try {
            requestParams.put(filename, file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return requestParams;
    }
}
