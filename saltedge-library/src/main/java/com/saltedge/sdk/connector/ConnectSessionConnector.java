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

import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.interfaces.ConnectSessionResult;
import com.saltedge.sdk.model.SEAttempt;
import com.saltedge.sdk.model.SEConsent;
import com.saltedge.sdk.model.request.ConnectSessionRequest;
import com.saltedge.sdk.model.request.MappedRequest;
import com.saltedge.sdk.model.response.ConnectSessionResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEConstants;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectSessionConnector implements Callback<ConnectSessionResponse> {

    private final ConnectSessionResult callback;
    private Call<ConnectSessionResponse> call;

    public ConnectSessionConnector(ConnectSessionResult callback) {
        this.callback = callback;
    }

    public void createConnectSession(
            String customerSecret,
            String providerCode,
            String[] consentScopes,
            String localeCode
    ) {
        ConnectSessionRequest requestData = new ConnectSessionRequest(
                new SEConsent(consentScopes),
                new SEAttempt(localeCode, SaltEdgeSDK.getReturnToUrl()),
                providerCode,
                SEConstants.IFRAME,
                null
        );
        call = SERestClient.getInstance().service.createConnectSession(customerSecret, requestData);
        if (call != null) call.enqueue(this);
    }

    public void createConnectSession(String customerSecret, Map<String, Object> dataMap) {
        MappedRequest requestData = new MappedRequest(dataMap);
        call = SERestClient.getInstance().service.createConnectSession(customerSecret, requestData);
        if (call != null) call.enqueue(this);
    }

    public void createReconnectSession(
            String customerSecret,
            String connectionSecret,
            String[] consentScopes,
            String localeCode,
            boolean overrideCredentials
    ) {
        ConnectSessionRequest requestData = new ConnectSessionRequest(
                new SEConsent(consentScopes),
                new SEAttempt(localeCode, SaltEdgeSDK.getReturnToUrl()),
                null,
                SEConstants.IFRAME,
                overrideCredentials ? "override" : null
        );
        call = SERestClient.getInstance().service.createReconnectSession(customerSecret, connectionSecret, requestData);
        if (call != null) call.enqueue(this);
    }

    public void createRefreshSession(
            String customerSecret,
            String connectionSecret,
            String localeCode
    ) {
        ConnectSessionRequest requestData = new ConnectSessionRequest(
                null,
                new SEAttempt(localeCode, SaltEdgeSDK.getReturnToUrl()),
                null,
                SEConstants.IFRAME,
                null);
        call = SERestClient.getInstance().service.createRefreshSession(customerSecret, connectionSecret, requestData);
        if (call != null) call.enqueue(this);
    }

    @Override
    public void onResponse(Call<ConnectSessionResponse> call, Response<ConnectSessionResponse> response) {
        ConnectSessionResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) {
            callback.onSuccess(responseBody.getConnectUrl());
        }
        else {
            if (callback != null) callback.onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
        }
    }

    @Override
    public void onFailure(Call<ConnectSessionResponse> call, Throwable t) {
        if (callback != null) callback.onFailure(SEErrorTools.processConnectionError(t));
    }
}
