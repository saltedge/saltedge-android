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

import com.saltedge.sdk.interfaces.TokenConnectionResult;
import com.saltedge.sdk.model.request.CreateTokenRequest;
import com.saltedge.sdk.model.request.MappedRequest;
import com.saltedge.sdk.model.request.TokenRequest;
import com.saltedge.sdk.model.response.CreateTokenResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenConnector extends BasePinnedConnector implements Callback<CreateTokenResponse> {

    private final TokenConnectionResult callback;
    private Call<CreateTokenResponse> call;

    public TokenConnector(TokenConnectionResult callback) {
        this.callback = callback;
    }

    public void createToken(String providerCode, String[] scopes, String returnTo, String customerSecret) {
        CreateTokenRequest requestData = new CreateTokenRequest(new String[0], providerCode, scopes, returnTo);
        call = SERestClient.getInstance().service.createToken(customerSecret, requestData);
        checkAndLoadPinsOrDoRequest();
    }

    public void createToken(Map<String, Object> dataMap, String customerSecret) {
        MappedRequest requestData = new MappedRequest(dataMap);
        call = SERestClient.getInstance().service.createToken(customerSecret, requestData);
        checkAndLoadPinsOrDoRequest();
    }

    public void reconnectToken(String locale, String returnTo, String loginSecret, String customerSecret) {
        TokenRequest requestData = new TokenRequest(locale, returnTo);
        call = SERestClient.getInstance().service.reconnectToken(customerSecret, loginSecret, requestData);
        checkAndLoadPinsOrDoRequest();
    }

    public void refreshToken(String locale, String returnTo, String loginSecret, String customerSecret) {
        TokenRequest requestData = new TokenRequest(locale, returnTo);
        call = SERestClient.getInstance().service.refreshToken(customerSecret, loginSecret, requestData);
        checkAndLoadPinsOrDoRequest();
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
    public void onResponse(Call<CreateTokenResponse> call, Response<CreateTokenResponse> response) {
        CreateTokenResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) callback.onSuccess(responseBody.getConnectUrl());
        else onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<CreateTokenResponse> call, Throwable t) {
        onFailure(SEErrorTools.processConnectionError(t));
    }
}
