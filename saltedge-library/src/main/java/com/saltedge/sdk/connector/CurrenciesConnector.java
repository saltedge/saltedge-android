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

import com.saltedge.sdk.interfaces.FetchCurrencyRatesResult;
import com.saltedge.sdk.model.SECurrencyRate;
import com.saltedge.sdk.model.response.CurrenciesResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrenciesConnector implements Callback<CurrenciesResponse> {

    private final FetchCurrencyRatesResult callback;
    private String customerSecret = "";
    private String ratesDate = "";

    public CurrenciesConnector(FetchCurrencyRatesResult callback) {
        this.callback = callback;
    }

    public void fetchCurrenciesRates(String customerSecret, String ratesDate) {
        this.customerSecret = customerSecret;
        this.ratesDate = ratesDate;
        SERestClient.getInstance().service.getCurrencies(customerSecret, ratesDate).enqueue(this);
    }

    @Override
    public void onResponse(Call<CurrenciesResponse> call, Response<CurrenciesResponse> response) {
        CurrenciesResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) {
            ArrayList<SECurrencyRate> currencies = new ArrayList<>(responseBody.getCurrencies());
            Collections.sort(currencies, (a1, a2) -> a1.getCurrencyCode().compareTo(a2.getCurrencyCode()));
            callback.onFetchCurrenciesSuccess(currencies);
        }
        else {
            if (callback != null) callback.onFetchCurrenciesFailure(SEJsonTools.getErrorMessage(response.errorBody()));
        }
    }

    @Override
    public void onFailure(Call<CurrenciesResponse> call, Throwable t) {
        if (callback != null) callback.onFetchCurrenciesFailure(SEErrorTools.processConnectionError(t));
    }
}