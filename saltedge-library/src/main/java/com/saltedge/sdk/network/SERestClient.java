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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saltedge.sdk.BuildConfig;
import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.preferences.SEPreferencesRepository;

import org.json.JSONObject;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SERestClient {

    private static final int DEFAULT_MAX_RETRIES = 2;
    private static final int DEFAULT_TIMEOUT = 2000;
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;

    public ApiInterface service = createRetrofit().create(ApiInterface.class);
    private static SERestClient instance;

    public static SERestClient getInstance() {
        if (instance == null) {
            instance = new SERestClient();
        }
        return instance;
    }

    public void initService() {
        service = createRetrofit().create(ApiInterface.class);
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(getApiBaseUrl())
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(createDefaultGson()))
                .build();
    }

    private Gson createDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(JSONObject.class, new ExtraJsonDataAdapter())
                .create();
    }

    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(prepareLoginInterceptor())
                .certificatePinner(createCertificatePinner())
                .build();
    }

    private CertificatePinner createCertificatePinner() {
        CertificatePinner.Builder pinnerBuilder = new CertificatePinner.Builder();
        try {
            String[] pins = SEPreferencesRepository.getInstance().getPins();
            if (pins != null && pins.length > 0) {
                pinnerBuilder.add(ApiConstants.ROOT_HOST_NAME, pins);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pinnerBuilder.build();
    }

    private HttpLoggingInterceptor prepareLoginInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }

    private String getApiBaseUrl() {
        int port = isHttpsPrefixInRootUrl() ? DEFAULT_HTTPS_PORT : DEFAULT_HTTP_PORT;
        return ApiConstants.ROOT_URL + ":" + String.valueOf(port);
    }

    private static boolean isHttpsPrefixInRootUrl() {
        return ApiConstants.ROOT_URL.startsWith("https://");
    }

    /**
     * Check if networks are available.
     *
     * @return true if networks are available, false if not
     */
    private static boolean isNetworkUnavailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) SaltEdgeSDK.getInstance().getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return (activeNetworkInfo == null || !activeNetworkInfo.isConnected());
    }
}