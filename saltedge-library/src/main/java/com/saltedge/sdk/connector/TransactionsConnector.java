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

import com.saltedge.sdk.interfaces.FetchTransactionsResult;
import com.saltedge.sdk.model.TransactionData;
import com.saltedge.sdk.model.response.TransactionsResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsConnector extends BasePinnedConnector implements Callback<TransactionsResponse> {

    private final FetchTransactionsResult callback;
    private ArrayList<TransactionData> transactionsList = new ArrayList<>();
    private String loginSecret = "";
    private String customerSecret = "";
    private String accountId = "";
    private String fromId = "";

    public TransactionsConnector(FetchTransactionsResult callback) {
        this.callback = callback;
    }

    public void fetchTransactions(String customerSecret, String loginSecret, String accountId, String fromId) {
        this.loginSecret = loginSecret;
        this.customerSecret = customerSecret;
        this.accountId = accountId;
        this.fromId = fromId;
        checkAndLoadPinsOrDoRequest();
    }

    @Override
    void enqueueCall() {
        SERestClient.getInstance().service.getTransactions(customerSecret, loginSecret, accountId, fromId)
                .enqueue(this);
    }

    @Override
    void onFailure(String errorMessage) {
        if (callback != null) callback.onFailure(errorMessage);
    }

    @Override
    public void onResponse(Call<TransactionsResponse> call, Response<TransactionsResponse> response) {
        TransactionsResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) {
            List<TransactionData> newTransactions = responseBody.getData();
            if (newTransactions != null) transactionsList.addAll(newTransactions);
            fromId = responseBody.getMeta().getNextId();
            fetchNextPageOrFinish();
        }
        else onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<TransactionsResponse> call, Throwable t) {
        onFailure(SEErrorTools.processConnectionError(t));
    }

    private void fetchNextPageOrFinish() {
        if (fromId == null || fromId.isEmpty()) {
            callback.onSuccess(transactionsList);
        } else enqueueCall();
    }
}