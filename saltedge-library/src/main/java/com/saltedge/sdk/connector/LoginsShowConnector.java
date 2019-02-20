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

import com.saltedge.sdk.interfaces.FetchLoginsResult;
import com.saltedge.sdk.model.LoginData;
import com.saltedge.sdk.model.response.LoginResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginsShowConnector extends BasePinnedConnector implements Callback<LoginResponse> {

    private FetchLoginsResult callback;
    private ArrayList<Call<LoginResponse>> callsList;
    private ArrayList<LoginData> loginsList = new ArrayList<>();
    private int resultCount;

    public LoginsShowConnector(FetchLoginsResult callback) {
        this.callback = callback;
    }

    public void fetchLogins(String customerSecret, String[] loginSecretsArray) {
        loginsList = new ArrayList<>();
        callsList = new ArrayList<>();
        for (String loginSecret : loginSecretsArray) {
            callsList.add(SERestClient.getInstance().service.showLogin(customerSecret, loginSecret));
        }
        checkAndLoadPinsOrDoRequest();
    }

    @Override
    void enqueueCall() {
        resultCount = callsList.size();
        for (Call<LoginResponse> call : callsList) {
            call.enqueue(this);
        }
    }

    @Override
    void onFailure(String errorMessage) {
        if (callback != null) callback.onFailure(errorMessage);
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        LoginResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) onSuccess(responseBody.getData());
        else onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        onFailure(SEErrorTools.processConnectionError(t));
    }

    public void cancel() {
        callback = null;
        if (callsList != null) {
            for (Call<LoginResponse> call : callsList) {
                if (callsList != null && !call.isCanceled()) {
                    call.cancel();
                }
            }
        }
        callsList = null;
    }

    private void onSuccess(LoginData data) {
        loginsList.add(data);
        if (loginsList.size() >= resultCount) {
            callback.onSuccess(loginsList);
        }
    }
}
