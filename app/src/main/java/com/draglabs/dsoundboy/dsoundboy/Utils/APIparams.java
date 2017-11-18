package com.draglabs.dsoundboy.dsoundboy.Utils;

import android.location.Location;

import com.loopj.android.http.RequestParams;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by davrukin on 11/17/17.
 */

class APIparams {

    static RequestParams authenticateUser(String facebookID,
                                          String facebookAccessToken) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("facebook_id", facebookID);
        requestParams.put("access_token", facebookAccessToken);

        return requestParams;
    }

    static RequestParams soloUpload(String uniqueID,
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

    static RequestParams startJam(String uniqueID,
                                  String jamLocation,
                                  String jamName,
                                  Location location) {

        double jamLatitude = location.getLatitude();
        double jamLongitude = location.getLongitude();

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_location", jamLocation);
        requestParams.put("jam_name", jamName);
        requestParams.put("jam_lat", jamLatitude);
        requestParams.put("jam_long", jamLongitude);

        return requestParams;
    }

    static RequestParams joinJam(String uniqueID,
                                 int pin) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("unique_id", uniqueID);
        requestParams.put("pin", pin);

        return requestParams;
    }

    static RequestParams jamRecordingUpload(String uniqueID,
                                            String jamID,
                                            String filename,
                                            String notes,
                                            Date startTime,
                                            Date endTime) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jamid", jamID);
        requestParams.put("fileName", filename);
        requestParams.put("notes", notes);
        requestParams.put("startTime", dateFormat.format(startTime));
        requestParams.put("endTime", dateFormat.format(endTime));

        return requestParams;
    }

    static RequestParams jamRecordingUpload(String uniqueID,
                                            String jamID,
                                            String filename,
                                            String notes,
                                            String startTime,
                                            String endTime) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jamid", jamID);
        requestParams.put("fileName", filename);
        requestParams.put("notes", notes);
        requestParams.put("startTime", startTime);
        requestParams.put("endTime", endTime);

        return requestParams;
    }

    static RequestParams exitJam(String uniqueID,
                                 String jamID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);

        return requestParams;
    }

    static RequestParams getCollaborators(String uniqueID,
                                          String jamID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);

        return requestParams;
    }

    static RequestParams getUserActivity(String uniqueID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);

        return requestParams;
    }

    static RequestParams getJamDetails() {
        return new RequestParams();
    }

    static RequestParams notifyUser(String jamID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("jam_id", jamID);

        return requestParams;
    }

    static RequestParams generateXML(String jamID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("jamid", jamID);

        return requestParams;
    }

    static RequestParams compress(String jamID,
                                  String userID) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("jam_id", jamID);
        requestParams.put("user_id", userID);

        return requestParams;
    }
}
