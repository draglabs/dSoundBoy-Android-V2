package com.draglabs.dsoundboy.dsoundboy.Models;

import android.util.Log;

import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

/**
 * Holds a JSON response that is returned
 * Created by davrukin on 9/11/17.
 * @author Daniel Avrukin
 */
@Deprecated
public class ResponseModel {

    private int statusCode;
    private Header[] headers;
    private JSONObject response;

    private String id;
    private String pin;
    private boolean is_current;
    private String name;
    private String user_id;
    private double[] coordinates;
    private int collaborators;
    private String location;
    private String start_time;
    private String end_time;
    private String notes;
    private String archive_url;
    private String first_name;
    private String last_name;
    private String fb_email;
    private String fb_id;
    private String message;
    private Jam current_jam;
    private Recording[] recordings;

    /**
     * Default no-arg constructor for the ResponseModel
     */
    public ResponseModel() {
        /*statusCode = 0;
        headers = null;
        response = null;*/
    }

    /**
     * Constructor the the ResponseModel
     * @param statusCode the status code
     * @param headers the HTTP headers
     * @param response the JSON response
     */
    public ResponseModel(int statusCode, Header[] headers, JSONObject response) {
        /*this.statusCode = statusCode;
        this.headers = headers;
        this.response = response;*/
    }

    public ResponseModel(String id, String pin, boolean is_current, String name, String user_id, double[] coordinates, int collaborators, Recording[] recordings, String location, String start_time, String end_time, String notes, String archive_url, String first_name, String last_name, String fb_email, String fb_id, Jam current_jam, String message) {
        this.id = id;
        this.pin = pin;
        this.is_current = is_current;
        this.name = name;
        this.user_id = user_id;
        this.coordinates = coordinates;
        this.collaborators = collaborators;
        this.recordings = recordings;
        this.location = location;
        this.start_time = start_time;
        this.end_time = end_time;
        this.notes = notes;
        this.archive_url = archive_url;
        this.first_name = first_name;
        this.last_name = last_name;
        this.fb_email = fb_email;
        this.fb_id = fb_id;
        this.current_jam = current_jam;
        this.message = message;
    }

    private class Recording {
        private String id;
        private String user_id;
        private String file_name;
        private String jam_id;
        private String start_time;
        private String end_time;
        private String notes;
        private String s3url;

        public Recording() {
        }

        public Recording(String id, String user_id, String file_name, String jam_id, String start_time, String end_time, String notes, String s3url) {
            this.id = id;
            this.user_id = user_id;
            this.file_name = file_name;
            this.jam_id = jam_id;
            this.start_time = start_time;
            this.end_time = end_time;
            this.notes = notes;
            this.s3url = s3url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public String getJam_id() {
            return jam_id;
        }

        public void setJam_id(String jam_id) {
            this.jam_id = jam_id;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getS3url() {
            return s3url;
        }

        public void setS3url(String s3url) {
            this.s3url = s3url;
        }
    }

    private class Jam {

        private String id;
        private String pin;
        private boolean is_current;
        private String name;
        private String user_id;
        private double[] coordinates;
        private int collaborators;
        private Recording[] recordings;
        private String location;
        private String start_time;
        private String end_time;
        private String notes;

        public Jam() {
        }

        public Jam(String id, String pin, boolean is_current, String name, String user_id, double[] coordinates, int collaborators, Recording[] recordings, String location, String start_time, String end_time, String notes) {
            this.id = id;
            this.pin = pin;
            this.is_current = is_current;
            this.name = name;
            this.user_id = user_id;
            this.coordinates = coordinates;
            this.collaborators = collaborators;
            this.recordings = recordings;
            this.location = location;
            this.start_time = start_time;
            this.end_time = end_time;
            this.notes = notes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public boolean isIs_current() {
            return is_current;
        }

        public void setIs_current(boolean is_current) {
            this.is_current = is_current;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public double[] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[] coordinates) {
            this.coordinates = coordinates;
        }

        public int getCollaborators() {
            return collaborators;
        }

        public void setCollaborators(int collaborators) {
            this.collaborators = collaborators;
        }

        public Recording[] getRecordings() {
            return recordings;
        }

        public void setRecordings(Recording[] recordings) {
            this.recordings = recordings;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public boolean isIs_current() {
        return is_current;
    }

    public void setIs_current(boolean is_current) {
        this.is_current = is_current;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public int getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(int collaborators) {
        this.collaborators = collaborators;
    }

    public Recording[] getRecordings() {
        return recordings;
    }

    public void setRecordings(Recording[] recordings) {
        this.recordings = recordings;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getArchive_url() {
        return archive_url;
    }

    public void setArchive_url(String archive_url) {
        this.archive_url = archive_url;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFb_email() {
        return fb_email;
    }

    public void setFb_email(String fb_email) {
        this.fb_email = fb_email;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public Jam getCurrent_jam() {
        return current_jam;
    }

    public void setCurrent_jam(Jam current_jam) {
        this.current_jam = current_jam;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Logs the response to console
     */
    public void logResponse(String functionCall) {
        Log.v("Function Call: ", functionCall);
        Log.v("Status Code: ", statusCode + "");
        Log.v("Headers: ", Arrays.toString(headers));
        Log.v("ResponseModel: ", response.toString());
    }

    /**
     * Gets the status code
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status code
     * @param statusCode the status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the HTTP headers
     * @return the HTTP headers
     */
    public Header[] getHeaders() {
        return headers;
    }

    /**
     * Sets the HTTP headers
     * @param headers the HTTP headers
     */
    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    /**
     * Gets the JSON object response
     * @return the JSON object response
     */
    public JSONObject getResponse() {
        return response;
    }
}
