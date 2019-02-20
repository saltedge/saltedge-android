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

import com.saltedge.sdk.interfaces.FetchLoginResult;
import com.saltedge.sdk.model.request.PutLoginCredentialsRequest;
import com.saltedge.sdk.model.response.LoginResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginInteractiveCredentialsConnector extends BasePinnedConnector implements Callback<LoginResponse> {

    private FetchLoginResult callback;
    private Call<LoginResponse> call;

    public LoginInteractiveCredentialsConnector(FetchLoginResult callback) {
        this.callback = callback;
    }

    public void sendLoginCredentials(String customerSecret, String loginSecret, Map<String, Object> credentials) {
        PutLoginCredentialsRequest requestData = new PutLoginCredentialsRequest(credentials);
        call = SERestClient.getInstance().service.putInteractiveCredentials(customerSecret, loginSecret, requestData);
        checkAndLoadPinsOrDoRequest();
    }

    public void cancel() {
        callback = null;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
        call = null;
    }

    @Override
    void enqueueCall() {
        if (call != null) call.enqueue(this);
    }

    @Override
    void onFailure(String errorMessage) {
        if (callback != null) callback.onFailure(errorMessage);
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        LoginResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) callback.onSuccess(responseBody.getData());
        else onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        onFailure(SEErrorTools.processConnectionError(t));
    }
}
