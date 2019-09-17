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

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.interfaces.CreateCustomerResult;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferenceRepository;
import com.saltedge.sdk.sample.utils.UITools;

import java.util.Date;

import static com.saltedge.sdk.sample.utils.UITools.refreshProgressDialog;

/**
 * First Activity.
 * Trying to register new customer if not exist.
 */
public class StartActivity extends AppCompatActivity {

    private final static String customerIdentifierPrefix = "ANDROID_APP_DEV_IDENTIFIER_"; // Random name, each installation - new name
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (SaltEdgeSDK.isNotPartner()) {
            tryToCreateCustomer();
        } else {
            showConnectionsActivity();
        }
    }

    private void tryToCreateCustomer() {
        String customerIdentifier = PreferenceRepository.getStringFromPreferences(Constants.KEY_CUSTOMER_IDENTIFIER);
        String customerSecret = PreferenceRepository.getStringFromPreferences(Constants.KEY_CUSTOMER_SECRET);

        if (customerIdentifier.isEmpty()) {
            customerIdentifier = customerIdentifierPrefix + new Date().getTime();
            PreferenceRepository.putStringToPreferences(Constants.KEY_CUSTOMER_IDENTIFIER, customerIdentifier);
        }

        if (TextUtils.isEmpty(customerSecret)) {
            progressDialog = UITools.showProgressDialog(this, getString(R.string.creating_customer));
            SERequestManager.getInstance().createCustomer(customerIdentifier, new CreateCustomerResult() {
                @Override
                public void onSuccess(String secret) {
                    onCreateCustomerSuccess(secret);
                }

                @Override
                public void onFailure(String errorResponse) {
                    onCreateCustomerFailure(errorResponse);
                }
            });
        } else {
            showConnectionsActivity();
        }
    }

    private void onCreateCustomerFailure(String errorResponse) {
        refreshProgressDialog(this, progressDialog, errorResponse);
    }

    private void onCreateCustomerSuccess(String customerSecret) {
        UITools.destroyAlertDialog(progressDialog);
        PreferenceRepository.putStringToPreferences(Constants.KEY_CUSTOMER_SECRET, customerSecret);
        showConnectionsActivity();
    }

    private void showConnectionsActivity() {
        try {
            String dataString = getIntent().getDataString();
            if (dataString != null) {
                Uri uri = Uri.parse(dataString);
                String connectionSecret = uri.getQueryParameter(Constants.KEY_CONNECTION_SECRET);
                if (connectionSecret != null && !connectionSecret.isEmpty()) {
                    PreferenceRepository.putConnectionSecret(connectionSecret);
                }
            }
            startActivity(new Intent(this, ConnectionsActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
