package com.draglabs.dsoundboy.dsoundboy.Models;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Holds a JSON response that is returned
 * Created by davrukin on 9/11/17.
 */

class ResponseModel {

    private int statusCode;
    private Header[] headers;
    private JSONObject jsonObject;

    /**
     * Default no-arg constructor for the ResponseModel
     */
    public ResponseModel() {
        statusCode = 0;
        headers = null;
        jsonObject = null;
    }

    /**
     * Constructor the the ResponseModel
     * @param statusCode the status code
     * @param headers the HTTP headers
     * @param jsonObject the JSON response
     */
    public ResponseModel(int statusCode, Header[] headers, JSONObject jsonObject) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.jsonObject = jsonObject;
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
    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
