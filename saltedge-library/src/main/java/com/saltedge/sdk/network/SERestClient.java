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
package com.saltedge.sdk.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.utils.SEConstants;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class SERestClient {

    public static final int DEFAULT_MAX_RETRIES = 2;
    public static final int DEFAULT_TIMEOUT = 2000;
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;

    public static boolean get(String servicePath,
                              SEHTTPResponseHandler responseHandler,
                              HashMap<String, String> headers) {
        if (isNetworkUnavailable() || responseHandler == null) {
            return false;
        }
        AsyncHttpClient client = createHttpClient(headers);
        RequestHandle handler = client.get(SaltEdgeSDK.getInstance().getContext(),
                getAbsoluteUrl(servicePath),
                responseHandler);
        return (handler != null);
    }

    public static boolean post(String servicePath,
                               String jsonRequest,
                               SEHTTPResponseHandler responseHandler,
                               HashMap<String, String> headers) {
        if (isNetworkUnavailable() || responseHandler == null) {
            return false;
        }
        AsyncHttpClient client = createHttpClient(headers);
        RequestHandle handler = null;
        try {
            Log.v("tag", "path " + getAbsoluteUrl(servicePath));
            Log.v("tag", "jsonRequest.toString() " + jsonRequest.toString());
            Log.v("tag", "SEConstants.MIME_TYPE_JSON " + SEConstants.MIME_TYPE_JSON);

            handler = client.post(SaltEdgeSDK.getInstance().getContext(),
                    getAbsoluteUrl(servicePath),
                    new StringEntity(jsonRequest.toString(), "UTF-8"),
                    SEConstants.MIME_TYPE_JSON,
                    responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        return (handler != null);
    }

    public static boolean delete(String servicePath,
                                 SEHTTPResponseHandler responseHandler,
                                 HashMap<String, String> headers) {
        if (isNetworkUnavailable() || responseHandler == null) {
            return false;
        }
        AsyncHttpClient client = createHttpClient(headers);
        RequestHandle handler = client.delete(SaltEdgeSDK.getInstance().getContext(),
                getAbsoluteUrl(servicePath),
                responseHandler);
        return (handler != null);
    }

    private static AsyncHttpClient createHttpClient(HashMap<String, String> headers) {
        AsyncHttpClient client = isHttpsPrefixInRootUrl()
                ? new AsyncHttpClient(true, DEFAULT_HTTP_PORT, DEFAULT_HTTPS_PORT)
                : new AsyncHttpClient(SEConstants.HTTP_PORT);
        client.setMaxRetriesAndTimeout(DEFAULT_MAX_RETRIES, DEFAULT_TIMEOUT);
        for (HashMap.Entry<String, String> entry : headers.entrySet()) {
            client.addHeader(entry.getKey(), entry.getValue());
        }
        return client;
    }

    /**
     * Convert relative URL to absolute URL.
     *
     * @param relativeUrl
     * @return absolute URL
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        if (relativeUrl == null) {
            relativeUrl = "";
        }
        return SEConstants.ROOT_URL + relativeUrl;
    }

    private static boolean isHttpsPrefixInRootUrl() {
        return SEConstants.ROOT_URL.startsWith("https://");
    }

    /**
     * Check if networks are available.
     *
     * @return true if networks are available, false if not
     */
    private static boolean isNetworkUnavailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) SaltEdgeSDK.getInstance().getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo == null || !activeNetworkInfo.isConnected());
    }

}