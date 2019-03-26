package com.saltedge.sdk.connector;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.TestTools;
import com.saltedge.sdk.interfaces.ConnectSessionResult;
import com.saltedge.sdk.network.SEApiInterface;
import com.saltedge.sdk.network.SERestClient;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TokenConnectorTest implements ConnectSessionResult {

    @Test
    public void createTokenTest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(successResponse));

        String[] scopes = {"value2", "value3"};
        new ConnectSessionConnector(this).createConnectSession("test_provider_code", scopes, "test_url", "test customer secret");
        doneSignal.await(10, TimeUnit.SECONDS);

        Assert.assertNull(errorMessage);
        assertThat(connectUrl, equalTo("test_connect_url"));

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertFalse(mockRetrofit.baseUrl().toString().isEmpty());
        assertThat(request.getPath(), equalTo("/api/v4/tokens/create"));
    }

    @Test
    public void createTokenWithMapDataTest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(successResponse));

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("provider_code", "test_provider_code");
        String[] scopes = {"scope1", "scope2"};
        dataMap.put("scopes", scopes);
        dataMap.put("return_to", "test_url");
        new ConnectSessionConnector(this).createConnectSession(dataMap, "test customer secret");
        doneSignal.await(5, TimeUnit.SECONDS);

        Assert.assertNull(errorMessage);
        assertThat(connectUrl, equalTo("test_connect_url"));

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertFalse(mockRetrofit.baseUrl().toString().isEmpty());
        assertThat(request.getPath(), equalTo("/api/v4/tokens/create"));
    }

    @Test
    public void createTokenTestWithErrorResult() throws Exception {
        String errorResponse = "{\"error_class\": \"ResourceNotFound\", \"error_message\": \"Resource Not Found\"}";
        mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody(errorResponse));

        String[] scopes = {"value2", "value3"};
        new ConnectSessionConnector(this).createConnectSession("test_provider_code", scopes, "test_url", "test customer secret");
        doneSignal.await(5, TimeUnit.SECONDS);

        assertThat(errorMessage, equalTo("Resource Not Found"));
        Assert.assertNull(connectUrl);

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertFalse(mockRetrofit.baseUrl().toString().isEmpty());
        assertThat(request.getPath(), equalTo("/api/v4/tokens/create"));
    }

    private CountDownLatch doneSignal;
    private MockWebServer mockWebServer;
    private Retrofit mockRetrofit;
    private String connectUrl;
    private String errorMessage;
    private String successResponse = "{\"data\": { \"connect_url\": \"test_connect_url\", \"token\": \"test_token\", \"expires_at\": \"2017-06-13T13:02:06.124Z\"} }";

    @Before
    public void setUp() throws Exception {
        SaltEdgeSDK.getInstance().init(InstrumentationRegistry.getTargetContext(), "testClientId", "testAppSecret");
        TestTools.saveValidPinsInPreferences();
        connectUrl = null;
        errorMessage = null;
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/");
        mockRetrofit = TestTools.createMockRetrofit(url);
        SERestClient.getInstance().service = mockRetrofit.create(SEApiInterface.class);
        doneSignal = new CountDownLatch(1);
    }

    @After
    public void tearDown() throws Exception {
        if (mockWebServer != null) mockWebServer.shutdown();
    }

    @Override
    public void onSuccess(String connectUrl) {
        this.connectUrl = connectUrl;
        doneSignal.countDown();
    }

    @Override
    public void onFailure(String errorMessage) {
        this.errorMessage = errorMessage;
        doneSignal.countDown();
    }
}
