package com.draglabs.dsoundboy.dsoundboy.Params;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.ParseException;

/**
 * <p>Contains headers for dlsAPI calls</p>
 * <p>Created by davrukin on 12/6/2017.</p>
 * @author Daniel Avrukin
 */
public class APIheaders {

    /**
     * Standard header for most of the dlsAPI calls
     * @param UUID the dlsAPI unique user ID
     * @return new headers, with content-type application/json and the user ID
     */
    public static Header[] standardHeader(String UUID) {
        return new Header[] { new Header() {
            @Override
            public String getName() {
                return "Content-Type";
            }

            @Override
            public String getValue() {
                return "application/json";
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        }, new Header() {
            @Override
            public String getName() {
                return "user_id";
            }

            @Override
            public String getValue() {
                return UUID;
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[1];
            }
        }};
    }

    /**
     * Header containing content-type application/x-www-form-urlencoded and the user's email
     * @param emailAddress the user's email address
     * @return new headers, with content type application/x-www-form-urlencoded and the user's email
     */
    public static Header[] withEmail(String emailAddress) {
        return new Header[] { new Header() {
            @Override
            public String getName() {
                return "Content-Type";
            }

            @Override
            public String getValue() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        }, new Header() {
            @Override
            public String getName() {
                return "email";
            }

            @Override
            public String getValue() {
                return emailAddress;
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[1];
            }
        }};
    }

    /**
     * Header containing just the user ID
     * @param UUID the dlsAPI unique user ID
     * @return new headers, with the user ID
     */
    public static Header[] userID(String UUID) {
        return new Header[] { new Header() {
            @Override
            public String getName() {
                return "user_id";
            }

            @Override
            public String getValue() {
                return UUID;
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        }};
    }

    /**
     * Header containing just the content-type: application/json
     * @return new headers, with the content-type application/json
     */
    public static Header[] contentTypeJson() {
        return new Header[] { new Header() {
            @Override
            public String getName() {
                return "Content-Type";
            }

            @Override
            public String getValue() {
                return "application/json";
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        }};
    }

    /**
     * Returns blank header
     * @return blank header
     */
    public static Header[] blank() {
        return new Header[]{};
    }
}
