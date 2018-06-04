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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Toast;

import com.saltedge.sdk.interfaces.TokenConnectionResult;
import com.saltedge.sdk.network.ApiConstants;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;
import com.saltedge.sdk.webview.SEWebViewTools;

public class ConnectActivity extends AppCompatActivity implements SEWebViewTools.WebViewRedirectListener,
        DialogInterface.OnClickListener, TokenConnectionResult {

    private ProgressDialog progressDialog;
    private WebView webView;
    private String providerCode;
    private String loginSecret;
    private Boolean tryToRefresh;
    private String locale = "";
    private String callbackUrl = Constants.CALLBACK_URL;

    public static Intent newIntent(Activity activity, String providerCode, String loginSecret, Boolean tryToRefresh) {
        Intent intent = new Intent(activity, ConnectActivity.class);
        intent.putExtra(SEConstants.KEY_PROVIDER_CODE, providerCode);
        intent.putExtra(Constants.KEY_LOGIN_SECRET, loginSecret);
        intent.putExtra(Constants.KEY_REFRESH, tryToRefresh);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        webView = findViewById(R.id.webView);
        setInitialData();
        setupActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String url = webView.getOriginalUrl();
        if (url == null || url.isEmpty()) {
            fetchConnectionToken();
        }
    }

    @Override
    public void onSuccess(String connectUrl) {
        // here is a URL you can use to redirect the user
        try {
            UITools.destroyAlertDialog(progressDialog);
            loadConnectUrl(connectUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        handleConnectionError(errorMessage);
    }

    @Override
    public void onLoginSecretFetchSuccess(String statusResponse, String loginSecret) {
        PreferencesTools.addLoginSecret(this, providerCode, loginSecret);
        Toast.makeText(this, "Login connected", Toast.LENGTH_SHORT).show();
        closeActivity(true);
    }

    @Override
    public void onLoginRefreshSuccess() {
        Toast.makeText(this, "Login refreshed", Toast.LENGTH_SHORT).show();
        closeActivity(true);
    }

    @Override
    public void onLoginSecretFetchError(String statusResponse) {
        UITools.showAlertDialog(this, statusResponse, this);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {//On Error Dialog click
        closeActivity(false);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (isRefreshMode()) {
                actionBar.setTitle("Refresh provider");
            } else if (isReconnectViewMode()) {
                actionBar.setTitle("Reconnect provider");
            } else {
                actionBar.setTitle("Connect provider");
            }
        }
    }

    private void fetchConnectionToken() {
        if (isRefreshMode()) {
            fetchRefreshConnectionToken();
        } else if (isReconnectViewMode()) {
            fetchReconnectConnectionToken();
        } else {
            fetchCreateConnectionToken();
        }
    }

    private boolean isRefreshMode() {
        return tryToRefresh != null && loginSecret != null && tryToRefresh;
    }

    private boolean isReconnectViewMode() {
        return tryToRefresh != null && loginSecret != null && !tryToRefresh;
    }

    private void fetchCreateConnectionToken() {
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, getString(R.string.creating_token));
        String[] scopes = ApiConstants.SCOPE_ACCOUNT_TRANSACTIONS;
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().createToken(providerCode, scopes, callbackUrl, customerSecret, this);
    }

    private void fetchRefreshConnectionToken() {
        String loginSecret = PreferencesTools.getStringFromPreferences(this, providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.refresh_provider));
        SERequestManager.getInstance().refreshToken(locale, callbackUrl, loginSecret, customerSecret, this);
    }

    private void fetchReconnectConnectionToken() {
        String loginSecret = PreferencesTools.getStringFromPreferences(this, providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.reconnect_provider));
        SERequestManager.getInstance().reconnectToken(locale, callbackUrl, loginSecret, customerSecret, this);
    }

    private void handleConnectionError(String errorMessage) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.showAlertDialog(this, errorMessage);
    }

    private void loadConnectUrl(String connectUrl) {
        if (webView != null) {
            SEWebViewTools.getInstance().initializeWithUrl(this, webView, connectUrl, callbackUrl, this);
        }
    }

    private void closeActivity(boolean result) {
        try {
            setResult(result ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInitialData() {
        Intent intent = this.getIntent();
        providerCode = intent.getStringExtra(SEConstants.KEY_PROVIDER_CODE);
        loginSecret = intent.getStringExtra(Constants.KEY_LOGIN_SECRET);
        if (intent.hasExtra(Constants.KEY_REFRESH)) {
            tryToRefresh = intent.getBooleanExtra(Constants.KEY_REFRESH, false);
        }
    }
}
