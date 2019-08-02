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
package com.saltedge.sdk.connector;

import com.saltedge.sdk.interfaces.UpdateTransactionsResult;
import com.saltedge.sdk.model.request.DeleteTransactionsRequest;
import com.saltedge.sdk.model.request.PutTransactionsIdsRequest;
import com.saltedge.sdk.model.response.UpdateTransactionsResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsUpdateConnector extends BasePinnedConnector implements Callback<UpdateTransactionsResponse> {

    public enum RequestType {
        DUPLICATE, UNDUPLICATE, CLEANUP
    }

    private final UpdateTransactionsResult callback;
    private String customerSecret = "";
    private String connectionSecret = "";
    private PutTransactionsIdsRequest transactionsIdsRequestBody;
    private DeleteTransactionsRequest deleteTransactionsRequestBody;
    private RequestType requestType = RequestType.DUPLICATE;

    public TransactionsUpdateConnector(UpdateTransactionsResult callback) {
        this.callback = callback;
    }

    public void markTransactionsAsDuplicated(String customerSecret,
                                             @NotNull String connectionSecret,
                                             @NotNull List<String> transactionsIds
    ) {
        requestType = RequestType.DUPLICATE;
        this.customerSecret = customerSecret;
        this.connectionSecret = connectionSecret;
        this.transactionsIdsRequestBody = new PutTransactionsIdsRequest(transactionsIds);

        checkAndLoadPinsOrDoRequest();
    }

    public void markTransactionsAsNotDuplicated(String customerSecret,
                                                @NotNull String connectionSecret,
                                                @NotNull List<String> transactionsIds
    ) {
        requestType = RequestType.UNDUPLICATE;
        this.customerSecret = customerSecret;
        this.connectionSecret = connectionSecret;
        this.transactionsIdsRequestBody = new PutTransactionsIdsRequest(transactionsIds);

        checkAndLoadPinsOrDoRequest();
    }

    public void removeTransactions(String customerSecret,
                                   @NotNull String connectionSecret,
                                   @NotNull String accountId,
                                   int keepDays
    ) {
        requestType = RequestType.CLEANUP;
        this.customerSecret = customerSecret;
        this.connectionSecret = connectionSecret;
        this.deleteTransactionsRequestBody = new DeleteTransactionsRequest(accountId, keepDays);

        checkAndLoadPinsOrDoRequest();
    }

    @Override
    void enqueueCall() {
        switch (requestType) {
            case DUPLICATE:
                SERestClient.getInstance().service
                        .putDuplicatedTransactions(customerSecret, connectionSecret, transactionsIdsRequestBody)
                        .enqueue(this);
                break;
            case UNDUPLICATE:
                SERestClient.getInstance().service
                        .putUnduplicatedTransactions(customerSecret, connectionSecret, transactionsIdsRequestBody)
                        .enqueue(this);
                break;
            case CLEANUP:
                SERestClient.getInstance().service
                        .deleteTransactions(customerSecret, connectionSecret, deleteTransactionsRequestBody)
                        .enqueue(this);
                break;
        }
    }

    @Override
    void onFailure(String errorMessage) {
        if (callback != null) callback.onUpdateTransactionsFailure(errorMessage);
    }

    @Override
    public void onResponse(Call<UpdateTransactionsResponse> call, Response<UpdateTransactionsResponse> response) {
        UpdateTransactionsResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null && callback != null) {
            if (responseBody.isDuplicated() != null) {
                callback.onUpdateTransactionsSuccess(responseBody.isDuplicated(), requestType.toString());
            } else if (responseBody.isUnduplicated() != null) {
                callback.onUpdateTransactionsSuccess(responseBody.isUnduplicated(), requestType.toString());
            } else if (responseBody.isCleanupStarted() != null) {
                callback.onTransactionsCleanupStartedSuccess(responseBody.isCleanupStarted());
            } else {
                callback.onUpdateTransactionsSuccess(null, requestType.toString());
            }
        }
        else onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<UpdateTransactionsResponse> call, Throwable t) {
        onFailure(SEErrorTools.processConnectionError(t));
    }
}
