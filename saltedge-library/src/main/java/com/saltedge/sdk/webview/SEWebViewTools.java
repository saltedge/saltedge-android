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
package com.saltedge.sdk.webview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.saltedge.sdk.utils.SEConstants;
import com.saltedge.sdk.utils.SEJSONTools;
import com.saltedge.sdk.utils.SETools;
import com.saltedge.sdk.utils.UITools;

import org.json.JSONObject;

public class SEWebViewTools {

    private String returnStatus;
    private static SEWebViewTools instance;
    private ProgressDialog progressDialog;
    private Activity activity;
    ValueCallback<Uri[]> uploadMessage;
    private WebViewRedirectListener webViewRedirectListener;
/**
 * Parse JSON Interface
 * */
    public interface WebViewRedirectListener {
        void onLoadingFinished(String statusResponse, String loginSecret);
        void onLoadingFinishedWithError(String statusResponse);
    }

    public static SEWebViewTools getInstance() {
        if (instance == null) {
            instance = new SEWebViewTools();
        }
        return instance;
    }

    public void initializeWithUrl(Activity activity, final WebView webView, final String url, WebViewRedirectListener listener) {
        this.activity = activity;
        this.webViewRedirectListener = listener;
        progressDialog = UITools.createProgressDialog(this.activity);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                SETools.uploadMessage = filePathCallback;
                pickFile();
                return true;
            }

        });
        webView.loadUrl(url);
    }

    private void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("file/*");
        activity.startActivityForResult(chooserIntent, SEConstants.FILECHOOSER_RESULT_CODE);
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (urlIsFenturyRedirection(url)) {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            UITools.showProgress(progressDialog);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            UITools.destroyProgressDialog(progressDialog);
        }
    }

    private boolean urlIsFenturyRedirection(String url) {
        if (url.contains(SEConstants.PREFIX_SALTBRIDGE)) {
            String redirectURL = url.substring(SEConstants.PREFIX_SALTBRIDGE.length(), url.length());
            JSONObject dataJsonObject = SEJSONTools.getObject(SEJSONTools.stringToJSON(redirectURL), SEConstants.KEY_DATA);
            String stage = SEJSONTools.getString(dataJsonObject, SEConstants.KEY_STAGE);
            String loginSecret = SEJSONTools.getString(dataJsonObject, SEConstants.KEY_SECRET);
            if (stage.equals(SEConstants.STATUS_SUCCESS)) {
                webViewRedirectListener.onLoadingFinished(stage, loginSecret);
            } else if (stage.equals(SEConstants.STATUS_ERROR)) {
                webViewRedirectListener.onLoadingFinishedWithError(stage);
            }
            return false;
        }
        return true;
    }

}
