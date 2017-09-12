package com.draglabs.dsoundboy.dsoundboy;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.viralypatel.sharedpreferenceshelper.lib.SharedPreferencesHelper;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HttpProcessor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import retrofit2.Retrofit;

/**
 * Created by davrukin on 8/14/17.
 */

public class APIutils {

    private static final String END_POINT = "http://api.draglabs.com";
    private static final String API_VERSION = "/v1.01";

    private static final String POST_EMAIL_URL = "";
    private static final String POST_MUSIC_CONSOLIDATOR_URL = "";

    private static final String AUTHENTICATE_USER = END_POINT + API_VERSION +  "/user/auth";
    private static final String SOLO_UPLOAD_RECORDING = END_POINT + API_VERSION + "/soloupload/id";
    private static final String START_JAM = END_POINT + API_VERSION + "/jam/start";
    private static final String JOIN_JAM = END_POINT + API_VERSION + "/jam/join";
    private static final String JAM_RECORDING_UPLOAD = END_POINT + API_VERSION + "/jam/upload/uniqueid";
    private static final String EXIT_JAM = END_POINT + API_VERSION + "/jam/exit";
    private static final String GET_COLLABORATORS = END_POINT + API_VERSION + "/jam/collaborators";
    private static final String GET_USER_ACTIVITY = END_POINT + API_VERSION + "/user/activity/id";
    private static final String NOTIFY_USER = END_POINT + API_VERSION + "/jam/notifyuser";

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient(true, 80, 433);
    private static JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.v("Status Code: ", statusCode + "");
            Log.v("Headers: ", headers.toString());
            Log.v("Response: ", response.toString());

            // TODO: Somehow save or pass information, maybe put back into each method and return it?
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.v("Status Code: ", statusCode + "");
            Log.v("Headers: ", headers.toString());
            Log.v("Response: ", response.toString());
                /*try {
                    System.out.println(response.getJSONObject(0).getString("user_id"));
                    System.out.println(response.getJSONObject(1).getString("first_name"));
                    System.out.println(response.getJSONObject(2).getString("last_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
            if (headers != null && throwable != null && response != null) {
                Log.v("Status Code: ", statusCode + "\n");
                Log.v("Headers: ", headers + "");
                Log.v("Throwable: ", throwable.getMessage());
                Log.v("Response: ", response.toString());
            } else {
                Log.v("Reason: ", "Other Failure.");
            }
        }
    };


    public static int sendEmail(String email, String subject, String message) {
        try {
            Gson gson = new Gson();
            URL url = new URL(POST_EMAIL_URL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

            outputStreamWriter.write(gson.toJson(new Email(email, subject, message)));
            outputStreamWriter.flush();
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            outputStreamWriter.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    public static int uploadToS3(String pathname) {
        return 0;
    }

    public static String authenticateUser() {
        JSONObject jsonObject = new JSONObject();

        String facebookID = Profile.getCurrentProfile().getId();
        String facebookAccessToken = AccessToken.getCurrentAccessToken().getToken();

        try {
            jsonObject.put("facebook_id", facebookID);
            jsonObject.put("access_token", facebookAccessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = gson.toJson(jsonObject);

        RequestParams requestParams = new RequestParams();
        requestParams.put("facebook_id", facebookID);
        requestParams.put("access_token", facebookAccessToken);

        post(AUTHENTICATE_USER, requestParams, jsonHttpResponseHandler);

        //return sendPOST2(AUTHENTICATE_USER, json);

        //List<NameValuePair> urlParameters = new ArrayList<>();
        return null;
    }

    public static int soloUpload(String uniqueID, String filename, String path, String notes, Date startTime, Date endTime) {
        // DATE FORMAT: 2017-07-27T23:48:48
        //              yyyy-MM-dd'T'HH:mm:ss
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // TODO: SAVE/ACCESS START/END TIMES
        // TODO: Date ___time = Calendar.getInstance().getTime();

        String newPOST = SOLO_UPLOAD_RECORDING + "/" + uniqueID; // TODO: SAVE/ACCESS UNIQUE ID
        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("fileName", filename);
        requestParams.put("notes", notes);
        requestParams.put("start_time", dateFormat.format(startTime));
        requestParams.put("end_time", dateFormat.format(endTime));
        upload("audioFile", path, newPOST, requestParams, jsonHttpResponseHandler);
        return 0; // TODO: return the server response code
    }

    public static int startJam(String uniqueID, String jamLocation, String jamName, Location location) {
        double jamLatitude = location.getLatitude();
        double jamLongitude = location.getLongitude();

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_location", jamLocation);
        requestParams.put("jam_name", jamName);
        requestParams.put("jam_lat", jamLatitude);
        requestParams.put("jam_long", jamLongitude);

        post(START_JAM, requestParams, jsonHttpResponseHandler);

        int resultCode;
        String jamID;
        String jamPIN;
        String jamStartTime;
        String jamEndTime;

        String error; // TODO: get response to get JamID

        return 0;
    }

    public static int joinJam(String uniqueID, int pin) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("unique_id", uniqueID);
        requestParams.put("pin", pin);

        post(JOIN_JAM, requestParams, jsonHttpResponseHandler);

        int resultCode;
        String jamStartTime;
        String jamEndTime;

        String error;
        return 0;
    }

    public static int jamRecordingUpload(String uniqueID, String jamID, String filename, String path, String notes, Date startTime, Date endTime) { // convert through binary data and multi-part upload
        String newPOST = JAM_RECORDING_UPLOAD + "/" + uniqueID + "/jamid/" + jamID;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // TODO: SAVE/ACCESS START/END TIMES

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jamid", jamID);
        requestParams.put("fileName", filename);
        requestParams.put("notes", notes);
        requestParams.put("startTime", dateFormat.format(startTime));
        requestParams.put("endTime", dateFormat.format(endTime));

        upload("audioFile", path, newPOST, requestParams, jsonHttpResponseHandler);
        return 0;
    }

    public static int exitJam(String uniqueID, String jamID) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);

        post(EXIT_JAM, requestParams, jsonHttpResponseHandler);
        return 0;
    }

    public static int getCollaborators(String uniqueID, String jamID) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);

        post(GET_COLLABORATORS, requestParams, jsonHttpResponseHandler);
        return 0;
    }

    public static int getUserActivity(String uniqueID, String jamID) {
        String newPOST = GET_USER_ACTIVITY + "/" + uniqueID;
        // TODO: add date as part of the request header
        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);

        post(GET_USER_ACTIVITY, requestParams, jsonHttpResponseHandler);
        return 0;
    }

    public static int notifyUser(String jamID) {
        RequestParams requestParams = new RequestParams("jam_id", jamID);

        post(NOTIFY_USER, requestParams, jsonHttpResponseHandler);
        return 0;
    }

    private static String post(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.post(url, requestParams, asyncHttpResponseHandler);

        return "Done.";
    }

    private static void get(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.get(url, requestParams, asyncHttpResponseHandler);
    }

    private static RequestParams createRequestParamsForUpload(String filename, String path) {
        File file = new File(path);
        RequestParams requestParams = new RequestParams();
        try {
            requestParams.put(filename, file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return requestParams;
    }

    private static void upload(String filename, String path, String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.post(url, createRequestParamsForUpload(filename, path), asyncHttpResponseHandler);
    }

    private static void upload(String filename, String path, String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        File file = new File(path);
        try {
            requestParams.put(filename, file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        asyncHttpClient.post(url, requestParams, asyncHttpResponseHandler);
    }

    private static Location getCurrentLocation() {
        //LocationManager locationManager = (LocationManager)getSystem
        return null;
    }
}
