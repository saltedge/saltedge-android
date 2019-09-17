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
import android.util.Log;

import com.saltedge.sdk.utils.SEConstants;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class for initializing SDK for access to Salt Edge API or Salt Edge Partner API
 */
public class SaltEdgeSDK {

    private static SaltEdgeSDK instance;
    private Context applicationContext;
    private String appId;
    private String appSecret;
    private String returnToUrl;
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

    public static String getReturnToUrl() {
        return getInstance().returnToUrl;
    }

    public static void setReturnToUrl(String returnToUrl) {
        getInstance().returnToUrl = returnToUrl;
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
        return applicationContext;
    }

    /**
     * Initialize Salt Edge SDK for access to Salt Edge API
     *
     * @param applicationContext Application context
     * @param clientAppId unique app id
     * @param clientAppSecret unique ap secret
     * @param returnToUrl the URL the user will be redirected to. The return_to URL should not exceed 2040 characters.
     */
    public void init(Context applicationContext,
                     @NotNull String clientAppId,
                     @NotNull String clientAppSecret,
                     @NotNull String returnToUrl
    ) {
        init(applicationContext, clientAppId, clientAppSecret, returnToUrl, false);
    }

    /**
     * Initialize Salt Edge SDK for access to Salt Edge API
     *
     * @param applicationContext Application context
     * @param clientAppId unique app id
     * @param clientAppSecret unique ap secret
     * @param enableLogging flag, which enable or disable network logging
     * @param returnToUrl the URL the user will be redirected to. The return_to URL should not exceed 2040 characters.
     */
    public void init(Context applicationContext,
                     @NotNull String clientAppId,
                     @NotNull String clientAppSecret,
                     @NotNull String returnToUrl,
                     boolean enableLogging
    ) {
        init(applicationContext, clientAppId, clientAppSecret, returnToUrl, false, enableLogging);
    }

    /**
     * Initialize Salt Edge SDK for access to Salt Edge Partner API
     *
     * @param applicationContext Application context
     * @param clientAppId unique app id
     * @param clientAppSecret unique ap secret
     */
    public void initPartner(Context applicationContext,
                            @NotNull String clientAppId,
                            @NotNull String clientAppSecret
    ) {
        initPartner(applicationContext, clientAppId, clientAppSecret, false);
    }

    /**
     * Initialize Salt Edge SDK for access to Salt Edge Partner API
     *
     * @param applicationContext Application context
     * @param clientAppId unique app id
     * @param clientAppSecret unique ap secret
     * @param enableLogging flag, which enable or disable network logging
     */
    public void initPartner(Context applicationContext,
                            @NotNull String clientAppId,
                            @NotNull String clientAppSecret,
                            boolean enableLogging
    ) {
        init(applicationContext, clientAppId, clientAppSecret, null, true, enableLogging);
    }

    private void init(Context applicationContext,
                      @NotNull String clientAppId,
                      @NotNull String clientAppSecret,
                      String returnToUrl,
                      boolean actAsPartner,
                      boolean enableLogging
    ) {
        if (clientAppId.isEmpty()) {
            throw new RuntimeException(SEConstants.ERROR_CLIENT_APP_ID_IS_NULL);
        }
        if (clientAppSecret.isEmpty()) {
            throw new RuntimeException(SEConstants.ERROR_CLIENT_APP_SECRET_IS_NULL);
        }
        this.applicationContext = applicationContext;
        this.appId = clientAppId;
        this.appSecret = clientAppSecret;
        this.usePartnersApi = actAsPartner;
        this.loggingEnabled = enableLogging;
        this.returnToUrl = returnToUrl;
    }

    public static void printToLogcat(@NotNull String tag, @NotNull String message) {
        if (isLoggingEnabled()) {
            Log.d(tag, message);
        }
    }
}
