/*
Copyright © 2015 Salt Edge. https://saltedge.com

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
package com.saltedge.sdk.sample.tabs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.saltedge.sdk.models.SEProvider;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.params.SECreateTokenParams;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.Tools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;
import com.saltedge.sdk.webview.SEWebViewTools;

public class ConnectFragment extends Fragment {

    private ProgressDialog progressDialog;
    private WebView webView;
    private String providerCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UITools.destroyProgressDialog(progressDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_connect, null);
        progressDialog = UITools.createProgressDialog(getActivity(), getString(R.string.creating_token));
        String url = Tools.getStringFromPreferences(getActivity(), Constants.KEY_REFRESH_URL);
        goToURL(url.isEmpty() ? Constants.CALLBACK_URL : url, mainView);
        return mainView;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.provider_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_provider_list:
                UITools.fetchProviders(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        obtainCreateToken(UITools.searchedProviders.get(which));
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void obtainCreateToken(SEProvider selectedProvider) {
        UITools.showProgress(progressDialog);
        providerCode = selectedProvider.getCode();
        String customerId = Tools.getStringFromPreferences(getActivity(), SEConstants.KEY_CUSTOMER_ID);
        SECreateTokenParams params = new SECreateTokenParams(selectedProvider.getCountryCode(), providerCode, Constants.CALLBACK_URL, customerId);
        SERequestManager.getInstance().createToken(params,
                new SERequestManager.FetchListener() {
                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        failedParsing(errorResponse);
                    }

                    @Override
                    public void onSuccess(Object response) {
                        UITools.destroyAlertDialog(progressDialog);
                        dataObtained(response);
                    }
                });

    }

    private void failedParsing(String message) {
        UITools.showAlertDialog(getActivity(), message);
    }

    private void dataObtained(Object urlToGo) {
        goToURL((String) urlToGo, getView());
    }

    private void goToURL(String url, View view) {
        Tools.addStringToPreferences(getActivity(), Constants.KEY_REFRESH_URL, "");
        webView = (WebView) view.findViewById(R.id.webView);
        SEWebViewTools.getInstance().initializeWithUrl(getActivity(), webView, url, new SEWebViewTools.WebViewRedirectListener() {
            @Override
            public void onLoadingFinished(String responseStatus, String loginSecret) {
                Tools.addStringToPreferences(getActivity(), providerCode, loginSecret);
                Tools.addStringToArrayPreferences(getActivity(), Constants.LOGIN_SECRET_ARRAY, loginSecret);
                openLoginsFragment();
            }

            @Override
            public void onLoadingFinishedWithError(String errorResponse) {
                // TODO
            }
        });
    }

    private void openLoginsFragment() {
        TabHost host = (TabHost) getActivity().findViewById(android.R.id.tabhost);
        host.setCurrentTab(1);
    }
}