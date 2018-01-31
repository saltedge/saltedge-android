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

import com.saltedge.sdk.model.LoginData;
import com.saltedge.sdk.model.response.LoginResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginConnector implements Callback<LoginResponse> {

    private final Result callback;
    private ArrayList<LoginData> loginsList = new ArrayList<>();
    private int resultCount = 0;

    public LoginConnector(Result callback) {
        this.callback = callback;
    }

    public void fetchLogins(String[] loginSecretsArray, String customerSecret) {
        resultCount = loginSecretsArray.length;
        for (String loginSecret : loginSecretsArray) {
            SERestClient.getInstance().service.getLogin(customerSecret, loginSecret).enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        LoginResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) onSuccess(responseBody.getData());
        else callback.onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    private void onSuccess(LoginData data) {
        loginsList.add(data);
        if (loginsList.size() >= resultCount) {
            callback.onSuccess(loginsList);
        }
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        callback.onFailure(t.getMessage());
    }

    public interface Result {
        void onSuccess(List<LoginData> logins);
        void onFailure(String errorMessage);
    }
}
