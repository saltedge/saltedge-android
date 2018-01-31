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

import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.model.request.CreateTokenRequest;
import com.saltedge.sdk.model.response.CreateTokenResponse;
import com.saltedge.sdk.model.request.TokenRequest;
import com.saltedge.sdk.utils.SEJsonTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenConnector implements Callback<CreateTokenResponse> {

    private final Result callback;

    public TokenConnector(Result callback) {
        this.callback = callback;
    }

    public void createToken(String providerCode, String[] scopes, String returnTo, String customerSecret) {
        CreateTokenRequest request = new CreateTokenRequest(new String[0], providerCode, scopes, returnTo);
        SERestClient.getInstance().service.createToken(customerSecret, request).enqueue(this);
    }

    public void reconnectToken(String locale, String returnTo, String loginSecret, String customerSecret) {
        TokenRequest request = new TokenRequest(locale, returnTo);
        SERestClient.getInstance().service.reconnectToken(customerSecret, loginSecret, request).enqueue(this);
    }

    public void refreshToken(String locale, String returnTo, String loginSecret, String customerSecret) {
        TokenRequest request = new TokenRequest(locale, returnTo);
        SERestClient.getInstance().service.refreshToken(customerSecret, loginSecret, request).enqueue(this);
    }

    @Override
    public void onResponse(Call<CreateTokenResponse> call, Response<CreateTokenResponse> response) {
        CreateTokenResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) callback.onSuccess(responseBody.getConnectUrl());
        else callback.onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<CreateTokenResponse> call, Throwable t) {
        callback.onFailure(t.getMessage());
    }

    public interface Result {
        void onSuccess(String connectUrl);
        void onFailure(String errorMessage);
    }
}