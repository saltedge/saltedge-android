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
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.saltedge.sdk.interfaces.ConnectSessionResult;
import com.saltedge.sdk.model.SEProvider;
import com.saltedge.sdk.model.Saltbridge;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferenceRepository;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;
import com.saltedge.sdk.webview.SEWebViewTools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static com.saltedge.sdk.sample.utils.Constants.KEY_CONNECTION_SECRET;
import static com.saltedge.sdk.sample.utils.UITools.refreshProgressDialog;

public class ConnectActivity extends AppCompatActivity implements DialogInterface.OnClickListener,
        SEWebViewTools.WebViewRedirectListener {

    public static final int CONNECT_REQUEST_CODE = 1001;
    private ProgressDialog progressDialog;
    private WebView webView;
    private String providerCode;
    private String connectionSecret;
    private Boolean tryToRefresh = false;
    private Boolean isOAuthProvider = false;
    private String localeCode = "";

    /**
     * ConnectActivity intent creator for the new provider connection
     *
     * @param activity - host activity
     * @param provider - provider which should be connected
     * @return new Intent
     */
    public static Intent newIntent(Activity activity, SEProvider provider) {
        Intent intent = new Intent(activity, ConnectActivity.class);
        intent.putExtra(SEConstants.KEY_PROVIDER_CODE, provider.getCode());
        intent.putExtra(Constants.KEY_OAUTH, provider.isOAuth());
        return intent;
    }

    /**
     * ConnectActivity intent creator for the provider reconnect or refresh procedure
     *
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
        intent.putExtra(KEY_CONNECTION_SECRET, connectionSecret);
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
            requestConnectUrl();
        }
    }

    @Override
    public void onConnectSessionSuccessStage(String connectionId, String connectionSecret, String rawJsonData) {
        PreferenceRepository.putConnectionSecret(connectionSecret);
        UITools.showShortToast(this, R.string.connection_connected);
        closeActivity(true, null);
    }

    @Override
    public void onConnectSessionErrorStage(String rawJsonData) {
        UITools.showAlertDialog(this, "Connect error", this);
    }

    @Override
    public void onConnectSessionFetchingStage(String connectionId, String connectionSecret, String apiStage, String rawJsonData) {
        updateConnectionSecret(connectionSecret);

    }

    @Override
    public void onConnectSessionStageChange(Saltbridge result, String rawJsonData) {
        //TODO
    }

    @Override
    public boolean onRedirectToReturnUrl(String url) {
        UITools.showShortToast(this, R.string.connection_updated);
        if (isOAuthProvider) {
            String connectionSecret = null;
            try {
                Uri uri = Uri.parse(url);
                connectionSecret = URLDecoder.decode(uri.getQueryParameter(KEY_CONNECTION_SECRET), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            updateConnectionSecret(connectionSecret);
        }
        closeActivity(true, null);
        return false;
    }

    /**
     * Error dialog click listener
     *
     * @param dialogInterface - dialog interface
     * @param id - dialog action id
     */
    @Override
    public void onClick(DialogInterface dialogInterface, int id) {
        closeActivity(false, null);
    }

    private ConnectSessionResult connectSessionResult = new ConnectSessionResult() {
        @Override
        public void onSuccess(String connectUrl) {
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
    };

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (isOAuthProvider) {
                actionBar.setTitle(R.string.connecting_oauth);
            } else {
                if (isRefreshMode()) {
                    actionBar.setTitle(R.string.refreshing);
                } else if (isReconnectViewMode()) {
                    actionBar.setTitle(R.string.reconnecting);
                } else {
                    actionBar.setTitle(R.string.connecting);
                }
            }
        }
    }

    private void requestConnectUrl() {
        if (isRefreshMode()) {//Refresh connection
            createRefreshSession();
        } else if (isReconnectViewMode()) {//Reconnect connection
            boolean overrideCredentials = this.getIntent().getBooleanExtra(Constants.KEY_OVERRIDE_CREDENTIALS, false);
            createReconnectSession(overrideCredentials);
        } else {//Create connection
            createConnectSession();
        }
    }

    private boolean isRefreshMode() {
        return connectionSecret != null && (tryToRefresh != null && tryToRefresh);
    }

    private boolean isReconnectViewMode() {
        return connectionSecret != null && (tryToRefresh != null && !tryToRefresh);
    }

    private void createConnectSession() {
        int progressTitle = isOAuthProvider ? R.string.connecting_oauth : R.string.connecting;
        progressDialog = refreshProgressDialog(this, progressDialog, progressTitle);

        String customerSecret = PreferenceRepository.getStringFromPreferences(Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().createConnectSession(
                customerSecret,
                providerCode,
                Constants.CONSENT_SCOPES,
                localeCode,
                connectSessionResult
        );
    }

    private void createRefreshSession() {
        progressDialog = refreshProgressDialog(this, progressDialog, R.string.refresh_provider);

        String customerSecret = PreferenceRepository.getStringFromPreferences(Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().createRefreshSession(
                customerSecret,
                connectionSecret,
                localeCode,
                connectSessionResult);
    }

    private void createReconnectSession(boolean overrideCredentials) {
        progressDialog = refreshProgressDialog(this, progressDialog, R.string.reconnect_provider);

        String customerSecret = PreferenceRepository.getStringFromPreferences(Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().createReconnectSession(
                customerSecret,
                connectionSecret,
                Constants.CONSENT_SCOPES,
                localeCode,
                overrideCredentials,
                connectSessionResult);
    }

    private void handleConnectionError(String errorMessage) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.showAlertDialog(this, errorMessage);
    }

    private void loadConnectUrl(String connectUrl) {
        if (isOAuthProvider) {
            closeActivity(true, connectUrl);
        } else if (webView != null) {
            SEWebViewTools.getInstance().initializeWithUrl(
                    this,
                    webView,
                    connectUrl,
                    this
            );
        }
    }

    private void updateConnectionSecret(String connectionSecret) {
        if (connectionSecret != null && (this.connectionSecret == null || !this.connectionSecret.equals(connectionSecret))) {
            this.connectionSecret = connectionSecret;
            if (!PreferenceRepository.connectionSecretIsSaved(connectionSecret)) {
                PreferenceRepository.putConnectionSecret(connectionSecret);
            }
        }
    }

    private void closeActivity(boolean finishWithSuccess, String connectUrl) {
        try {
            SEWebViewTools.getInstance().clear();
            int resultCode = finishWithSuccess ? Activity.RESULT_OK : Activity.RESULT_CANCELED;

            if (connectUrl != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(SEConstants.KEY_CONNECT_URL, connectUrl);
                setResult(resultCode, resultIntent);
            } else {
                setResult(resultCode);
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInitialData() {
        Intent intent = this.getIntent();
        providerCode = intent.getStringExtra(SEConstants.KEY_PROVIDER_CODE);
        connectionSecret = intent.getStringExtra(KEY_CONNECTION_SECRET);

        isOAuthProvider = intent.getBooleanExtra(Constants.KEY_OAUTH, false);

        if (intent.hasExtra(Constants.KEY_REFRESH)) {
            tryToRefresh = intent.getBooleanExtra(Constants.KEY_REFRESH, false);
        }
    }
}
