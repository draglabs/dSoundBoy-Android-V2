package com.draglabs.dsoundboy.dsoundboy.Acessories;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by davrukin on 9/11/17.
 * Holds a JSON response that is returned
 */

class Response {

    private int statusCode;
    private Header[] headers;
    private JSONObject jsonObject;

    public Response() {
        statusCode = 0;
        headers = null;
        jsonObject = null;
    }

    public Response(int statusCode, Header[] headers, JSONObject jsonObject) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.jsonObject = jsonObject;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
