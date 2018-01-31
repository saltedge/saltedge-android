/*
Copyright © 2015 Salt Edge. https://saltedge.com

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
    private static String clientId;
    private static String appSecret;
    private static Context context;

    public static SaltEdgeSDK getInstance() {
        if (instance == null) {
            instance = new SaltEdgeSDK();
        }
        return instance;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void init(Context context, String clientId, String appSecret) {
        if (clientId == null || clientId.isEmpty()) {
            throw new RuntimeException(SEConstants.ERROR_CLIENT_ID_IS_NULL);
        }
        if (appSecret == null || appSecret.isEmpty()) {
            throw new RuntimeException(SEConstants.ERROR_APP_SECRET_IS_NULL);
        }
        SaltEdgeSDK.context = context;
        setAppSecret(appSecret);
        setClientId(clientId);
    }

    public Context getContext() {
        return context;
    }

    private void setAppSecret(String appSecret) {
        SaltEdgeSDK.appSecret = appSecret;
    }

    private void setClientId(String cliendId) {
        SaltEdgeSDK.clientId = cliendId;
    }
}
