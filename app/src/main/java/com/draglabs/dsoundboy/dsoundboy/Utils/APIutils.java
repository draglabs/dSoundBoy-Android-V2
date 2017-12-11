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
import com.draglabs.dsoundboy.dsoundboy.Params.APIheaders;
import com.draglabs.dsoundboy.dsoundboy.Params.APIparams;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

/**
 * <p>Holds all dlsAPI calls</p>
 * <p>Created by davrukin on 8/14/17.</p>
 */
@SuppressWarnings("DefaultFileTemplate")
public class APIutils { // TODO: docs for all other utils

    private static final String POST_EMAIL_URL = "";

    // CURRENT FEATURES //
    private static final String NEW_JAM = StringsModel.apiPaths.NEW_JAM.path();
    private static final String UPDATE_JAM = StringsModel.apiPaths.UPDATE_JAM.path();
    private static final String JOIN_JAM = StringsModel.apiPaths.JOIN_JAM.path();
    private static final String JAM_RECORDING_UPLOAD = StringsModel.apiPaths.JAM_RECORDING_UPLOAD.path();
    private static final String GET_JAM_DETAILS = StringsModel.apiPaths.GET_JAM_DETAILS.path();
    private static final String GET_RECORDINGS = StringsModel.apiPaths.GET_RECORDINGS.path();
    private static final String REGISTER_USER = StringsModel.apiPaths.REGISTER_USER.path();
    private static final String UPDATE_USER = StringsModel.apiPaths.UPDATE_USER.path();
    private static final String GET_ACTIVE_JAM = StringsModel.apiPaths.GET_ACTIVE_JAM.path();
    private static final String GET_USER_ACTIVITY = StringsModel.apiPaths.GET_USER_ACTIVITY.path();

    // DEPRECATED FEATURES //
    private static final String SOLO_UPLOAD_RECORDING = StringsModel.apiPaths.SOLO_UPLOAD_RECORDING.path();
    private static final String EXIT_JAM = StringsModel.apiPaths.EXIT_JAM.path();
    private static final String GET_COLLABORATORS = StringsModel.apiPaths.GET_COLLABORATORS.path();
    private static final String NOTIFY_USER = StringsModel.apiPaths.NOTIFY_USER.path();
    private static final String GENERATE_XML = "http://54.153.93.94" + "/v1/generateXML/";
    private static final String COMPRESS = StringsModel.apiPaths.COMPRESS.path();

    private static final AsyncHttpClient asyncHttpClient = new AsyncHttpClient(true, 80, 433);

    /**
     * Starts a jam
     * @param activity the activity starting a jam
     * @param context the app's context
     * @param UUID dlsAPI UUID
     * @param jamLocation the jam's location name
     * @param jamName the jam's name
     * @param location the jam's GPS location
     */
    public static void newJam(Activity activity, Context context, String UUID, String jamLocation, String jamName, Location location) {
        final PrefUtils prefUtils = new PrefUtils(activity);

        Header[] headers = APIheaders.standardHeader(UUID);
        RequestParams requestParams = APIparams.newJam(UUID, jamLocation, jamName, location, "");

        post(context, NEW_JAM, headers, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);

                prefUtils.saveJamPIN((String)JsonUtils.getItem(response, StringsModel.jsonTypes.PIN.type()));
                prefUtils.saveJamID((String)JsonUtils.getItem(response, StringsModel.jsonTypes.JAM_ID.type()));
                prefUtils.saveJamStartTime((String)JsonUtils.getItem(response, StringsModel.jsonTypes.START_TIME.type()));
                prefUtils.saveJamEndTime((String)JsonUtils.getItem(response, StringsModel.jsonTypes.END_TIME.type()));
                createDialog(activity, "", "Jam PIN", (String)JsonUtils.getItem(response, StringsModel.jsonTypes.PIN.type()));

                /*try {
                    String jamPIN = JsonUtils.getJsonObject(StringsModel.NEW_JAM, response, StringsModel.jsonTypes.PIN.type());
                    String jamID = JsonUtils.getJsonObject(StringsModel.NEW_JAM, response, StringsModel.jsonTypes.JAM_ID.type());
                    String jamStartTime = JsonUtils.getJsonObject(StringsModel.NEW_JAM, response, StringsModel.jsonTypes.START_TIME.type());
                    String jamEndTime = JsonUtils.getJsonObject(StringsModel.NEW_JAM, response, StringsModel.jsonTypes.END_TIME.type());
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
                }*/
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    public static void updateJam(Activity activity, Context context, String UUID, String jamName, String jamID, String jamLocation, String jamNotes) {
        final PrefUtils prefUtils = new PrefUtils(activity);

        Header[] headers = APIheaders.standardHeader(UUID);
        RequestParams requestParams = APIparams.updateJam(jamName, jamID, jamLocation, jamNotes);

        post(context, UPDATE_JAM, headers, requestParams, new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               logSuccessResponse(statusCode, headers, response); // TODO: parse the json response
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
               logFailureResponse(statusCode, headers, throwable, response);
           }
        });
    }

    /**
     * Joins a jam
     * @param activity the activity joining the jam
     * @param UUID the dlsAPI UUID
     * @param pin the jam PIN
     */
    public static void joinJam(Activity activity, Context context, int pin, String UUID) {
        final PrefUtils prefUtils = new PrefUtils(activity);

        Header[] headers = APIheaders.standardHeader(UUID);
        RequestParams requestParams = APIparams.joinJam(UUID, pin);

        post(context, JOIN_JAM, headers, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);
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
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    /**
     * Uploads a jam's recording
     * @param UUID dlsAPI UUID
     * @param jamID the jam ID
     * @param path the local path to the file
     * @param notes the recording notes
     * @param startTime the recording's start time
     * @param endTime the recording's end time
     * @param view the view calling the upload
     */
    public static void jamRecordingUpload(Context context, String UUID, String jamID, String path, String notes, Date startTime, Date endTime, View view) { // convert through binary data and multi-part upload
        String newPOST = JAM_RECORDING_UPLOAD;

        Header[] headers = APIheaders.standardHeader(UUID);
        RequestParams requestParams = APIparams.jamRecordingUpload(UUID, jamID, path, notes, startTime, endTime);

        upload(context, headers, requestParams, "audioFile", path, newPOST, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);
                try {
                    String message = JsonUtils.getJsonObject(StringsModel.JAM_RECORDING_UPLOAD, response, StringsModel.jsonTypes.MESSAGE.type());
                    Log.v("Response Message: ", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Snackbar.make(view, "Recording uploaded.", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    /**
     * Uploads a jam's recording
     * @param context the app's context
     * @param UUID dlsAPI UUID
     * @param jamID the jam ID
     * @param path the local path to the file
     * @param notes the recording notes
     * @param startTime the recording's start time
     * @param endTime the recording's end time
     * @param view the view calling the upload
     */
    public static void jamRecordingUpload(Context context, String UUID, String jamID, String path, String notes, String startTime, String endTime, View view) { // convert through binary data and multi-part upload
        String newPOST = JAM_RECORDING_UPLOAD;

        Header[] headers = APIheaders.standardHeader(UUID);
        RequestParams requestParams = APIparams.jamRecordingUpload(UUID, jamID, path, notes, startTime, endTime);

        upload(context, headers, requestParams, "audioFile", path, newPOST, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);
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
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    /**
     * Gets the user's jam's details
     * @param UUID the dlsAPI UUID
     * @param jamID the jam ID
     * @param activity the activity calling the method
     * @param context the app's context
     */
    // TODO: needs to then somehow be sent to the JamDetails Activity/ListOfRecordings
    public static void getJamDetails(String UUID, String jamID, Activity activity, Context context) {
        String newGET = GET_JAM_DETAILS + jamID;

        RequestParams requestParams = APIparams.getJamDetails();
        Header[] headers = APIheaders.userID(UUID);

        get(context, newGET, headers, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);
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
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    /**
     * Gets the json array of recordings
     * @param jamID the jam ID
     */
    public static void getRecordings(String jamID) {
        String newGET = GET_RECORDINGS + jamID;

        RequestParams requestParams = APIparams.getRecordings();
        //Header[] headers = APIheaders.blank();

        get(newGET, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    /**
     * Authenticates the user
     * @param activity the activity performing the authentication
     * @param context the app's context
     */
    public static void registerUser(Activity activity, Context context) {
        String facebookID = Profile.getCurrentProfile().getId();
        Log.d("Facebook ID:", facebookID);
        String facebookAccessToken = AccessToken.getCurrentAccessToken().getToken();
        Log.d("Auth Token:", facebookAccessToken);

        final PrefUtils prefUtils = new PrefUtils(activity);
        prefUtils.saveAccessToken(facebookAccessToken);

        Header[] headers = APIheaders.contentTypeJson();
        RequestParams requestParams = APIparams.registerUser(facebookID, facebookAccessToken);

        post(context, REGISTER_USER, headers, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);
                try {
                    String uniqueID = JsonUtils.getJsonObject(StringsModel.REGISTER_USER, response, StringsModel.jsonTypes.UUID.type());
                    Log.v("Unique ID: ", uniqueID);
                    prefUtils.saveUniqueUserID(uniqueID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    /**
     * Updates the user
     * @param context the app's context
     * @param emailAddress the user's email address
     */
    public static void updateUser(Context context, String emailAddress) {
        Header[] headers = APIheaders.withEmail(emailAddress);
        RequestParams requestParams = APIparams.updateUser();
        HttpEntity httpEntity = new HttpEntity() {
            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public boolean isChunked() {
                return false;
            }

            @Override
            public long getContentLength() {
                return 0;
            }

            @Override
            public Header getContentType() {
                return null;
            }

            @Override
            public Header getContentEncoding() {
                return null;
            }

            @Override
            public InputStream getContent() throws IOException, IllegalStateException {
                return null;
            }

            @Override
            public void writeTo(OutputStream outstream) throws IOException {

            }

            @Override
            public boolean isStreaming() {
                return false;
            }

            @Override
            public void consumeContent() throws IOException {

            }
        };

        put(context, UPDATE_USER, headers, httpEntity, headers[0].getValue(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    /**
     * Gets the active jam
     * @param context the app's context
     * @param UUID the dlsAPI UUID
     */
    public static void getActiveJam(Context context, String UUID) {
        Header[] headers = APIheaders.userID(UUID);
        RequestParams requestParams = APIparams.getActiveJam();

        post(context, GET_ACTIVE_JAM, headers, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    /**
     * Gets the user's activity, saves to PrefUtils
     * @param activity the activity calling the jam
     * @param UUID the dlsAPI UUID
     * @param context the context of the app
     */
    // TODO: rather than save these to PrefUtils, why not somehow return? But also the fact that they're in async methods
    public static void getUserActivity(Activity activity, String UUID, Context context) {
        Header[] headers = APIheaders.userID(UUID);
        RequestParams requestParams = APIparams.getUserActivity();

        get(context, GET_USER_ACTIVITY, headers, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                logSuccessResponse(statusCode, headers, response);
                try {
                    /*if (type.equals(StringsModel.jsonTypes.RECORDINGS.type())) {
                        String recordings = JsonUtils.getResponse(StringsModel.GET_USER_ACTIVITY, response, type);
                        Log.v("Recordings: ", recordings);
                        prefUtils.saveUserActivity(recordings);
                    } else if (type.equals(StringsModel.jsonTypes.JAMS.type())){
                        String jams = JsonUtils.getResponse(StringsModel.GET_USER_ACTIVITY, response, type);
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
                logFailureResponse(statusCode, headers, throwable, response);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

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
    private static String post(Context context, String url, Header[] headers, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestHandle requestHandle = asyncHttpClient.post(context, url, headers, requestParams, headers[0].getValue(), asyncHttpResponseHandler);
        if (requestHandle.isFinished()) {
            return "Done with POST. Tag: " + requestHandle.getTag();
        } else {
            return "POST Failed. Tag: " + requestHandle.getTag();
        }
    }

    /**
     * Sends HTTP POST request
     * @param url target url
     * @param requestParams HTTP parameters
     * @param asyncHttpResponseHandler handles the response
     * @return "Done with POST"
     */
    private static String post(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestHandle requestHandle = asyncHttpClient.post(url, requestParams, asyncHttpResponseHandler);
        if (requestHandle.isFinished()) {
            return "Done with POST. Tag: " + requestHandle.getTag();
        } else {
            return "POST Failed. Tag: " + requestHandle.getTag();
        }
    }

    /**
     * Sends HTTP GET request
     * @param url target url
     * @param requestParams HTTP parameters
     * @param asyncHttpResponseHandler handles the response
     * @return "Done with GET."
     */
    private static String get(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestHandle requestHandle = asyncHttpClient.get(url, requestParams, asyncHttpResponseHandler);
        if (requestHandle.isFinished()) {
            return "Done with GET. Tag: " + requestHandle.getTag();
        } else {
            return "GET Failed. Tag: " + requestHandle.getTag();
        }    }

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
        RequestHandle requestHandle = asyncHttpClient.get(context, url, headers, requestParams, asyncHttpResponseHandler);
        if (requestHandle.isFinished()) {
            return "Done with GET. Tag: " + requestHandle.getTag();
        } else {
            return "GET Failed. Tag: " + requestHandle.getTag();
        }    }

    /**
     * Sends HTTP PUT request
     * @param context the app's context
     * @param url the target url
     * @param headers the HTTP headers
     * @param httpEntity the HTTP entity
     * @param contentType the HTTP content type
     * @param responseHandler the response handler
     * @return "Done with PUT"
     */
    private static String put(Context context, String url, Header[] headers, HttpEntity httpEntity, String contentType, AsyncHttpResponseHandler responseHandler) {
        RequestHandle requestHandle = asyncHttpClient.put(context, url, headers, httpEntity, contentType, responseHandler);
        if (requestHandle.isFinished()) {
            return "Done with PUT. Tag: " + requestHandle.getTag();
        } else {
            return "PUT Failed. Tag: " + requestHandle.getTag();
        }
    }

    /**
     * Uploads a file, headers + params
     * @param context  the app context
     * @param headers the HTTP headers
     * @param filename the file name
     * @param path the local file path
     * @param url the target url
     * @param asyncHttpResponseHandler handles the response
     */
    private static void upload(Context context, Header[] headers, RequestParams requestParams, String filename, String path, String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestHandle requestHandle = asyncHttpClient.post(context, url, headers, requestParams, headers[0].getValue(), asyncHttpResponseHandler);
        if (requestHandle.isFinished()) {
            Log.d("Upload Result: ", "Done with Upload. Tag: " + requestHandle.getTag());
        } else {
            Log.d("Upload Result: ", "Upload Failed. Tag: " + requestHandle.getTag());
        }
    }

    /**
     * Uploads a file
     * @param filename the file name
     * @param path the local file path
     * @param url the target url
     * @param asyncHttpResponseHandler handles the response
     */
    private static void upload(String filename, String path, String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestHandle requestHandle = asyncHttpClient.post(url, APIparams.createRequestParamsForUpload(filename, path), asyncHttpResponseHandler);
        if (requestHandle.isFinished()) {
            Log.d("Upload Result: ", "Done with Upload. Tag: " + requestHandle.getTag());
        } else {
            Log.d("Upload Result: ", "Upload Failed. Tag: " + requestHandle.getTag());
        }
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

        RequestHandle requestHandle = asyncHttpClient.post(url, requestParams, asyncHttpResponseHandler); // too many prints in the logger
        if (requestHandle.isFinished()) {
            Log.d("Upload Result: ", "Done with Upload. Tag: " + requestHandle.getTag());
        } else {
            Log.d("Upload Result: ", "Upload Failed. Tag: " + requestHandle.getTag());
        }
    }

    /**
     * Logs the HTTP success response to console
     * @param statusCode the status code
     * @param headers the headers
     * @param response the response
     */
    private static void logSuccessResponse(int statusCode, Header[] headers, JSONObject response) {
        Log.v("Status Code: ", statusCode + "");
        Log.v("Headers: ", Arrays.toString(headers));
        Log.v("Response: ", response.toString());
    }

    /**
     * Logs the HTTP failure response to console
     * @param statusCode the status code
     * @param headers the headers
     * @param throwable the throwable error
     * @param response the response
     */
    private static void logFailureResponse(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
        if (headers != null && throwable != null && response != null) {
            Log.v("Status Code: ", statusCode + "\n");
            Log.v("Headers: ", Arrays.toString(headers) + "");
            Log.v("Throwable: ", throwable.getMessage());
            Log.v("Response: ", response.toString());
        } else {
            Log.v("Reason: ", "Other Failure.");
        }
    }
}
