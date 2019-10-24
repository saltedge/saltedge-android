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
import com.saltedge.sdk.preferences.SEPreferencesRepository;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import okhttp3.CertificatePinner;
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

    @NotNull
    private Retrofit createRetrofit() {
        String baseUrl = SEApiConstants.API_BASE_URL + SEApiConstants.BASE_API_PATH;
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @NotNull
    private static Gson createDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(JSONObject.class, new ExtraJsonDataAdapter())
                .create();
    }

    @NotNull
    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(prepareLoggingInterceptor());
        boolean pinsAdded = addCertificatePinner(clientBuilder);
        addSSLSocketFactory(clientBuilder);
        addHeaderInterceptor(clientBuilder, pinsAdded);
        return clientBuilder.build();
    }

    private void addHeaderInterceptor(OkHttpClient.Builder clientBuilder, boolean acceptJson) {
        clientBuilder.addInterceptor(new HeaderInterceptor(acceptJson ? SEApiConstants.MIME_TYPE_JSON : null));
    }

    private boolean addCertificatePinner(OkHttpClient.Builder clientBuilder) {
        try {
            String[] pins = SEPreferencesRepository.getInstance().getPins();
            if (pins != null && pins.length > 0) {
                CertificatePinner.Builder pinnerBuilder = new CertificatePinner.Builder();
                pinnerBuilder.add(SEApiConstants.API_HOST_NAME, pins);
                clientBuilder.certificatePinner(pinnerBuilder.build());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addSSLSocketFactory(OkHttpClient.Builder builder) {
        try {
            TLSSocketFactory factory = new TLSSocketFactory(SaltEdgeSDK.getInstance().getContext());
            builder.sslSocketFactory(factory, factory.getTrustManager());
        } catch (Exception e) {
            Log.e(TAG, "Can't add SSL Socket factory");
            e.printStackTrace();
        }
    }

    private HttpLoggingInterceptor prepareLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(SaltEdgeSDK.isLoggingEnabled()
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }
}