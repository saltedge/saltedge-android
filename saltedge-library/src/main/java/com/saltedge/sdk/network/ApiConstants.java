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

    public static final int HTTP_PORT = 80;

    //Headers
    public static final String KEY_HEADER_APP_SECRET = "App-secret";
    public static final String KEY_HEADER_CLIENT_ID = "Client-Id";
    public static final String KEY_HEADER_CUSTOMER_SECRET = "Customer-Secret";
    public static final String KEY_CUSTOMER_SECRET = "secret";
    public static final String KEY_HEADER_LOGIN_SECRET = "Login-secret";
    public static final String MIME_TYPE_JSON = "application/json";

    //PREFIX
    public static final String PREFIX_SALTBRIDGE = "saltbridge://connect/";
    public static final String PREFIX_FROM = "?from_id=";

    //API urls
    public static final String ROOT_URL = "https://www.saltedge.com/api/v3";
    public static final String CUSTOMERS_URL = "/customers";
    public static final String PROVIDERS_URL = "/providers";
    public static final String PER_PAGE = "/per";
    public static final String TOKENS_URL = "/tokens";
    public static final String LOGIN_URL = "/login";
    public static final String ACCOUNTS_URL = "/accounts";
    public static final String TRANSACTIONS_URL = "/transactions";
    public static final String PENDING_TRANSACTIONS_URL = "/pending";

    //Login tails
    public static final String TAIL_RECONNECT = "/reconnect";
    public static final String TAIL_CREATE = "/create";
    public static final String TAIL_REFRESH = "/refresh";
}
