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
package com.saltedge.sdk;

import android.content.Context;

import com.saltedge.sdk.utils.SEConstants;

public class SaltEdgeSDK {

    private static SaltEdgeSDK instance;
    private static Context context;
    private static String appId;
    private static String appSecret;
    private static boolean loggingEnabled;

    public static SaltEdgeSDK getInstance() {
        if (instance == null) {
            instance = new SaltEdgeSDK();
        }
        return instance;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getAppId() {
        return appId;
    }

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void init(Context context, String clientAppId, String clientAppSecret) {
        init(context, clientAppId, clientAppSecret, false);
    }

    public void init(Context context, String clientAppId, String clientAppSecret, boolean enableLogging) {
        if (clientAppId == null || clientAppId.isEmpty()) {
            throw new RuntimeException(SEConstants.ERROR_CLIENT_APP_ID_IS_NULL);
        }
        if (clientAppSecret == null || clientAppSecret.isEmpty()) {
            throw new RuntimeException(SEConstants.ERROR_CLIENT_APP_SECRET_IS_NULL);
        }
        SaltEdgeSDK.context = context;
        setAppSecret(clientAppSecret);
        setAppId(clientAppId);
        setLoggingEnabled(enableLogging);
    }

    public Context getContext() {
        return context;
    }

    private void setAppSecret(String clientAppSecret) {
        SaltEdgeSDK.appSecret = clientAppSecret;
    }

    private void setAppId(String clientAppId) {
        SaltEdgeSDK.appId = clientAppId;
    }

    private static void setLoggingEnabled(boolean loggingEnabled) {
        SaltEdgeSDK.loggingEnabled = loggingEnabled;
    }
}
