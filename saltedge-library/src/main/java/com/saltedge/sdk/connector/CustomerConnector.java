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

import com.saltedge.sdk.interfaces.CreateCustomerResult;
import com.saltedge.sdk.model.request.CreateCustomerRequest;
import com.saltedge.sdk.model.response.CreateCustomerResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerConnector extends BasePinnedConnector implements Callback<CreateCustomerResponse> {

    private final CreateCustomerResult callback;
    private String customerIdentifier = "";

    public CustomerConnector(CreateCustomerResult callback) {

        this.callback = callback;
    }

    public void createCustomer(String customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
        checkAndLoadPinsOrDoRequest();
    }

    @Override
    void enqueueCall() {
        SERestClient.getInstance().service.createCustomer(new CreateCustomerRequest(customerIdentifier))
                .enqueue(this);
    }

    @Override
    void onFailure(String errorMessage) {
        if (callback != null) callback.onFailure(errorMessage);
    }

    @Override
    public void onResponse(Call<CreateCustomerResponse> call, Response<CreateCustomerResponse> response) {
        CreateCustomerResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) callback.onSuccess(responseBody.getSecret());
        else callback.onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<CreateCustomerResponse> call, Throwable t) {
        onFailure(SEErrorTools.processConnectionError(t));
    }
}
