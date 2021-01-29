package com.saltedge.sdk.network;

import androidx.test.runner.AndroidJUnit4;

import com.saltedge.sdk.model.response.ProvidersResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Call;

@RunWith(AndroidJUnit4.class)
public class SERestClientTest {

    @Test
    public void createRetrofitTest() throws Exception {
        SERestClient.getInstance().initService();
        Call<ProvidersResponse> call = SERestClient.getInstance().service.getProviders("", true, "");

        Assert.assertTrue(call.request().url().toString().startsWith("https://www.saltedge.com/api/v5/providers"));
    }
}
