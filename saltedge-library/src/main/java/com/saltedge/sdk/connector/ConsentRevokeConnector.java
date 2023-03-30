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

import com.saltedge.sdk.interfaces.DeleteEntryResult;
import com.saltedge.sdk.model.response.ConsentResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsentRevokeConnector implements Callback<ConsentResponse> {

    private final DeleteEntryResult callback;
    private String customerSecret;
    private String connectionSecret;
    private String consentId;

    public ConsentRevokeConnector(DeleteEntryResult callback) {
        this.callback = callback;
    }

    public void revokeConsent(String customerSecret, String connectionSecret, String consentId) {
        this.customerSecret = customerSecret;
        this.connectionSecret = connectionSecret;
        this.consentId = consentId;
        SERestClient.getInstance().service.revokeConsent(customerSecret, connectionSecret, consentId).enqueue(this);
    }

    @Override
    public void onResponse(Call<ConsentResponse> call, Response<ConsentResponse> response) {
        ConsentResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null && responseBody.getData() != null) {
            callback.onSuccess(responseBody.getData().getRevokedAt() != null, consentId);
        } else {
            if (callback != null) callback.onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
        }
    }

    @Override
    public void onFailure(Call<ConsentResponse> call, Throwable t) {
        if (callback != null) callback.onFailure(SEErrorTools.processConnectionError(t));
    }
}
