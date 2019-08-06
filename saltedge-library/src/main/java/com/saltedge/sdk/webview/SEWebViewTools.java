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
package com.saltedge.sdk.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.model.Saltbridge;
import com.saltedge.sdk.model.response.SaltbridgeResponse;
import com.saltedge.sdk.network.SEApiConstants;
import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.utils.SEConstants;
import com.saltedge.sdk.utils.UITools;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class designated to show Salt Edge Connect Web View and handle it callbacks
 */
public class SEWebViewTools {

    public static ValueCallback<Uri[]> uploadMessage;

    private static SEWebViewTools instance;

    private ProgressDialog progressDialog;
    private Activity activity;
    private WebViewRedirectListener webViewListener;
    private String returnUrl;

    public interface WebViewRedirectListener {

        /**
         * Received SUCCESS stage callback of provider connect flow
         *
         * @param connectionId connection id
         * @param connectionSecret connection secret code
         * @param rawJsonData raw JSON result data
         */
        void onConnectSessionSuccessStage(String connectionId, String connectionSecret, String rawJsonData);

        /**
         * Received ERROR stage callback of provider connect flow
         *
         * @param rawJsonData raw JSON result data
         */
        void onConnectSessionErrorStage(String rawJsonData);

        /**
         * Received FETCHING stage callback of provider connect flow
         *
         * @param connectionId connection id
         * @param connectionSecret connection secret code
         * @param apiStage api stage string
         * @param rawJsonData raw JSON result data
         */
        void onConnectSessionFetchingStage(String connectionId, String connectionSecret, String apiStage, String rawJsonData);

        /**
         * Received new Stage callback of provider connect flow
         * (Not SUCCESS, not ERROR, not FETCHING)
         *
         * @param result parsed Saltbridge result object
         * @param rawJsonData raw JSON result data
         */
        void onConnectSessionStageChange(Saltbridge result, String rawJsonData);

        /**
         * Connect session redirected to Return URL. Means that Connection was updated.
         *
         * @return true if WebView should be redirected to Return URL, otherwise return false.
         */
        boolean onConnectSessionRedirectToReturnUrl();
    }

    public static SEWebViewTools getInstance() {
        if (instance == null) {
            instance = new SEWebViewTools();
        }
        return instance;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initializeWithUrl(Activity activity,
                                  WebView webView,
                                  String url,
                                  String returnUrl,
                                  WebViewRedirectListener listener) {
        this.activity = activity;
        this.webViewListener = listener;
        this.returnUrl = returnUrl;
        progressDialog = UITools.createProgressDialog(activity);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                uploadMessage = filePathCallback;
                startFilePicker();
                return true;
            }

        });
        webView.loadUrl(url);
    }

    public void clear() {
        activity = null;
        webViewListener = null;
    }

    private void startFilePicker() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("file/*");
        if (activity != null) activity.startActivityForResult(chooserIntent, SEConstants.FILECHOOSER_RESULT_CODE);
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            SaltEdgeSDK.printToLogcat("SEWebViewTools", "load url: " + url);
            if (view != null && urlIsSaltedgeRedirection(url)) {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (activity != null && !activity.isFinishing()) {
                UITools.showProgress(progressDialog);
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (activity != null && !activity.isFinishing()) {
                UITools.destroyProgressDialog(progressDialog);
            }
        }
    }

    private boolean urlIsSaltedgeRedirection(String url) {
        if (returnUrl != null && !returnUrl.isEmpty() && url.equals(returnUrl)) {
            if (webViewListener != null) {
                return webViewListener.onConnectSessionRedirectToReturnUrl();
            }
        } else if (isSaltbridgeUrl(url)) {
            String rawJsonData = extractRawSaltbridgeData(url);
            onStageChanged(parseSaltbridgeObject(rawJsonData), rawJsonData);
            return false;
        }
        return true;
    }

    private boolean isSaltbridgeUrl(String url) {
        return url != null && url.contains(SEApiConstants.PREFIX_SALTBRIDGE);
    }

    public String extractRawSaltbridgeData(@NotNull String url) {
        return url.contains(SEApiConstants.PREFIX_SALTBRIDGE)
                ? url.substring(SEApiConstants.PREFIX_SALTBRIDGE.length())
                : "";
    }

    private Saltbridge parseSaltbridgeObject(@NotNull String rawJsonData) {
        SaltbridgeResponse response = null;
        try {
            response = SERestClient.gson.fromJson(rawJsonData, SaltbridgeResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response == null ? null : response.getData();
    }

    private void onStageChanged(Saltbridge saltbridge, String rawJsonData) {
        if (webViewListener == null || saltbridge == null) return;
        String stage = saltbridge.getStage();
        String connectionId = saltbridge.getConnectionId();
        String connectionSecret = saltbridge.getSecret();
        switch (stage) {
            case SEConstants.STATUS_SUCCESS:
                webViewListener.onConnectSessionSuccessStage(
                        connectionId,
                        connectionSecret,
                        rawJsonData
                );
                break;
            case SEConstants.STATUS_ERROR:
                webViewListener.onConnectSessionErrorStage(rawJsonData);
                break;
            case SEConstants.STATUS_FETCHING:
                webViewListener.onConnectSessionFetchingStage(
                        connectionId,
                        connectionSecret,
                        saltbridge.getApiStage(),
                        rawJsonData
                );
                break;
            default:
                webViewListener.onConnectSessionStageChange(saltbridge, rawJsonData);
                break;
        }
    }
}
