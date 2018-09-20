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
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.saltedge.sdk.interfaces.DeleteLoginResult;
import com.saltedge.sdk.interfaces.FetchAccountsResult;
import com.saltedge.sdk.interfaces.RefreshLoginResult;
import com.saltedge.sdk.model.AccountData;
import com.saltedge.sdk.model.LoginData;
import com.saltedge.sdk.model.StageData;
import com.saltedge.sdk.network.ApiConstants;
import com.saltedge.sdk.network.SERefreshService;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.AccountAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AccountsActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, RefreshLoginResult, InputValueResult {

    private ProgressDialog progressDialog;
    private ArrayList<AccountData> accounts;
    private LoginData currentLogin;
    private BottomSheetDialog mBottomSheetDialog;
    private ListView listView;
    private TextView emptyView;
    private MenuItem refreshMenuItem;
    private String customerSecret = "";
    private SERefreshService refreshService;
    private boolean fetchingAccounts = false;

    public static Intent newIntent(Activity activity, LoginData login) {
        Intent intent = new Intent(activity, AccountsActivity.class);
        intent.putExtra(Constants.KEY_LOGIN, login);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(R.string.accounts);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setupViews();
        setInitialData();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchAccounts();
    }

    @Override
    public void onDestroy() {
        UITools.destroyProgressDialog(progressDialog);
        if (refreshService != null) {
            refreshService.cancel();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_accounts, menu);
        refreshMenuItem = menu.findItem(R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// Respond to the action bar's Up/Home button
                finish();
                return true;
            case R.id.action_delete:
                deleteLogin();
                return true;
            case R.id.action_reconnect:
                showConnectActivity(false);
                return true;
            case R.id.action_refresh:
                showConnectActivity(true);
                return true;
            case R.id.action_refresh_background:
                startRefreshInBackground();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.transactionsOption:
                showTransactionsList((String) view.getTag(), false);
                break;
            case R.id.pendingTransactionsOption:
                showTransactionsList((String) view.getTag(), true);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AccountData account= accounts.get(position);
        showAccountOptions(account.getId());
    }

    @Override
    public void onRefreshSuccess(LoginData login) {
        showShortToast("Login refreshed.");
        refreshMenuItem.setVisible(!accounts.isEmpty());
        if (login != null) {
            this.currentLogin = login;
        }
        if (!fetchingAccounts) {
            fetchAccounts();
        }
    }

    @Override
    public void onRefreshFailure(String errorMessage) {
        refreshMenuItem.setVisible(!accounts.isEmpty());
        showShortToast("Refresh error: " + errorMessage);
    }

    @Override
    public void onInteractiveStepFailure(String errorMessage) {
        //we can ignore interactive step error
        showShortToast(errorMessage);
    }

    @Override
    public void onLoginStateFetchError(String errorMessage) {
        //we can ignore intermediate login's fetch error
    }

    @Override
    public void provideInteractiveData(StageData lastStage) {
        if (lastStage.getInteractiveFieldsNames().length == 0) {
            refreshService.sendInteractiveData(new HashMap<String, Object>());
        } else {
            String inputFieldKey = lastStage.getInteractiveFieldsNames()[0];
            InputValueDialogHelper.showInputValueDialog(this, inputFieldKey, this);
        }
    }

    @Override
    public void inputValueResult(String inputFieldKey, String inputFieldValue) {
        if (TextUtils.isEmpty(inputFieldValue)) {
            showShortToast("Empty value not allowed");
        }
        HashMap<String, Object> credentials = new HashMap<>();
        credentials.put(inputFieldKey, inputFieldValue);
        refreshService.sendInteractiveData(credentials);
    }

    private void deleteLogin() {
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.removing_login));
        SERequestManager.getInstance().deleteLogin(customerSecret, currentLogin.getSecret(),
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
        try {
            PreferencesTools.removeLoginSecret(this, currentLogin.getId());
            UITools.destroyAlertDialog(progressDialog);
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchAccounts() {
        if (TextUtils.isEmpty(currentLogin.getSecret()) || TextUtils.isEmpty(customerSecret)) {
            return;
        }
        accounts = new ArrayList<>();
        UITools.destroyProgressDialog(progressDialog);
        fetchingAccounts = true;
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.fetching_accounts));
        SERequestManager.getInstance().fetchAccounts(customerSecret, currentLogin.getSecret(),
                new FetchAccountsResult() {
                    @Override
                    public void onSuccess(ArrayList<AccountData> accountsList) {
                        fetchingAccounts = false;
                        onFetchAccountsSuccess(accountsList);
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        fetchingAccounts = false;
                        onConnectionError(errorResponse);
                    }
        });
    }

    private void onFetchAccountsSuccess(ArrayList<AccountData> accountsList) {
        UITools.destroyAlertDialog(progressDialog);
        accounts = accountsList;
        updateViewsContent();
    }

    private void onConnectionError(String errorResponse) {
        UITools.destroyAlertDialog(progressDialog);
        updateViewsContent();
        UITools.showAlertDialog(this, errorResponse);
    }

    private void updateViewsContent() {
        refreshMenuItem.setVisible(!accounts.isEmpty());
        emptyView.setVisibility(accounts.isEmpty() ? View.VISIBLE : View.GONE);
        if (accounts.isEmpty()) {
            emptyView.setText(R.string.no_accounts);
        } else {
            listView.setAdapter(new AccountAdapter(this, accounts));
        }
    }

    private void setupViews() {
        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
        emptyView.setText(R.string.fetching_accounts);
        listView.setOnItemClickListener(this);
    }

    private void showAccountOptions(String accountId) {
        mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_account_options, null);
        View transactionsOption = sheetView.findViewById(R.id.transactionsOption);
        transactionsOption.setTag(accountId);
        transactionsOption.setOnClickListener(this);
        View pendingTransactionsOption = sheetView.findViewById(R.id.pendingTransactionsOption);
        pendingTransactionsOption.setTag(accountId);
        pendingTransactionsOption.setOnClickListener(this);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }

    private void showTransactionsList(String accountId, boolean showPendingTransactions) {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }
        if (accountId != null && !accountId.isEmpty()) {
            Intent transactionsIntent = TransactionsActivity.newIntent(this, accountId,
                    currentLogin.getSecret(), showPendingTransactions);
            startActivity(transactionsIntent);
        }
    }

    private void showConnectActivity(Boolean tryToRefresh) {
        try {
            boolean overrideCredentials = !tryToRefresh && currentLogin.getLastSuccessAt() == null;
            startActivityForResult(ConnectActivity.newIntent(this, currentLogin.getProviderCode(),
                    currentLogin.getSecret(), tryToRefresh, overrideCredentials), Constants.CONNECT_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRefreshInBackground() {
        if (TextUtils.isEmpty(currentLogin.getSecret()) || TextUtils.isEmpty(customerSecret)) {
            showShortToast("Internal error. Invalid refresh secrets");
        } else if (currentLogin.getNextRefreshPossibleAt() == null) {
            showLongToast("Refresh is not allowed. Please Reconnect");
        } else if (currentLogin.getNextRefreshPossibleAtDate().before(new Date())) {
            String[] scopes = ApiConstants.SCOPE_ACCOUNT_TRANSACTIONS;
            refreshMenuItem.setVisible(false);
            refreshService = SERequestManager.getInstance().refreshLoginWithSecret(customerSecret,
                    currentLogin, scopes, this);
        } else {
            showLongToast("Refresh is not allowed. Wait until " + currentLogin.getNextRefreshPossibleAtDate());
        }
    }

    private void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setInitialData() {
        currentLogin = (LoginData) this.getIntent().getSerializableExtra(Constants.KEY_LOGIN);
        customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
    }
}
