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

import com.saltedge.sdk.interfaces.ConnectSessionResult;
import com.saltedge.sdk.network.SEApiConstants;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;
import com.saltedge.sdk.webview.SEWebViewTools;

public class ConnectActivity extends AppCompatActivity implements SEWebViewTools.WebViewRedirectListener,
        DialogInterface.OnClickListener, ConnectSessionResult {

    private ProgressDialog progressDialog;
    private WebView webView;
    private String providerCode;
    private String connectionSecret;
    private Boolean tryToRefresh;
    private String locale = "";
    private String callbackUrl = Constants.CALLBACK_URL;

    /**
     * ConnectActivity intent creator for the provider reconnect or refresh procedure
     * @param activity - host activity
     * @param providerCode - provider code which should be connected
     * @return new Intent
     */
    public static Intent newIntent(Activity activity, String providerCode) {
        Intent intent = new Intent(activity, ConnectActivity.class);
        intent.putExtra(SEConstants.KEY_PROVIDER_CODE, providerCode);
        return intent;
    }

    /**
     * ConnectActivity intent creator for the new provider connection
     * @param activity - host activity
     * @param providerCode - provider code which should be connected
     * @param connectionSecret - connection secret which should reconnected
     * @param tryToRefresh - if it is possible connector should try to refresh the connection data else reconnect the connection
     * @param overrideCredentials - indicates that the new credentials will automatically override the old ones on reconnect
     * @return new Intent
     */
    public static Intent newIntent(Activity activity,
                                   String providerCode,
                                   String connectionSecret,
                                   Boolean tryToRefresh,
                                   Boolean overrideCredentials) {
        Intent intent = new Intent(activity, ConnectActivity.class);
        intent.putExtra(SEConstants.KEY_PROVIDER_CODE, providerCode);
        intent.putExtra(Constants.KEY_CONNECTION_SECRET, connectionSecret);
        intent.putExtra(Constants.KEY_REFRESH, tryToRefresh);
        intent.putExtra(Constants.KEY_OVERRIDE_CREDENTIALS, overrideCredentials);
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

    /**
     * Success callback after Token create request. Url is used to redirect the user
     * @param connectUrl - url of Saltedge Connect Page.
     */
    @Override
    public void onSuccess(String connectUrl) {
        try {
            UITools.destroyAlertDialog(progressDialog);
            loadConnectUrl(connectUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fail callback after Token create request
     * @param errorMessage - error message
     */
    @Override
    public void onFailure(String errorMessage) {
        handleConnectionError(errorMessage);
    }

    /**
     * Success callback of provider connect flow
     * @param stage - last connect flow stage
     * @param loginSecret - login secret code
     */
    @Override
    public void onLoginSecretFetchSuccess(String stage, String loginId, String loginSecret) {
        PreferencesTools.putConnectionSecret(this, loginId, loginSecret);
        Toast.makeText(this, "Login successfully connected", Toast.LENGTH_SHORT).show();
        closeActivity(true);
    }

    /**
     * Login data updated callback.
     */
    @Override
    public void onLoginRefreshSuccess() {
        Toast.makeText(this, "Login updated", Toast.LENGTH_SHORT).show();
        closeActivity(true);
    }

    @Override
    public void onLoginFetchingStage(String loginId, String loginSecret) {
        if (loginId == null || loginSecret == null) return;
        if (this.connectionSecret == null || !this.connectionSecret.equals(loginSecret)) {
            this.connectionSecret = loginSecret;
            if (!PreferencesTools.connectionSecretIsSaved(this, loginId, loginSecret)) {
                PreferencesTools.putConnectionSecret(this, loginId, loginSecret);
            }
        }
    }

    /**
     * Error callback of provider connect flow
     * @param statusResponse - error message from connect page
     */
    @Override
    public void onLoginSecretFetchError(String statusResponse) {
        UITools.showAlertDialog(this, statusResponse, this);
    }

    /**
     * Error dialog click listener
     * @param dialogInterface - dialog interface
     * @param id - dialog action id
     */
    @Override
    public void onClick(DialogInterface dialogInterface, int id) {
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
            boolean overrideCredentials = this.getIntent()
                    .getBooleanExtra(Constants.KEY_OVERRIDE_CREDENTIALS, false);
            fetchReconnectConnectionToken(overrideCredentials);
        } else {
            fetchCreateConnectionToken();
        }
    }

    private boolean isRefreshMode() {
        return tryToRefresh != null && connectionSecret != null && tryToRefresh;
    }

    private boolean isReconnectViewMode() {
        return tryToRefresh != null && connectionSecret != null && !tryToRefresh;
    }

    private void fetchCreateConnectionToken() {
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, getString(R.string.creating_token));
        String[] scopes = SEApiConstants.SCOPE_ACCOUNT_TRANSACTIONS;
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().createToken(providerCode, scopes, callbackUrl, customerSecret, this);
    }

    private void fetchRefreshConnectionToken() {
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.refresh_provider));
        SERequestManager.getInstance().refreshToken(locale, callbackUrl, connectionSecret, customerSecret, this);
    }

    private void fetchReconnectConnectionToken(boolean overrideCredentials) {
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.reconnect_provider));
        SERequestManager.getInstance().reconnectToken(locale, callbackUrl, connectionSecret, customerSecret,
                overrideCredentials, this);
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
            SEWebViewTools.getInstance().clear();
            setResult(result ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInitialData() {
        Intent intent = this.getIntent();
        providerCode = intent.getStringExtra(SEConstants.KEY_PROVIDER_CODE);
        connectionSecret = intent.getStringExtra(Constants.KEY_CONNECTION_SECRET);
        if (intent.hasExtra(Constants.KEY_REFRESH)) {
            tryToRefresh = intent.getBooleanExtra(Constants.KEY_REFRESH, false);
        }
    }
}
