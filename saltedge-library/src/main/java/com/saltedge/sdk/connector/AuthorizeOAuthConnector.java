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

import com.saltedge.sdk.interfaces.FetchConnectionResult;
import com.saltedge.sdk.model.request.ConnectionAuthorizeRequest;
import com.saltedge.sdk.model.response.ConnectionResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizeOAuthConnector implements Callback<ConnectionResponse> {

    private FetchConnectionResult callback;
    private Call<ConnectionResponse> call;

    public AuthorizeOAuthConnector(FetchConnectionResult callback) {
        this.callback = callback;
    }

    public void authorizeConnection(String customerSecret,
                                    String connectionSecret,
                                    String authorizationQuery) {
        call = SERestClient.getInstance().service.authorizeOAuthConnection(
                customerSecret,
                connectionSecret,
                new ConnectionAuthorizeRequest(authorizationQuery)
        );
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<ConnectionResponse> call, Response<ConnectionResponse> response) {
        ConnectionResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) {
            callback.onSuccess(responseBody.getData());
        } else {
            if (callback != null) callback.onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
        }
    }

    @Override
    public void onFailure(Call<ConnectionResponse> call, Throwable t) {
        if (callback != null) callback.onFailure(SEErrorTools.processConnectionError(t));
    }
}
