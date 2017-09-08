package com.draglabs.dsoundboy.dsoundboy;

import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HttpProcessor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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

        return sendPOST2(AUTHENTICATE_USER, json);

        //List<NameValuePair> urlParameters = new ArrayList<>();
    }

    public static int soloUpload(String uniqueID) {
        String newPOST = SOLO_UPLOAD_RECORDING + "/" + uniqueID;
        return 0;
    }

    public static int startJam(Location location) {
        String userID = Profile.getCurrentProfile().getId();
        String jamLocation;
        String jamName;
        double jamLatitude = location.getLatitude();
        double jamLongitude = location.getLongitude();

        int resultCode;
        String jamID;
        String jamPIN;
        String jamStartTime;
        String jamEndTime;

        String error;
        return 0;
    }

    public static int joinJam() {
        String userID = Profile.getCurrentProfile().getId();
        int pin;

        int resultCode;
        String jamStartTime;
        String jamEndTime;

        String error;
        return 0;
    }

    public static int jamRecordingUpload(String uniqueID, String jamID) {
        String newPOST = JAM_RECORDING_UPLOAD + "/" + uniqueID + "/jamid/" + jamID;
        return 0;
    }

    public static int exitJam() {
        String userID = Profile.getCurrentProfile().getId();
        String jamID;
        return 0;
    }

    public static int getCollaborators() {
        return 0;
    }

    public static int getUserActivity(String uniqueID) {
        String newPOST = GET_USER_ACTIVITY + "/" + uniqueID;
        return 0;
    }

    public static int notifyUser() {
        return 0;
    }

    private static void sendPOST(final String url, final String jsonData) {
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(url).openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setChunkedStreamingMode(0);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Accept", "application/json");

                    httpURLConnection.connect();

                    OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                    outputStream.write(jsonData.getBytes());
                    outputStream.flush();

                    System.out.println(httpURLConnection.getResponseCode());

                    //return httpURLConnection.getResponseMessage();

                    /*Writer writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
                    writer.write(jsonData);
                    writer.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    StringBuffer stringBuffer = new StringBuffer();
                    if (inputStream == null) {
                        // do nothing
                    }
                    JsonResponse*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private static String sendPOST2(final String url, final String jsonData) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(url).openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setChunkedStreamingMode(0);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            httpURLConnection.connect();

            OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            outputStream.write(jsonData.getBytes());
            outputStream.flush();

            System.out.println(httpURLConnection.getResponseCode());

            return httpURLConnection.getResponseMessage();

            /*Writer writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            writer.write(jsonData);
            writer.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) {
                // do nothing
            }
            JsonResponse*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    private static Location getCurrentLocation() {
        //LocationManager locationManager = (LocationManager)getSystem
        return null;
    }
}
