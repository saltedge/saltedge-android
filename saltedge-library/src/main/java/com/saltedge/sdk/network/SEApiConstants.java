/*
Copyright © 2019 Salt Edge. https://saltedge.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.saltedge.sdk.network;

import com.saltedge.sdk.utils.SEConstants;

public class SEApiConstants {

    //Headers
    static final String KEY_HEADER_ACCEPT = "Accept";
    static final String KEY_HEADER_CONTENT_TYPE = "Content-type";
    static final String KEY_HEADER_APP_ID = "App-id";
    static final String KEY_HEADER_APP_SECRET = "Secret";
    static final String KEY_HEADER_CUSTOMER_SECRET = "Customer-Secret";
    static final String KEY_HEADER_CONNECTION_SECRET = "Connection-secret";
    static final String MIME_TYPE_JSON = "application/json";

    //PREFIX
    public static final String PREFIX_SALTBRIDGE = "saltbridge://connect/";

    //API urls
    static final String API_HOST_NAME = "www.saltedge.com";
    public static final String API_BASE_URL = "https://" + API_HOST_NAME + "/";
    private static final String API_VERSION_PATH = "api/v5";
    public static final String API_COUNTRIES_PATH = API_VERSION_PATH + "/countries";
    static final String API_CUSTOMERS_PATH = API_VERSION_PATH + "/customers";
    static final String API_PROVIDERS_PATH = API_VERSION_PATH + "/providers";
    private static final String API_CONNECT_SESSIONS_PATH = API_VERSION_PATH + "/connect_sessions";
    static final String API_CONNECT_SESSION_CREATE_PATH = API_CONNECT_SESSIONS_PATH + "/create";
    static final String API_CONNECT_SESSION_RECONNECT_PATH = API_CONNECT_SESSIONS_PATH + "/reconnect";
    static final String API_CONNECT_SESSION_REFRESH_PATH = API_CONNECT_SESSIONS_PATH + "/refresh";
    static final String API_CONNECTION_PATH = API_VERSION_PATH + "/connection";
    static final String API_CONNECTION_REFRESH_PATH = API_CONNECTION_PATH + "/refresh";
    static final String API_CONNECTION_INTERACTIVE_PATH = API_CONNECTION_PATH + "/interactive";
    static final String API_ACCOUNTS_PATH = API_VERSION_PATH + "/accounts";
    static final String API_TRANSACTIONS_PATH = API_VERSION_PATH + "/transactions";
    static final String API_PENDING_TRANSACTIONS_PATH = API_TRANSACTIONS_PATH + "/pending";
    static final String API_CONSENTS_PATH = API_VERSION_PATH + "/consents";
    static final String API_CONSENT_REVOKE_PATH = API_CONSENTS_PATH + "/{"  + SEConstants.KEY_CONSENT_ID + "}/revoke";
}
