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

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.saltedge.sdk.utils.SEConstants;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class for initializing SDK for access to Salt Edge API or Salt Edge Partner API
 */
public class SaltEdgeSDK {

    private static SaltEdgeSDK instance;
    private Application application;
    private String appId;
    private String appSecret;
    private boolean usePartnersApi;
    private boolean loggingEnabled;

    public static SaltEdgeSDK getInstance() {
        if (instance == null) {
            instance = new SaltEdgeSDK();
        }
        return instance;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public static boolean isPartner() {
        return getInstance().usePartnersApi;
    }

    public static boolean isNotPartner() {
        return !isPartner();
    }

    public static boolean isLoggingEnabled() {
        return getInstance().loggingEnabled;
    }

    public Context getContext() {
        return application.getApplicationContext();
    }

    /**
     * Initialize Salt Edge SDK for access to Salt Edge API
     *
     * @param application Application instance
     * @param clientAppId unique app id
     * @param clientAppSecret unique ap secret
     */
    public void init(@NotNull Application application,
                     @NotNull String clientAppId,
                     @NotNull String clientAppSecret
    ) {
        init(application, clientAppId, clientAppSecret, false);
    }

    /**
     * Initialize Salt Edge SDK for access to Salt Edge API
     *
     * @param application Application instance
     * @param clientAppId unique app id
     * @param clientAppSecret unique ap secret
     * @param enableLogging flag, which enable or disable network logging
     */
    public void init(@NotNull Application application,
                     @NotNull String clientAppId,
                     @NotNull String clientAppSecret,
                     boolean enableLogging
    ) {
        init(application, clientAppId, clientAppSecret, false, enableLogging);
    }

    /**
     * Initialize Salt Edge SDK for access to Salt Edge Partner API
     *
     * @param application Application instance
     * @param clientAppId unique app id
     * @param clientAppSecret unique ap secret
     */
    public void initPartner(@NotNull Application application,
                            @NotNull String clientAppId,
                            @NotNull String clientAppSecret
    ) {
        initPartner(application, clientAppId, clientAppSecret, false);
    }

    /**
     * Initialize Salt Edge SDK for access to Salt Edge Partner API
     *
     * @param application Application instance
     * @param clientAppId unique app id
     * @param clientAppSecret unique ap secret
     * @param enableLogging flag, which enable or disable network logging
     */
    public void initPartner(@NotNull Application application,
                            @NotNull String clientAppId,
                            @NotNull String clientAppSecret,
                            boolean enableLogging
    ) {
        init(application, clientAppId, clientAppSecret, true, enableLogging);
    }

    private void init(@NotNull Application application,
                     @NotNull String clientAppId,
                     @NotNull String clientAppSecret,
                     boolean actAsPartner,
                     boolean enableLogging
    ) {
        if (clientAppId.isEmpty()) {
            throw new RuntimeException(SEConstants.ERROR_CLIENT_APP_ID_IS_NULL);
        }
        if (clientAppSecret.isEmpty()) {
            throw new RuntimeException(SEConstants.ERROR_CLIENT_APP_SECRET_IS_NULL);
        }
        this.application = application;
        this.appId = clientAppId;
        this.appSecret = clientAppSecret;
        this.usePartnersApi = actAsPartner;
        this.loggingEnabled = enableLogging;
    }

    public static void printToLogcat(@NotNull String tag, @NotNull String message) {
        if (isLoggingEnabled()) {
            Log.d(tag, message);
        }
    }
}
