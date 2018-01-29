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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.saltedge.sdk.network.ApiConstants;
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

    private void setAppSecret(String appSecret) {
        SaltEdgeSDK.appSecret = appSecret;
    }

    private void setClientId(String cliendId) {
        SaltEdgeSDK.clientId = cliendId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setContext(Context context) {
        SaltEdgeSDK.context = context;
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appInfo != null) {
            Bundle bundle = appInfo.metaData;
            setAppSecret(bundle.getString(ApiConstants.KEY_HEADER_APP_SECRET));
            setClientId(bundle.getString(ApiConstants.KEY_HEADER_CLIENT_ID));
        }
        if (getClientId() == null) {
            throw new RuntimeException(SEConstants.CLIENT_ID_IS_NULL);
        }
        if (getAppSecret() == null) {
            throw new RuntimeException(SEConstants.APP_SECRET_IS_NULL);
        }

    }

    public Context getContext() {
        return context;
    }
}
