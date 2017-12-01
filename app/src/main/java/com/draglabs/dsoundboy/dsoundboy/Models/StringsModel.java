package com.draglabs.dsoundboy.dsoundboy.Models;

/**
 * <p>Holds strings used throughout the program, separate from the XML strings file</p>
 * <p>Created by davrukin on 9/12/17.</p>
 */

@SuppressWarnings("DefaultFileTemplate")
public final class StringsModel {

    public static final String AUTHENTICATE_USER = "Authenticate User";
    public static final String SOLO_UPLOAD_RECORDING = "Solo Upload Recording";
    public static final String START_JAM = "Start Jam";
    public static final String JOIN_JAM = "Join Jam";
    public static final String JAM_RECORDING_UPLOAD = "Jam Recording Upload";
    public static final String EXIT_JAM = "Exit Jam";
    public static final String GET_COLLABORATORS = "Get Collaborators";
    public static final String GET_USER_ACTIVITY = "Get User Activity";
    public static final String NOTIFY_USER = "Notify User";
    public static final String GET_JAM_DETAILS = "Get Jam Details";

    private static final String JAM = "/jam/";
    private static final String USER = "/user/";

    /**
     * Strings used for parsing JSON
     */
    public enum jsonTypes {
        CODE("code"),
        USER_ID("user_id"),
        UNIQUE_ID("id"),
        FIRST_NAME("first_name"),
        LAST_NAME("last_name"),
        MESSAGE("message"),
        START_TIME("startTime"),
        END_TIME("endTime"),
        PIN("pin"),
        JAM_ID("id"),
        RECORDINGS("recordings"),
        S3URL("s3url"),
        FILENAME("fileName"),
        NOTES("notes"),
        RECORDING_ID("_id"),
        JAMS("jams"),
        DATA("data");

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
        AUTHENTICATE_USER(JAM + "auth"),
        START_JAM(JAM + "start"),
        JOIN_JAM(JAM + "join"),
        JAM_RECORDING_UPLOAD(JAM + "upload/userid"),
        EXIT_JAM(JAM + "exit"),
        GET_COLLABORATORS(JAM + "collaborators"),
        COMPRESS(JAM + "archive"),
        NOTIFY_USER(JAM + "notifyuser"),
        GET_USER_ACTIVITY(USER + "activity/id"),
        GET_JAM_DETAILS(USER + "jam-details?jamId="),
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
