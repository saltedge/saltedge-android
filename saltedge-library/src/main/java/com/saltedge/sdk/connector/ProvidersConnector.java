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

import com.saltedge.sdk.BuildConfig;
import com.saltedge.sdk.interfaces.ProvidersResult;
import com.saltedge.sdk.model.ProviderData;
import com.saltedge.sdk.model.response.ProvidersResponse;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEErrorTools;
import com.saltedge.sdk.utils.SEJsonTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProvidersConnector implements Callback<ProvidersResponse> {

    private final ProvidersResult callback;
    private String nextPageId = "";
    private ArrayList<ProviderData> providersList = new ArrayList<>();
    private boolean includeFakeProviders = BuildConfig.DEBUG;
    private String countryCode = "";

    public ProvidersConnector(ProvidersResult callback) {
        this.callback = callback;
    }

    public void fetchProviders(String providersCountryCode) {
        this.countryCode = providersCountryCode.toUpperCase();
        SERestClient.getInstance().service.getProviders(countryCode, includeFakeProviders, nextPageId).enqueue(this);
    }

    @Override
    public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
        ProvidersResponse responseBody = response.body();
        if (response.isSuccessful() && responseBody != null) {
            providersList.addAll(responseBody.getData());
            nextPageId = responseBody.getMeta().getNextId();
            fetchNextPageOrFinish();
        }
        else callback.onFailure(SEJsonTools.getErrorMessage(response.errorBody()));
    }

    @Override
    public void onFailure(Call<ProvidersResponse> call, Throwable t) {
        callback.onFailure(SEErrorTools.processConnectionError(t));
    }

    private void fetchNextPageOrFinish() {
        if (nextPageId == null || nextPageId.isEmpty()) {
            Collections.sort(providersList, new Comparator<ProviderData>() {
                @Override
                public int compare(ProviderData p1, ProviderData p2) {
                    return p1.getName().compareTo(p2.getName());
                }
            });
            callback.onSuccess(providersList);
        } else {
            SERestClient.getInstance().service.getProviders(countryCode, includeFakeProviders, nextPageId).enqueue(this);
        }
    }
}