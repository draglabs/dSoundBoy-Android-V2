package com.draglabs.dsoundboy.dsoundboy.Models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * <p>Holds strings used throughout the program, separate from the XML strings file</p>
 * <p>Created by davrukin on 9/12/17.</p>
 * @author Daniel Avrukin
 */
@SuppressWarnings("DefaultFileTemplate")
@Deprecated
public final class StringsModel {

    private static final String END_POINT = "http://api.draglabs.com/api";
    private static final String API_VERSION = "/v2.0";
    private static final String BASE_STRING = END_POINT + API_VERSION;

    private static final String JAM = BASE_STRING + "/jam/";
    private static final String USER = BASE_STRING + "/user/";

    public static final DateFormat STANDARD_DATE_FORMAT_OLD = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    public static final DateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ZZZZ z", Locale.US); // TODO: is this correct?

    // CURRENT FEATURES //
    public static final String NEW_JAM = "New Jam";
    public static final String UPDATE_JAM = "Update Jam";
    public static final String JOIN_JAM = "Join Jam";
    public static final String JAM_RECORDING_UPLOAD = "Jam Recording Upload";
    public static final String GET_JAM_DETAILS = "Get Jam Details";
    public static final String GET_RECORDINGS = "Get Recordings";
    public static final String REGISTER_USER = "Register UserModel";
    public static final String UPDATE_USER = "Update UserModel";
    public static final String GET_ACTIVE_JAM = "Get Active Jam";
    public static final String GET_USER_ACTIVITY = "Get UserModel Activity";

    // DEPRECATED FEATURES //
    public static final String SOLO_UPLOAD_RECORDING = "Solo Upload Recording";
    public static final String EXIT_JAM = "Exit Jam";
    public static final String GET_COLLABORATORS = "Get Collaborators";
    public static final String NOTIFY_USER = "Notify UserModel";

    /**
     * Strings used for parsing JSON
     */
    public enum jsonTypes {
        CODE("code"),
        JAM_ID("id"),
        UUID("id"),
        RECORDING_ID("_id"),
        USER_ID("user_id"),
        FIRST_NAME("first_name"),
        LAST_NAME("last_name"),
        MESSAGE("message"),
        START_TIME("start_time"),
        END_TIME("end_time"),
        PIN("pin"),
        RECORDINGS("recordings"),
        S3URL("s3url"),
        FILENAME("file_name"),
        NOTES("notes"),
        JAMS("jams"),
        DATA("data"),
        LOCATION("location"),
        LATITUDE("lat"),
        LONGITUDE("lng"),
        COORDINATES("coordinates"),
        IS_CURRENT("is_current"),
        COLLABORATORS("collaborators"),
        FILE_NAME("file_name"),
        ARCHIVE_URL("archive_url"), // TODO: to be implemented soon in next version
        FB_EMAIL("fb_email"),
        FB_ID("fb_id"),
        CURRENT_JAM("current_jam");

        private final String name;

        /**
         * Constructor for the enum
         * @param name the name of the enum string
         */
        jsonTypes(String name) {
            this.name = name;
        }

        /**
         * Returns the name of the enum string
         * @return the name of the enum string
         */
        public String type() {
            return name;
        }
    }

    /**
     * Holds paths for all the API calls
     */
    public enum apiPaths {
        // CURRENT FEATURES //
        NEW_JAM(JAM + "new"),
        UPDATE_JAM(JAM + "update"),
        JOIN_JAM(JAM + "join"),
        JAM_RECORDING_UPLOAD(JAM + "upload"),
        GET_JAM_DETAILS(JAM + "details/"), // append Jam ID
        GET_RECORDINGS(JAM + "recording/"), // append Jam ID
        REGISTER_USER(USER + "register"),
        UPDATE_USER(USER + "update"),
        GET_ACTIVE_JAM(USER + "jam/active"),
        GET_USER_ACTIVITY(USER + "activity"),

        // DEPRECATED FEATURES //
        EXIT_JAM(JAM + "exit"),
        GET_COLLABORATORS(JAM + "collaborators"),
        COMPRESS(JAM + "archive"),
        NOTIFY_USER(JAM + "notifyuser"),
        SOLO_UPLOAD_RECORDING("/soloupload/id");

        private final String path;

        /**
         * Constructor for the enum
         * @param path the dlsAPI path of the enum string
         */
        apiPaths(String path) {
            this.path = path;
        }

        /**
         * Returns the dlsAPI path for the enum string
         * @return the dlsAPI path for the enum string
         */
        public String path() {
            return path;
        }
    }
}
