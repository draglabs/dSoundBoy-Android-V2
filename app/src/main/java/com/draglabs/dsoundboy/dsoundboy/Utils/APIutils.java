package com.draglabs.dsoundboy.dsoundboy.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.draglabs.dsoundboy.dsoundboy.Accessories.Email;
import com.draglabs.dsoundboy.dsoundboy.Accessories.Strings;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.ParseException;

/**
 * Created by davrukin on 8/14/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class APIutils {

    private static final String END_POINT = "http://api.draglabs.com";
    private static final String API_VERSION = "/v1.01";

    private static final String POST_EMAIL_URL = "";
    private static final String POST_MUSIC_CONSOLIDATOR_URL = "";

    private static final String AUTHENTICATE_USER = END_POINT + API_VERSION +  "/user/auth";
    private static final String SOLO_UPLOAD_RECORDING = END_POINT + API_VERSION + "/soloupload/id";
    private static final String START_JAM = END_POINT + API_VERSION + "/jam/start";
    private static final String JOIN_JAM = END_POINT + API_VERSION + "/jam/join";
    private static final String JAM_RECORDING_UPLOAD = END_POINT + API_VERSION + "/jam/upload/userid";
    private static final String EXIT_JAM = END_POINT + API_VERSION + "/jam/exit";
    private static final String GET_COLLABORATORS = END_POINT + API_VERSION + "/jam/collaborators";
    private static final String GET_USER_ACTIVITY = END_POINT + API_VERSION + "/user/activity/id";
    private static final String NOTIFY_USER = END_POINT + API_VERSION + "/jam/notifyuser";
    private static final String GET_JAM_DETIALS = END_POINT + API_VERSION + "/user/jam-details?jamId=";
    private static final String GENERATE_XML = "http://54.153.93.94" + "/v1/generateXML/";
    private static final String COMPRESS = END_POINT + API_VERSION + "/jam/archive";

    private static final AsyncHttpClient asyncHttpClient = new AsyncHttpClient(true, 80, 433);


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

    public static void authenticateUser(Activity activity) {
        String facebookID = Profile.getCurrentProfile().getId();
        Log.d("Facebook ID:", facebookID);
        String facebookAccessToken = AccessToken.getCurrentAccessToken().getToken();
        Log.d("Auth Token:", facebookAccessToken);

        final PrefUtils prefUtils = new PrefUtils(activity);
        prefUtils.saveAccessToken(facebookAccessToken);

        RequestParams requestParams = new RequestParams();
        requestParams.put("facebook_id", facebookID);
        requestParams.put("access_token", facebookAccessToken);

        //JsonHttpHandler jsonHttpHandler = new JsonHttpHandler();
        //JSONObject response = jsonHttpHandler.getResponse();
        post(AUTHENTICATE_USER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String uniqueID = FileUtils.getJsonObject(Strings.AUTHENTICATE_USER, response, Strings.jsonTypes.UNIQUE_ID.type());
                    Log.v("Unique ID: ", uniqueID);
                    prefUtils.saveUniqueUserID(uniqueID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void soloUpload(String uniqueID, String filename, String path, String notes, Date startTime, Date endTime) {
        // DATE FORMAT: 2017-07-27T23:48:48
        //              yyyy-MM-dd'T'HH:mm:ss
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US); // TODO: SAVE/ACCESS START/END TIMES
        // TODO: Date ___time = Calendar.getInstance().getTime();

        String newPOST = SOLO_UPLOAD_RECORDING + "/" + uniqueID; // TODO: SAVE/ACCESS UNIQUE ID

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("fileName", filename);
        requestParams.put("notes", notes);
        requestParams.put("start_time", dateFormat.format(startTime));
        requestParams.put("end_time", dateFormat.format(endTime));
        upload("audioFile", path, newPOST, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String message = FileUtils.getJsonObject(Strings.SOLO_UPLOAD_RECORDING, response, Strings.jsonTypes.MESSAGE.type());
                    Log.v("Response Message: ", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void startJam(Activity activity, String uniqueID, String jamLocation, String jamName, Location location) {
        double jamLatitude = location.getLatitude();
        double jamLongitude = location.getLongitude();

        final PrefUtils prefUtils = new PrefUtils(activity);

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_location", jamLocation);
        requestParams.put("jam_name", jamName);
        requestParams.put("jam_lat", jamLatitude);
        requestParams.put("jam_long", jamLongitude);

        post(START_JAM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String jamPIN = FileUtils.getJsonObject(Strings.START_JAM, response, Strings.jsonTypes.PIN.type());
                    String jamID = FileUtils.getJsonObject(Strings.START_JAM, response, Strings.jsonTypes.JAM_ID.type());
                    String jamStartTime = FileUtils.getJsonObject(Strings.START_JAM, response, Strings.jsonTypes.START_TIME.type());
                    String jamEndTime = FileUtils.getJsonObject(Strings.START_JAM, response, Strings.jsonTypes.END_TIME.type());
                    Log.v("Jam PIN: ", jamPIN + "");
                    Log.v("Jam ID: ", jamID + "");
                    Log.v("Jam Start Time: ", jamStartTime + "");
                    Log.v("Jam End Time: ", jamEndTime + "");
                    prefUtils.saveJamPIN(jamPIN);
                    prefUtils.saveJamID(jamID);
                    prefUtils.saveJamStartTime(jamStartTime);
                    prefUtils.saveJamEndTime(jamEndTime);
                    createDialog(activity, "", "Jam PIN", jamPIN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void joinJam(Activity activity, String uniqueID, int pin) {
        final PrefUtils prefUtils = new PrefUtils(activity);

        RequestParams requestParams = new RequestParams();
        requestParams.put("unique_id", uniqueID);
        requestParams.put("pin", pin);

        post(JOIN_JAM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String jamPIN = FileUtils.getJsonObject(Strings.JOIN_JAM, response, Strings.jsonTypes.PIN.type());
                    String jamID = FileUtils.getJsonObject(Strings.JOIN_JAM, response, Strings.jsonTypes.JAM_ID.type());
                    String jamStartTime = FileUtils.getJsonObject(Strings.JOIN_JAM, response, Strings.jsonTypes.START_TIME.type());
                    String jamEndTime = FileUtils.getJsonObject(Strings.JOIN_JAM, response, Strings.jsonTypes.END_TIME.type());
                    Log.v("Jam PIN: ", jamPIN + "");
                    Log.v("Jam ID: ", jamID + "");
                    Log.v("Jam Start Time: ", jamStartTime + "");
                    Log.v("Jam End Time: ", jamEndTime + "");
                    prefUtils.saveJamPIN(jamPIN);
                    prefUtils.saveJamID(jamID);
                    prefUtils.saveJamStartTime(jamStartTime);
                    prefUtils.saveJamEndTime(jamEndTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void jamRecordingUpload(String uniqueID, String jamID, String filename, String path, String notes, Date startTime, Date endTime, View view) { // convert through binary data and multi-part upload
        String newPOST = JAM_RECORDING_UPLOAD + "/" + uniqueID + "/jamid/" + jamID;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US); // TODO: SAVE/ACCESS START/END TIMES

        Log.d("newPOST: ", newPOST);

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jamid", jamID); // TODO: do something when there is error, not just give dummy response **!!**!!
        requestParams.put("fileName", filename);
        requestParams.put("notes", notes);
        requestParams.put("startTime", dateFormat.format(startTime));
        requestParams.put("endTime", dateFormat.format(endTime));
        //String string = uniqueID + "\n" + jamID + "\n" + filename + "\n" + notes + "\n" + startTime + "\n" + endTime;
        Snackbar.make(view, uniqueID, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, jamID, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, filename, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, notes, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, startTime.toString(), Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, endTime.toString(), Snackbar.LENGTH_LONG).show();

        upload("audioFile", path, newPOST, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String message = FileUtils.getJsonObject(Strings.JAM_RECORDING_UPLOAD, response, Strings.jsonTypes.MESSAGE.type());
                    Log.v("Response Message: ", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Snackbar.make(view, "Recording uploaded.", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void jamRecordingUpload(String uniqueID, String jamID, String filename, String path, String notes, String startTime, String endTime, View view) { // convert through binary data and multi-part upload
        String newPOST = JAM_RECORDING_UPLOAD + "/" + uniqueID + "/jamid/" + jamID;

        Log.d("newPOST: ", newPOST);

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jamid", jamID); // TODO: do something when there is error, not just give dummy response **!!**!!
        requestParams.put("fileName", filename);
        requestParams.put("notes", notes);
        requestParams.put("startTime", startTime);
        requestParams.put("endTime", endTime);
        //String string = uniqueID + "\n" + jamID + "\n" + filename + "\n" + notes + "\n" + startTime + "\n" + endTime;
        Snackbar.make(view, uniqueID, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, jamID, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, filename, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, notes, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, startTime.toString(), Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, endTime.toString(), Snackbar.LENGTH_LONG).show();

        upload("audioFile", path, newPOST, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String message = FileUtils.getJsonObject(Strings.JAM_RECORDING_UPLOAD, response, Strings.jsonTypes.MESSAGE.type());
                    Log.v("Response Message: ", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Snackbar.make(view, "Recording uploaded.", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void exitJam(String uniqueID, String jamID) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);

        post(EXIT_JAM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String message = FileUtils.getJsonObject(Strings.EXIT_JAM, response, Strings.jsonTypes.MESSAGE.type());
                    Log.v("Response Message: ", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void getCollaborators(Activity activity, String uniqueID, String jamID) {
        final PrefUtils prefUtils = new PrefUtils(activity);

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        requestParams.put("jam_id", jamID);

        get(GET_COLLABORATORS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String collaborators = FileUtils.getJsonObject(Strings.GET_COLLABORATORS, response, Strings.jsonTypes.MESSAGE.type());
                    Log.v("Collaborators: ", collaborators);
                    prefUtils.saveCollaborators(collaborators);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void getUserActivity(Activity activity, String uniqueID, Context context) {
        //String newPOST = GET_USER_ACTIVITY + "/" + uniqueID;
        String newPOST = GET_USER_ACTIVITY + "/";
        // TODO: add date as part of the request header

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", uniqueID);
        //requestParams.put("jam_id", jamID);
        Header[] headers = new Header[]{new Header() {
            @Override
            public String getName() {
                return "user_id";
            }

            @Override
            public String getValue() {
                return uniqueID;
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        }};
        get(context, newPOST, headers, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    /*if (type.equals(Strings.jsonTypes.RECORDINGS.type())) {
                        String recordings = FileUtils.getJsonObject(Strings.GET_USER_ACTIVITY, response, type);
                        Log.v("Recordings: ", recordings);
                        prefUtils.saveUserActivity(recordings);
                    } else if (type.equals(Strings.jsonTypes.JAMS.type())){
                        String jams = FileUtils.getJsonObject(Strings.GET_USER_ACTIVITY, response, type);
                        Log.v("Jams: ", jams);
                        prefUtils.saveUserActivity(jams);
                    }*/
                    String jams = FileUtils.getJsonObject(Strings.GET_USER_ACTIVITY, response, Strings.jsonTypes.JAMS.type());
                    Log.v("Jams: ", jams);
                    new PrefUtils(activity).saveUserActivity(jams);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void getJamDetails(String userID, String jamID, Activity activity, Context context) {
        final PrefUtils prefUtils = new PrefUtils(activity);

        RequestParams requestParams = new RequestParams();
        Header[] headers = new Header[]{new Header() {
            @Override
            public String getName() {
                return "user_id";
            }

            @Override
            public String getValue() {
                return userID;
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        }, new Header() {
            @Override
            public String getName() {
                return "jam_id";
            }

            @Override
            public String getValue() {
                return jamID;
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        }};

        get(context, GET_JAM_DETIALS, headers, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String jamDetails = FileUtils.getJsonObject(Strings.GET_JAM_DETAILS, response, Strings.jsonTypes.DATA.type());
                    Log.v("Jam Details: ", jamDetails);
                    new PrefUtils(activity).saveJamDetails(jamDetails); // TODO: enable later
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    public static void notifyUser(String jamID, Context context) {
        RequestParams requestParams = new RequestParams("jam_id", jamID);

        post(NOTIFY_USER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Function: ", "Notify User");
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                try {
                    String message = FileUtils.getJsonObject(Strings.NOTIFY_USER, response, Strings.jsonTypes.MESSAGE.type());
                    Log.v("Response Message: ", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                createDialog(context, jamID, "Notify User", "User Notified");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
                createNotifyUserErrorDialog(jamID, context);
            }
        });
    }

    public static void generateXML(String jamID, JSONObject jamDetails, Context context) {
        RequestParams requestParams = new RequestParams("jamid", jamID);

        post(GENERATE_XML + jamID, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               Log.v("Function: ", "Generate XML");
               Log.v("Status Code: ", statusCode + "");
               Log.v("Headers: ", Arrays.toString(headers));
               Log.v("Response: ", response.toString());
               createDialog(context, jamID, "Generate XML", response.toString());
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                   Log.v("Status Code: ", statusCode + "\n");
                   Log.v("Headers: ", Arrays.toString(headers) + "");
                   Log.v("Throwable: ", throwable.getMessage());
                   Log.v("Response: ", response.toString());
               } else {
                   Log.v("Reason: ", "Other Failure.");
               }
               createDialog(context, jamID, "Failure", response.toString());
           }
        });
    }

    public static void compress(String jamID, String userID, JSONObject jamDetails, Context context) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("jam_id", jamID);
        requestParams.put("user_id", userID);

        post(COMPRESS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Function: ", "Compress");
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("Response: ", response.toString());
                createDialog(context, jamID, "Compress", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("Response: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
                createDialog(context, jamID, "Failure", response.toString());
            }
        });
    }

    private static void createNotifyUserErrorDialog(String jamID, Context context) { // to notify user with email
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Please check it").setTitle("Invalid Jam ID");
        builder.setNegativeButton("Okay", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void createDialog(Context context, String jamID, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title);
        builder.setNeutralButton("Okay", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static String post(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.post(url, requestParams, asyncHttpResponseHandler);
        return "Done with POST.";
    }

    private static String get(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.get(url, requestParams, asyncHttpResponseHandler);

        return "Done with GET.";
    }

    private static String get(Context context, String url, Header[] headers, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.get(context, url, headers, requestParams, asyncHttpResponseHandler);

        return "Done with GET.";
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

        asyncHttpClient.post(url, requestParams, asyncHttpResponseHandler); // too many prints in the logger
    }
}
