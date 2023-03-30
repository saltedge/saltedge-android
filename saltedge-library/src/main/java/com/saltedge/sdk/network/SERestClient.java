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
package com.saltedge.sdk.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saltedge.sdk.SaltEdgeSDK;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SERestClient {

    private SERestClient() {}

    private static final String TAG = "SERestClient";

    public SEApiInterface service = createRetrofit().create(SEApiInterface.class);
    public static Gson gson = createDefaultGson();
    private static SERestClient instance;

    public static SERestClient getInstance() {
        if (instance == null) {
            instance = new SERestClient();
        }
        return instance;
    }

    public void initService() {
        service = createRetrofit().create(SEApiInterface.class);
    }

    private Retrofit createRetrofit() {
        String baseUrl = SEApiConstants.API_BASE_URL + SEApiConstants.BASE_API_PATH;
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private static Gson createDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(JSONObject.class, new ExtraJsonDataAdapter())
                .create();
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(prepareLoggingInterceptor());
        addHeaderInterceptor(clientBuilder);
        return clientBuilder.build();
    }

    private void addHeaderInterceptor(OkHttpClient.Builder clientBuilder) {
        clientBuilder.addInterceptor(new HeaderInterceptor(SEApiConstants.MIME_TYPE_JSON));
    }

    private HttpLoggingInterceptor prepareLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(SaltEdgeSDK.isLoggingEnabled()
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }
}