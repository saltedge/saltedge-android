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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saltedge.sdk.network.ExtraJsonDataAdapter;
import com.saltedge.sdk.network.HeaderInterceptor;
import com.saltedge.sdk.preferences.SEPreferencesRepository;

import org.json.JSONObject;

import java.util.Calendar;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestTools {

    public static Retrofit createMockRetrofit(HttpUrl url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(createTestClient())
                .addConverterFactory(GsonConverterFactory.create(createDefaultGson()))
                .build();
    }

    private static Gson createDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(JSONObject.class, new ExtraJsonDataAdapter())
                .create();
    }

    private static OkHttpClient createTestClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor("json"))
                .addInterceptor(prepareLoggingInterceptor())
                .build();
    }

    private static HttpLoggingInterceptor prepareLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    public static void saveValidPinsInPreferences() {
        long data = Calendar.getInstance().getTimeInMillis() + 3600L * 1000L;
        SEPreferencesRepository.getInstance().updatePinsAndMaxAge(new String[]{"sha256/q6syzBRvzhCBHB8F9PJkQHH27U/vG8a7r848qBAv5Yo="}, data);
    }
}
