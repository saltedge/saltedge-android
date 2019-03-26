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

import com.saltedge.sdk.interfaces.ConnectSessionResult;
import com.saltedge.sdk.model.request.CreateConnectSessionRequest;
import com.saltedge.sdk.model.request.MappedRequest;
import com.saltedge.sdk.model.request.ConnectSessionRequest;
import com.saltedge.sdk.model.response.CreateConnectSessionResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectSessionConnector extends BasePinnedConnector implements Callback<CreateConnectSessionResponse> {

    private final ConnectSessionResult callback;
    private Call<CreateConnectSessionResponse> call;

    public ConnectSessionConnector(ConnectSessionResult callback) {
        this.callback = callback;
    }

    public void createToken(String providerCode, String[] scopes, String returnTo, String customerSecret) {
        CreateConnectSessionRequest requestData = new CreateConnectSessionRequest(new String[0], providerCode, scopes, returnTo);
        call = SERestClient.getInstance().service.createConnectSession(customerSecret, requestData);
        checkAndLoadPinsOrDoRequest();
    }

    public void createToken(Map<String, Object> dataMap, String customerSecret) {
        MappedRequest requestData = new MappedRequest(dataMap);
        call = SERestClient.getInstance().service.createConnectSession(customerSecret, requestData);
        checkAndLoadPinsOrDoRequest();
    }

    public void reconnectToken(String localeCode,
                               String returnTo,
                               String connectionSecret,
                               String customerSecret,
                               boolean overrideCredentials) {
        ConnectSessionRequest requestData = overrideCredentials
                ? new ConnectSessionRequest(localeCode, returnTo, "override")
                : new ConnectSessionRequest(localeCode, returnTo);
        call = SERestClient.getInstance().service.createConnectSessionForReconnect(customerSecret, connectionSecret, requestData);
        checkAndLoadPinsOrDoRequest();
    }

    public void refreshToken(String localeCode,
                             String returnTo,
                             String connectSecret,
                             String customerSecret) {
        ConnectSessionRequest requestData = new ConnectSessionRequest(localeCode, returnTo);
        call = SERestClient.getInstance().service.createConnectSessionForRefresh(customerSecret, connectSecret, requestData);
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
    public void onResponse(Call<CreateConnectSessionResponse> call, Response<CreateConnectSessionResponse> response) {
        CreateConnectSessionResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) callback.onSuccess(responseBody.getConnectUrl());
        else onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<CreateConnectSessionResponse> call, Throwable t) {
        onFailure(SEErrorTools.processConnectionError(t));
    }
}
