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
package com.saltedge.sdk.connector;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.TestTools;
import com.saltedge.sdk.interfaces.FetchTransactionsResult;
import com.saltedge.sdk.model.TransactionData;
import com.saltedge.sdk.network.ApiInterface;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.preferences.SEPreferencesRepository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
public class TransactionConnectorTest implements FetchTransactionsResult {

    @Test
    public void fetchTransactionsTest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(successResponse));

        new TransactionsConnector(this).fetchTransactions("test customer secret", "1", "2");
        doneSignal.await(5, TimeUnit.SECONDS);
        TransactionData transaction = transactionsList.get(0);

        Assert.assertNull(errorMessage);
        assertThat(transactionsList.size(), equalTo(1));
        assertThat(transaction.getId(), equalTo("3"));
        assertThat(transaction.getAccountId(), equalTo("2"));
        Assert.assertFalse(transaction.isDuplicated());
        assertThat(transaction.getMode(), equalTo("normal"));
        assertThat(transaction.getStatus(), equalTo("posted"));
        assertThat(transaction.getMadeOn(), equalTo("2018-04-01"));
        assertThat(transaction.getAmount(), equalTo(100.0));
        assertThat(transaction.getCurrencyCode(), equalTo("EUR"));
        assertThat(transaction.getDescription(), equalTo("Income for MasterCard"));
        assertThat(transaction.getCategory(), equalTo("transfer"));
        assertThat(transaction.getExtra().length(), equalTo(1));
        assertThat(transaction.getExtra().getDouble("categorization_confidence"), equalTo(1.0));

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertFalse(mockRetrofit.baseUrl().toString().isEmpty());
        assertThat(request.getPath(), equalTo("/api/v4/transactions?account_id=2&from_id="));
    }

    private CountDownLatch doneSignal;
    private MockWebServer mockWebServer;
    private Retrofit mockRetrofit;
    private List<TransactionData> transactionsList;
    private String errorMessage;
    private String successResponse = "{\"data\":[{\"id\":\"3\",\"account_id\":\"2\",\"duplicated\":false,\"mode\":\"normal\",\"status\":\"posted\",\"made_on\":\"2018-04-01\",\"amount\":100.0,\"currency_code\":\"EUR\",\"description\":\"Income for MasterCard\",\"category\":\"transfer\",\"extra\":{\"categorization_confidence\":1.0},\"created_at\":\"2018-05-28T08:07:07Z\",\"updated_at\":\"2018-05-28T08:07:07Z\"}],\"meta\":{\"next_id\":null,\"next_page\":null}}";

    @Before
    public void setUp() throws Exception {
        SaltEdgeSDK.getInstance().init(InstrumentationRegistry.getTargetContext(), "testClientId", "testAppSecret");
        TestTools.saveValidPinsInPreferences();
        transactionsList = null;
        errorMessage = null;
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/");
        mockRetrofit = TestTools.createMockRetrofit(url);
        SERestClient.getInstance().service = mockRetrofit.create(ApiInterface.class);
        doneSignal = new CountDownLatch(1);
    }

    @After
    public void tearDown() throws Exception {
        if (mockWebServer != null) mockWebServer.shutdown();
    }

    @Override
    public void onSuccess(ArrayList<TransactionData> transactionsList) {
        this.transactionsList = transactionsList;
        doneSignal.countDown();
    }

    @Override
    public void onFailure(String errorMessage) {
        this.errorMessage = errorMessage;
        doneSignal.countDown();
    }
}
