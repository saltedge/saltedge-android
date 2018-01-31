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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import com.saltedge.sdk.interfaces.DeleteLoginResult;
import com.saltedge.sdk.interfaces.FetchAccountsResult;
import com.saltedge.sdk.interfaces.TokenConnectionResult;
import com.saltedge.sdk.model.AccountData;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.AccountAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;

import java.util.ArrayList;

public class AccountsActivity extends AppCompatActivity implements TokenConnectionResult {

    private ProgressDialog progressDialog;
    private ArrayList<AccountData> accounts;
    private String providerCode;
    private String loginId;

    public static Intent newIntent(Activity activity, String loginId, String providerCode) {
        Intent intent = new Intent(activity, AccountsActivity.class);
        intent.putExtra(SEConstants.KEY_LOGIN_ID, loginId);
        intent.putExtra(SEConstants.KEY_PROVIDER_CODE, providerCode);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list_view);
        getSupportActionBar().setTitle(R.string.accounts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setInitialData();
    }

    private void setInitialData() {
        Intent intent = this.getIntent();
        providerCode = intent.getStringExtra(SEConstants.KEY_PROVIDER_CODE);
        loginId = intent.getStringExtra(SEConstants.KEY_LOGIN_ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchAccounts();
    }

    @Override
    public void onDestroy() {
        UITools.destroyProgressDialog(progressDialog);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_accounts, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                fetchRefreshToken();
                return true;
            case R.id.action_reconnect:
                fetchReconnectToken();
                return true;
            case R.id.action_delete:
                deleteLogin();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(String connectUrl) {
        UITools.destroyAlertDialog(progressDialog);
        onConnectUrlFetchSuccess(connectUrl);
    }

    @Override
    public void onFailure(String errorMessage) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.failedParsing(this, errorMessage);
    }

    private void fetchRefreshToken() {
        String loginSecret = PreferencesTools.getStringFromPreferences(this, providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.fetching_accounts));
        String locale = "";
        String callbackUrl = Constants.CALLBACK_URL;
        SERequestManager.getInstance().refreshToken(locale, callbackUrl, loginSecret, customerSecret, this);
    }

    private void fetchReconnectToken() {
        String loginSecret = PreferencesTools.getStringFromPreferences(this, providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.fetching_accounts));
        String locale = "";
        String callbackUrl = Constants.CALLBACK_URL;
        SERequestManager.getInstance().reconnectToken(locale, callbackUrl, loginSecret, customerSecret, this);
    }

    private void deleteLogin() {
        String loginSecret = PreferencesTools.getStringFromPreferences(this, providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.removing_login));
        SERequestManager.getInstance().deleteLogin(loginSecret, customerSecret,
                new DeleteLoginResult() {
                    @Override
                    public void onSuccess(Boolean isRemoved) {
                        if (isRemoved) {
                            onDeleteLoginSuccess();
                        }
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        onConnectionError(errorResponse);
                    }
        });
    }

    private void onDeleteLoginSuccess() {
        PreferencesTools.removeLoginSecret(this, providerCode);
        UITools.destroyAlertDialog(progressDialog);
        getFragmentManager().popBackStack();
    }

    private void onConnectUrlFetchSuccess(Object urlToGo) {
        getFragmentManager().popBackStack();
        PreferencesTools.putStringToPreferences(this, Constants.KEY_REFRESH_URL, (String) urlToGo);
        TabHost host = this.findViewById(android.R.id.tabhost);
        host.setCurrentTab(0);
    }

    private void fetchAccounts() {
        String loginSecret = PreferencesTools.getStringFromPreferences(this, providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        if (TextUtils.isEmpty(loginSecret) || TextUtils.isEmpty(customerSecret)) {
            return;
        }
        accounts = new ArrayList<>();
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.fetching_accounts));
        SERequestManager.getInstance().fetchAccounts(customerSecret, loginSecret,
                new FetchAccountsResult() {
                    @Override
                    public void onSuccess(ArrayList<AccountData> accountsList) {
                        onFetchAccountsSuccess(accountsList);
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        onConnectionError(errorResponse);
                    }
        });
    }

    private void onFetchAccountsSuccess(ArrayList<AccountData> accountsList) {
        UITools.destroyAlertDialog(progressDialog);
        accounts = accountsList;
        populateAccounts();
    }

    private void onConnectionError(String errorResponse) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.failedParsing(this, errorResponse);
    }

    private void populateAccounts() {
        ListView listView = findViewById(R.id.listView);
        if (listView == null) return;
        listView.setAdapter(new AccountAdapter(this, accounts));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AccountData account= accounts.get(position);
                goToTransactions(account.getId());
            }
        });
    }

    private void goToTransactions(String accountId) {
        Intent transactionsIntent = TransactionsActivity.newIntent(this, accountId, providerCode);
        startActivity(transactionsIntent);
    }
}
