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
package com.saltedge.sdk.sample.utils;

import com.saltedge.sdk.model.SEConsent;

public class Constants {

    static String CONNECTIONS_SECRETS_ARRAY = "CONNECTIONS_SECRETS_ARRAY";
    public static final String KEY_CUSTOMER_SECRET = "secret";
    public static final String KEY_CUSTOMER_IDENTIFIER = "identifier";
    public static final String KEY_CONNECTION = "connection";
    public static final String KEY_CONNECTION_SECRET = "connection_secret";
    public static final String KEY_REFRESH = "refresh";
    public static final String KEY_OVERRIDE_CREDENTIALS = "override_credentials";
    public static final String KEY_OAUTH = "oauth";
    public static final String[] CONSENT_SCOPES = { SEConsent.SCOPE_ACCOUNT_DETAILS, SEConsent.SCOPE_TRANSACTIONS_DETAILS };
}
