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

import com.saltedge.sdk.model.response.DeleteLoginResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEJsonTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteLoginConnector implements Callback<DeleteLoginResponse> {

    private final Result callback;

    public DeleteLoginConnector(Result callback) {
        this.callback = callback;
    }

    public void deleteLogin(String loginSecret, String customerSecret) {
        SERestClient.getInstance().service.deleteLogin(customerSecret, loginSecret).enqueue(this);
    }

    @Override
    public void onResponse(Call<DeleteLoginResponse> call, Response<DeleteLoginResponse> response) {
        DeleteLoginResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) callback.onSuccess(responseBody.isRemoved());
        else callback.onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<DeleteLoginResponse> call, Throwable t) {
        callback.onFailure(t.getMessage());
    }

    public interface Result {
        void onSuccess(Boolean isRemoved);
        void onFailure(String errorMessage);
    }
}