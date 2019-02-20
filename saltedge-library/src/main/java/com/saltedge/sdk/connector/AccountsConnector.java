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

import com.saltedge.sdk.interfaces.FetchAccountsResult;
import com.saltedge.sdk.model.AccountData;
import com.saltedge.sdk.model.response.AccountsResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountsConnector extends BasePinnedConnector implements Callback<AccountsResponse> {

    private final FetchAccountsResult callback;
    private String nextPageId = "";
    private ArrayList<AccountData> accountsList = new ArrayList<>();
    private String customerSecret = "";
    private String loginSecret = "";

    public AccountsConnector(FetchAccountsResult callback) {
        this.callback = callback;
    }

    public void fetchAccounts(String customerSecret, String loginSecret) {
        this.customerSecret = customerSecret;
        this.loginSecret = loginSecret;
        checkAndLoadPinsOrDoRequest();
    }

    @Override
    void enqueueCall() {
        SERestClient.getInstance().service.getAccounts(customerSecret, loginSecret, nextPageId).enqueue(this);
    }

    @Override
    void onFailure(String errorMessage) {
        if (callback != null) callback.onFailure(errorMessage);
    }

    @Override
    public void onResponse(Call<AccountsResponse> call, Response<AccountsResponse> response) {
        AccountsResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) {
            accountsList.addAll(responseBody.getData());
            nextPageId = responseBody.getMeta().getNextId();
            fetchNextPageOrFinish();
        }
        else onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<AccountsResponse> call, Throwable t) {
        onFailure(SEErrorTools.processConnectionError(t));
    }

    private void fetchNextPageOrFinish() {
        if (nextPageId == null || nextPageId.isEmpty()) {
            Collections.sort(accountsList, new Comparator<AccountData>() {
                @Override
                public int compare(AccountData a1, AccountData a2) {
                    return a1.getName().compareTo(a2.getName());
                }
            });
            callback.onSuccess(accountsList);
        } else enqueueCall();
    }
}