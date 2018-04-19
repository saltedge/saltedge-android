/*
Copyright © 2018 Salt Edge. https://saltedge.com

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

public class ApiConstants {

    //Headers
    static final String KEY_HEADER_ACCEPT = "Accept";
    static final String KEY_HEADER_CONTENT_TYPE = "Content-type";
    static final String KEY_HEADER_CLIENT_APP_ID = "App-id";
    static final String KEY_HEADER_CLIENT_APP_SECRET = "Secret";
    static final String KEY_HEADER_CUSTOMER_SECRET = "Customer-Secret";
    static final String KEY_HEADER_LOGIN_SECRET = "Login-secret";
    static final String MIME_TYPE_JSON = "application/json";

    //PREFIX
    public static final String PREFIX_SALTBRIDGE = "saltbridge://connect/";

    //API urls
    static final String ROOT_HOST_NAME = "www.saltedge.com";
    static final String ROOT_URL = "https://" + ROOT_HOST_NAME;
    private static final String API_VERSION_PATH = "/api/v4";
    static final String API_COUNTRIES_PATH = API_VERSION_PATH + "/countries";
    static final String API_CUSTOMERS_PATH = API_VERSION_PATH + "/customers";
    static final String API_PROVIDERS_PATH = API_VERSION_PATH + "/providers";
    static final String PER_PAGE = API_VERSION_PATH + "/per";
    private static final String API_TOKENS_PATH = API_VERSION_PATH + "/tokens";
    static final String API_LOGIN_PATH = API_VERSION_PATH + "/login";
    static final String API_ACCOUNTS_PATH = API_VERSION_PATH + "/accounts";
    static final String API_TRANSACTIONS_PATH = API_VERSION_PATH + "/transactions";
    static final String API_PENDING_TRANSACTIONS_PATH = API_VERSION_PATH + "/pending";
    static final String API_TOKEN_RECONNECT_PATH = ApiConstants.API_TOKENS_PATH + "/reconnect";
    static final String API_TOKEN_CREATE_PATH = ApiConstants.API_TOKENS_PATH + "/create";
    static final String API_TOKEN_REFRESH_PATH = ApiConstants.API_TOKENS_PATH + "/refresh";

    //SCOPES
    public static final String[] SCOPE_ACCOUNT_TRANSACTIONS = {"accounts", "transactions"};
}
