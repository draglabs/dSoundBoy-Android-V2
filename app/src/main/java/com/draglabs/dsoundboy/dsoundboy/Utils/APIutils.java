package com.draglabs.dsoundboy.dsoundboy.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.draglabs.dsoundboy.dsoundboy.Models.EmailModel;
import com.draglabs.dsoundboy.dsoundboy.Models.StringsModel;
import com.draglabs.dsoundboy.dsoundboy.Params.APIparams;
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
import java.util.Arrays;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.ParseException;

/**
 * <p>Holds all dlsAPI calls</p>
 * <p>Created by davrukin on 8/14/17.</p>
 */
@SuppressWarnings("DefaultFileTemplate")
public class APIutils { // TODO: docs for all other utils

    private static final String END_POINT = "http://api.draglabs.com/api";
    private static final String API_VERSION = "/v2.0";
    private static final String BASE_STRING = END_POINT + API_VERSION;

    private static final String POST_EMAIL_URL = "";
    private static final String POST_MUSIC_CONSOLIDATOR_URL = "";

    private static final String AUTHENTICATE_USER = BASE_STRING + StringsModel.apiPaths.AUTHENTICATE_USER;
    private static final String SOLO_UPLOAD_RECORDING = BASE_STRING + StringsModel.apiPaths.SOLO_UPLOAD_RECORDING;
    private static final String START_JAM = BASE_STRING + StringsModel.apiPaths.START_JAM;
    private static final String JOIN_JAM = BASE_STRING + StringsModel.apiPaths.JOIN_JAM;
    private static final String JAM_RECORDING_UPLOAD = BASE_STRING + StringsModel.apiPaths.JAM_RECORDING_UPLOAD;
    private static final String EXIT_JAM = BASE_STRING + StringsModel.apiPaths.EXIT_JAM;
    private static final String GET_COLLABORATORS = BASE_STRING + StringsModel.apiPaths.GET_COLLABORATORS;
    private static final String GET_USER_ACTIVITY = BASE_STRING + StringsModel.apiPaths.GET_USER_ACTIVITY;
    private static final String NOTIFY_USER = BASE_STRING + StringsModel.apiPaths.NOTIFY_USER;
    private static final String GET_JAM_DETIALS = BASE_STRING + StringsModel.apiPaths.GET_JAM_DETAILS;
    private static final String GENERATE_XML = "http://54.153.93.94" + "/v1/generateXML/";
    private static final String COMPRESS = BASE_STRING + StringsModel.apiPaths.COMPRESS;

    private static final AsyncHttpClient asyncHttpClient = new AsyncHttpClient(true, 80, 433);

    /**
     * Sends email to company with support inquiries
     * @param email the email address sending the email
     * @param subject the subject of the email
     * @param message the email message
     * @return 0 if success, 1 if fail
     */
    public static int sendEmail(String email, String subject, String message) {
        try {
            Gson gson = new Gson();
            URL url = new URL(POST_EMAIL_URL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

            outputStreamWriter.write(gson.toJson(new EmailModel(email, subject, message)));
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

    /**
     * Authenticates the user
     * @param activity the activity performing the authentication
     */
    public static void authenticateUser(Activity activity) {
        String facebookID = Profile.getCurrentProfile().getId();
        Log.d("Facebook ID:", facebookID);
        String facebookAccessToken = AccessToken.getCurrentAccessToken().getToken();
        Log.d("Auth Token:", facebookAccessToken);

        final PrefUtils prefUtils = new PrefUtils(activity);
        prefUtils.saveAccessToken(facebookAccessToken);

        RequestParams requestParams = APIparams.authenticateUser(facebookID, facebookAccessToken);

        //JsonHttpHandler jsonHttpHandler = new JsonHttpHandler();
        //JSONObject response = jsonHttpHandler.getResponse();
        post(AUTHENTICATE_USER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("ResponseModel: ", response.toString());
                try {
                    String uniqueID = JsonUtils.getJsonObject(StringsModel.AUTHENTICATE_USER, response, StringsModel.jsonTypes.UNIQUE_ID.type());
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Uploads a single user's recording
     * @param uniqueID dlsAPI UUID
     * @param filename filename of the recording
     * @param path local path to the recording
     * @param notes recording notes
     * @param startTime recording start time
     * @param endTime recording end time
     */
    public static void soloUpload(String uniqueID, String filename, String path, String notes, Date startTime, Date endTime) {
        String newPOST = SOLO_UPLOAD_RECORDING + "/" + uniqueID;

        RequestParams requestParams = APIparams.soloUpload(uniqueID, filename, notes, startTime, endTime);

        upload("audioFile", path, newPOST, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("ResponseModel: ", response.toString());
                try {
                    String message = JsonUtils.getJsonObject(StringsModel.SOLO_UPLOAD_RECORDING, response, StringsModel.jsonTypes.MESSAGE.type());
                    Log.v("ResponseModel Message: ", message);
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Starts a jam
     * @param activity the activity starting a jam
     * @param uniqueID dlsAPI UUID
     * @param jamLocation the jam's location name
     * @param jamName the jam's name
     * @param location the jam's GPS location
     */
    public static void startJam(Activity activity, String uniqueID, String jamLocation, String jamName, Location location) {
        final PrefUtils prefUtils = new PrefUtils(activity);

        RequestParams requestParams = APIparams.startJam(uniqueID, jamLocation, jamName, location);

        post(START_JAM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("ResponseModel: ", response.toString());
                try {
                    String jamPIN = JsonUtils.getJsonObject(StringsModel.START_JAM, response, StringsModel.jsonTypes.PIN.type());
                    String jamID = JsonUtils.getJsonObject(StringsModel.START_JAM, response, StringsModel.jsonTypes.JAM_ID.type());
                    String jamStartTime = JsonUtils.getJsonObject(StringsModel.START_JAM, response, StringsModel.jsonTypes.START_TIME.type());
                    String jamEndTime = JsonUtils.getJsonObject(StringsModel.START_JAM, response, StringsModel.jsonTypes.END_TIME.type());
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Joins a jam
     * @param activity the activity joining the jam
     * @param uniqueID the dlsAPI UUID
     * @param pin the jam PIN
     */
    public static void joinJam(Activity activity, String uniqueID, int pin) {
        final PrefUtils prefUtils = new PrefUtils(activity);

        RequestParams requestParams = APIparams.joinJam(uniqueID, pin);

        post(JOIN_JAM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("ResponseModel: ", response.toString());
                try {
                    String jamPIN = JsonUtils.getJsonObject(StringsModel.JOIN_JAM, response, StringsModel.jsonTypes.PIN.type());
                    String jamID = JsonUtils.getJsonObject(StringsModel.JOIN_JAM, response, StringsModel.jsonTypes.JAM_ID.type());
                    String jamStartTime = JsonUtils.getJsonObject(StringsModel.JOIN_JAM, response, StringsModel.jsonTypes.START_TIME.type());
                    String jamEndTime = JsonUtils.getJsonObject(StringsModel.JOIN_JAM, response, StringsModel.jsonTypes.END_TIME.type());
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Uploads a jam's recording
     * @param uniqueID dlsAPI UUID
     * @param jamID the jam ID
     * @param filename the name of the file
     * @param path the local path to the file
     * @param notes the recording notes
     * @param startTime the recording's start time
     * @param endTime the recording's end time
     * @param view the view calling the upload
     */
    public static void jamRecordingUpload(String uniqueID, String jamID, String filename, String path, String notes, Date startTime, Date endTime, View view) { // convert through binary data and multi-part upload
        String newPOST = JAM_RECORDING_UPLOAD + "/" + uniqueID + "/jamid/" + jamID;

        Log.d("newPOST: ", newPOST);

        RequestParams requestParams = APIparams.jamRecordingUpload(uniqueID, jamID, filename, notes, startTime, endTime);
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
                Log.v("ResponseModel: ", response.toString());
                try {
                    String message = JsonUtils.getJsonObject(StringsModel.JAM_RECORDING_UPLOAD, response, StringsModel.jsonTypes.MESSAGE.type());
                    Log.v("ResponseModel Message: ", message);
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Uploads a jam's recording
     * @param uniqueID dlsAPI UUID
     * @param jamID the jam ID
     * @param filename the name of the file
     * @param path the local path to the file
     * @param notes the recording notes
     * @param startTime the recording's start time
     * @param endTime the recording's end time
     * @param view the view calling the upload
     */
    public static void jamRecordingUpload(String uniqueID, String jamID, String filename, String path, String notes, String startTime, String endTime, View view) { // convert through binary data and multi-part upload
        String newPOST = JAM_RECORDING_UPLOAD + "/" + uniqueID + "/jamid/" + jamID;

        Log.d("newPOST: ", newPOST);

        RequestParams requestParams = APIparams.jamRecordingUpload(uniqueID, jamID, filename, notes, startTime, endTime);
        //String string = uniqueID + "\n" + jamID + "\n" + filename + "\n" + notes + "\n" + startTime + "\n" + endTime;
        Snackbar.make(view, uniqueID, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, jamID, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, filename, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, notes, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, startTime, Snackbar.LENGTH_LONG).show();
        Snackbar.make(view, endTime, Snackbar.LENGTH_LONG).show();

        upload("audioFile", path, newPOST, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("ResponseModel: ", response.toString());
                try {
                    String message = JsonUtils.getJsonObject(StringsModel.JAM_RECORDING_UPLOAD, response, StringsModel.jsonTypes.MESSAGE.type());
                    Log.v("ResponseModel Message: ", message);
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Exits a jam
     * @param uniqueID the dlsAPI UUID
     * @param jamID the jam ID
     */
    public static void exitJam(String uniqueID, String jamID) {
        RequestParams requestParams = APIparams.exitJam(uniqueID, jamID);

        post(EXIT_JAM, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("ResponseModel: ", response.toString());
                try {
                    String message = JsonUtils.getJsonObject(StringsModel.EXIT_JAM, response, StringsModel.jsonTypes.MESSAGE.type());
                    Log.v("ResponseModel Message: ", message);
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Gets the collaborators for the jam, saves to PrefUtils
     * @param activity the activity calling the method
     * @param uniqueID the dlsAPI UUID
     * @param jamID the jam ID
     */
    public static void getCollaborators(Activity activity, String uniqueID, String jamID) {
        final PrefUtils prefUtils = new PrefUtils(activity);

        RequestParams requestParams = APIparams.getCollaborators(uniqueID, jamID);

        get(GET_COLLABORATORS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("ResponseModel: ", response.toString());
                try {
                    String collaborators = JsonUtils.getJsonObject(StringsModel.GET_COLLABORATORS, response, StringsModel.jsonTypes.MESSAGE.type());
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Gets the user's activity, saves to PrefUtils
     * @param activity the activity calling the jam
     * @param uniqueID the dlsAPI UUID
     * @param context the context of the app
     */
    // TODO: rather than save these to PrefUtils, why not somehow return? But also the fact that they're in async methods
    public static void getUserActivity(Activity activity, String uniqueID, Context context) {
        //String newPOST = GET_USER_ACTIVITY + "/" + uniqueID;
        String newPOST = GET_USER_ACTIVITY + "/";
        // TODO: add date as part of the request header

        RequestParams requestParams = APIparams.getUserActivity(uniqueID);

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
                Log.v("ResponseModel: ", response.toString());
                try {
                    /*if (type.equals(StringsModel.jsonTypes.RECORDINGS.type())) {
                        String recordings = JsonUtils.getJsonObject(StringsModel.GET_USER_ACTIVITY, response, type);
                        Log.v("Recordings: ", recordings);
                        prefUtils.saveUserActivity(recordings);
                    } else if (type.equals(StringsModel.jsonTypes.JAMS.type())){
                        String jams = JsonUtils.getJsonObject(StringsModel.GET_USER_ACTIVITY, response, type);
                        Log.v("Jams: ", jams);
                        prefUtils.saveUserActivity(jams);
                    }*/
                    String jams = JsonUtils.getJsonObject(StringsModel.GET_USER_ACTIVITY, response, StringsModel.jsonTypes.JAMS.type());
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Gets the user's jam's details
     * @param userID the dlsAPI UUID
     * @param jamID the jam ID
     * @param activity the activity calling the method
     * @param context the app's context
     */
    // TODO: needs to then somehow be sent to the JamDetails Activity/ListOfRecordings
    public static void getJamDetails(String userID, String jamID, Activity activity, Context context) {
        RequestParams requestParams = APIparams.getJamDetails();

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
                Log.v("ResponseModel: ", response.toString());
                try {
                    String jamDetails = JsonUtils.getJsonObject(StringsModel.GET_JAM_DETAILS, response, StringsModel.jsonTypes.DATA.type());
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
            }
        });
    }

    /**
     * Prepares to generate the XML
     * @param jamID the jam ID
     * @param context the app's context
     */
    public static void notifyUser(String jamID, Context context) {
        RequestParams requestParams = APIparams.notifyUser(jamID);

        post(NOTIFY_USER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Function: ", "Notify User");
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("ResponseModel: ", response.toString());
                try {
                    String message = JsonUtils.getJsonObject(StringsModel.NOTIFY_USER, response, StringsModel.jsonTypes.MESSAGE.type());
                    Log.v("ResponseModel Message: ", message);
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
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
                createNotifyUserErrorDialog(jamID, context);
            }
        });
    }

    /**
     * Generates the XML
     * @param jamID the jam ID
     * @param jamDetails the jam's details
     * @param context the app's context
     */
    // TODO: why the jamDetails?
    public static void generateXML(String jamID, JSONObject jamDetails, Context context) {
        RequestParams requestParams = APIparams.generateXML(jamID);

        post(GENERATE_XML + jamID, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               Log.v("Function: ", "Generate XML");
               Log.v("Status Code: ", statusCode + "");
               Log.v("Headers: ", Arrays.toString(headers));
               Log.v("ResponseModel: ", response.toString());
               createDialog(context, jamID, "Generate XML", response.toString());
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                   Log.v("Status Code: ", statusCode + "\n");
                   Log.v("Headers: ", Arrays.toString(headers) + "");
                   Log.v("Throwable: ", throwable.getMessage());
                   Log.v("ResponseModel: ", response.toString());
               } else {
                   Log.v("Reason: ", "Other Failure.");
               }
               createDialog(context, jamID, "Failure", response != null ? response.toString() : null);
           }
        });
    }

    /**
     * Sends the use the email with the xml and zip
     * @param jamID the jam ID
     * @param userID the dlsAPI UUID
     * @param jamDetails the jam's details
     * @param context the app's context
     */
    // TODO: are the jamDetails necessary?
    public static void compress(String jamID, String userID, JSONObject jamDetails, Context context) {
        RequestParams requestParams = APIparams.compress(jamID, userID);

        post(COMPRESS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Function: ", "Compress");
                Log.v("Status Code: ", statusCode + "");
                Log.v("Headers: ", Arrays.toString(headers));
                Log.v("ResponseModel: ", response.toString());
                createDialog(context, jamID, "Compress", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (headers != null && throwable != null && response != null) {
                    Log.v("Status Code: ", statusCode + "\n");
                    Log.v("Headers: ", Arrays.toString(headers) + "");
                    Log.v("Throwable: ", throwable.getMessage());
                    Log.v("ResponseModel: ", response.toString());
                } else {
                    Log.v("Reason: ", "Other Failure.");
                }
                createDialog(context, jamID, "Failure", response != null ? response.toString() : null);
            }
        });
    }

    /**
     * Creates dialog to notify the user
     * @param jamID the jam ID
     * @param context the app's context
     */
    // TODO: should this be here or elsewhere?
    private static void createNotifyUserErrorDialog(String jamID, Context context) { // to notify user with email
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Please check it").setTitle("Invalid Jam ID");
        builder.setNegativeButton("Okay", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Creates a dialog
     * @param context the app's context
     * @param jamID the jam ID
     * @param title the dialog title
     * @param message the dialog message
     */
    private static void createDialog(Context context, String jamID, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title);
        builder.setNeutralButton("Okay", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Sends HTTP POST request
     * @param url target url
     * @param requestParams HTTP parameters
     * @param asyncHttpResponseHandler handles the response
     * @return "Done with POST"
     */
    private static String post(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.post(url, requestParams, asyncHttpResponseHandler);
        return "Done with POST.";
    }

    /**
     * Sends HTTP GET request
     * @param url target url
     * @param requestParams HTTP parameters
     * @param asyncHttpResponseHandler handles the response
     * @return "Done with GET."
     */
    private static String get(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.get(url, requestParams, asyncHttpResponseHandler);
        return "Done with GET.";
    }

    /**
     * Sends HTTP GET request
     * @param context the app's context
     * @param url target url
     * @param headers HTTP headers
     * @param requestParams HTTP parameters
     * @param asyncHttpResponseHandler handles the response
     * @return "Done with GET."
     */
    private static String get(Context context, String url, Header[] headers, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.get(context, url, headers, requestParams, asyncHttpResponseHandler);
        return "Done with GET.";
    }

    /**
     * Uploads a file
     * @param filename the file name
     * @param path the local file path
     * @param url the target url
     * @param asyncHttpResponseHandler handles the response
     */
    private static void upload(String filename, String path, String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.post(url, APIparams.createRequestParamsForUpload(filename, path), asyncHttpResponseHandler);
    }

    /**
     * Uploads a file
     * @param filename the file name
     * @param path the local file path
     * @param url the target url
     * @param requestParams the HTTP parameters
     * @param asyncHttpResponseHandler handles the response
     */
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
