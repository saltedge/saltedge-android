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
package com.saltedge.sdk.sample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.tabs.TabHostFragmentActivity;
import com.saltedge.sdk.sample.utils.Tools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;

import java.util.Objects;

public class StartActivity extends Activity {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        progressDialog = UITools.createProgressDialog(this, "");
        setupSaltEdgeSDK();
        createCustomer();
    }

    private void setupSaltEdgeSDK() {
        SaltEdgeSDK.getInstance().setContext(this);
    }

    private void createCustomer() {
        String customerIdentifier = "testAndroidApp3"; // Random name, each installation - new name
        UITools.showProgress(progressDialog);
        String customerSecret = Tools.getStringFromPreferences(this, SEConstants.KEY_CUSTOMER_SECRET);
        if (TextUtils.isEmpty(customerSecret)) {
            SERequestManager.getInstance().createCustomer(customerIdentifier, new SERequestManager.FetchListener() {
                @Override
                public void onFailure(String errorResponse) {
                    UITools.destroyAlertDialog(progressDialog);
                    UITools.failedParsing(StartActivity.this, errorResponse);
                }

                @Override
                public void onSuccess(Object response) {
                    UITools.destroyAlertDialog(progressDialog);
                    dataObtained((String) response);
                }
            });
        } else {
            intentToTabs();
        }
    }

    private void intentToTabs() {
        Log.v("tag", "intentToTabs");
        Intent intent = new Intent(this, TabHostFragmentActivity.class);
        startActivity(intent);
    }

    private void dataObtained(String secret) {
        Log.v("tag", "secret " + secret.length());
        Tools.addStringToPreferences(this, SEConstants.KEY_CUSTOMER_SECRET, secret);
        intentToTabs();
    }

}
