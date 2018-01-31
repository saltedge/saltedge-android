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
package com.saltedge.sdk.sample.features;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.interfaces.CreateCustomerResult;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;

import java.util.Date;

public class StartActivity extends AppCompatActivity {

    private final static String customerIdentifierPrefix = "ANDROID_APP_EXAMPLE_IDENTIFIER"; // Random name, each installation - new name
    private final static String clientAppId = "";//TODO SET APP ID
    private final static String clientAppSecret = "";//TODO SET APP SECRET
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        setupSaltEdgeSDK();
        createCustomer();
    }

    private void setupSaltEdgeSDK() {
        SaltEdgeSDK.getInstance().init(this.getApplicationContext(), clientAppId, clientAppSecret);
    }

    private void createCustomer() {
        progressDialog = UITools.showProgressDialog(this, getString(R.string.creating_customer));
        String customerIdentifier = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_IDENTIFIER);
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);

        if (customerIdentifier.isEmpty()) {
            customerIdentifier = customerIdentifierPrefix + String.valueOf(new Date().getTime());
            PreferencesTools.putStringToPreferences(this, Constants.KEY_CUSTOMER_IDENTIFIER, customerIdentifier);
        }

        if (TextUtils.isEmpty(customerSecret)) {
            SERequestManager.getInstance().createCustomer(customerIdentifier, new CreateCustomerResult() {
                @Override
                public void onSuccess(String secret) {
                    onSecretCreateSuccess(secret);
                }

                @Override
                public void onFailure(String errorResponse) {
                    onCreateCustomerFailure(errorResponse);
                }
            });
        } else {
            showTabsActivity();
        }
    }

    private void onCreateCustomerFailure(String errorResponse) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.failedParsing(StartActivity.this, errorResponse);
    }

    private void onSecretCreateSuccess(String secret) {
        UITools.destroyAlertDialog(progressDialog);
        PreferencesTools.putStringToPreferences(this, Constants.KEY_CUSTOMER_SECRET, secret);
        showTabsActivity();
    }

    private void showTabsActivity() {
        try {
            Intent intent = new Intent(this, TabHostFragmentActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
