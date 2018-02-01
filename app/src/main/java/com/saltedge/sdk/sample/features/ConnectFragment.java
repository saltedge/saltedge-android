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
package com.saltedge.sdk.sample.features;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.Toast;

import com.saltedge.sdk.connector.ProvidersConnector;
import com.saltedge.sdk.interfaces.TokenConnectionResult;
import com.saltedge.sdk.model.ProviderData;
import com.saltedge.sdk.network.ApiConstants;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.BuildConfig;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.webview.SEWebViewTools;

import java.util.ArrayList;

public class ConnectFragment extends Fragment implements ProvidersDialog.ProviderSelectListener {

    private ProgressDialog progressDialog;
    private String providerCode;
    private ArrayList<ProviderData> providers;
    private WebView webView;
    private String applicationLanguage = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        applicationLanguage = getResources().getConfiguration().locale.getLanguage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_connect, null);
        webView = mainView.findViewById(R.id.webView);
        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_provider, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_provider_list:
                fetchProviders();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String url = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_REFRESH_URL);
        loadURL(url.isEmpty() ? Constants.CALLBACK_URL : url);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UITools.destroyProgressDialog(progressDialog);
    }

    @Override
    public void onProviderSelected(ProviderData provider) {
        Toast.makeText(getActivity(), "Selected " + String.valueOf(provider.getName()), Toast.LENGTH_SHORT).show();
        createToken(provider, Constants.CALLBACK_URL);
    }

    private void loadURL(String url) {
        PreferencesTools.putStringToPreferences(getActivity(), Constants.KEY_REFRESH_URL, "");
        SEWebViewTools.getInstance().initializeWithUrl(getActivity(), webView, url,
                new SEWebViewTools.WebViewRedirectListener() {
                    @Override
                    public void onLoginSecretFetchSuccess(String responseStatus, String loginSecret) {
                        onLoginSecretSuccessFetch(loginSecret);
                    }

                    @Override
                    public void onLoginSecretFetchError(String errorResponse) {
                        showError(errorResponse);
                    }
        });
    }

    private void createToken(ProviderData selectedProvider, String callbackUrl) {
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(getActivity(), getString(R.string.creating_token));
        providerCode = selectedProvider.getCode();
        String[] scopes = ApiConstants.SCOPE_ACCOUNT_TRANSACTIONS;
        String customerSecret = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().createToken(providerCode, scopes, callbackUrl, customerSecret,
                new TokenConnectionResult() {
                    @Override
                    public void onSuccess(String connectUrl) {// here is a URL you can use to redirect the user
                        UITools.destroyAlertDialog(progressDialog);
                        dataObtained(connectUrl);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        UITools.destroyAlertDialog(progressDialog);
                        failedParsing(errorMessage);
                    }
                });
    }

    private void fetchProviders() {
        if (providers == null || providers.isEmpty()) {
            UITools.destroyProgressDialog(progressDialog);
            progressDialog = UITools.showProgressDialog(getActivity(), getString(R.string.fetching_providers));
            String countryCode = (BuildConfig.DEBUG) ? "XF" : applicationLanguage;
            SERequestManager.getInstance().fetchProviders(countryCode, new ProvidersConnector.Result() {

                @Override
                public void onSuccess(ArrayList<ProviderData> providersList) {
                    UITools.destroyAlertDialog(progressDialog);
                    providers = providersList;
                    showProviders();
                }

                @Override
                public void onFailure(String errorResponse) {
                    UITools.destroyAlertDialog(progressDialog);
                    UITools.failedParsing(getActivity(), errorResponse);
                }
            });
        } else {
            showProviders();
        }
    }

    private void showProviders() {
        if (providers != null && !providers.isEmpty()) {
            Toast.makeText(getActivity(), "Fetched " + String.valueOf(providers.size()) + " providers", Toast.LENGTH_SHORT).show();
            ProvidersDialog.newInstance(providers, this).show(getFragmentManager(), "");
        } else {
            UITools.showAlertDialog(getActivity(), getString(R.string.providers_empty));
        }
    }

    private void failedParsing(String message) {
        UITools.showAlertDialog(getActivity(), message);
    }

    private void dataObtained(String urlToGo) {
        loadURL(urlToGo);
    }

    private void onLoginSecretSuccessFetch(String loginSecret) {
        PreferencesTools.addLoginSecret(getActivity(), providerCode, loginSecret);
        Toast.makeText(getActivity(), "Login connected", Toast.LENGTH_SHORT).show();
        openLoginsFragment();
    }

    private void showError(String errorResponse) {
        UITools.showAlertDialog(getActivity(), errorResponse);
    }

    private void openLoginsFragment() {
        TabHost host = getActivity().findViewById(android.R.id.tabhost);
        host.setCurrentTab(1);
    }
}