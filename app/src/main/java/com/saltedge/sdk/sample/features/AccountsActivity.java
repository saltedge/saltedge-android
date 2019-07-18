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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.saltedge.sdk.interfaces.DeleteEntryResult;
import com.saltedge.sdk.interfaces.FetchAccountsResult;
import com.saltedge.sdk.interfaces.RefreshConnectionResult;
import com.saltedge.sdk.model.SEAccount;
import com.saltedge.sdk.model.SEConnection;
import com.saltedge.sdk.model.SEStage;
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
        AdapterView.OnItemClickListener, RefreshConnectionResult, InputValueResult {

    private ProgressDialog progressDialog;
    private ArrayList<SEAccount> accounts;
    private SEConnection currentConnection;
    private BottomSheetDialog mBottomSheetDialog;
    private ListView listView;
    private TextView emptyView;
    private MenuItem refreshMenuItem;
    private String customerSecret = "";
    private SERefreshService refreshService;
    private boolean fetchingAccounts = false;

    public static Intent newIntent(Activity activity, SEConnection connection) {
        Intent intent = new Intent(activity, AccountsActivity.class);
        intent.putExtra(Constants.KEY_CONNECTION, connection);
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
                deleteConnection();
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
        SEAccount account= accounts.get(position);
        showAccountOptions(account.getId());
    }

    @Override
    public void onRefreshSuccess(SEConnection connection) {
        UITools.showShortToast(this, R.string.connection_refreshed);
        refreshMenuItem.setVisible(!accounts.isEmpty());
        if (connection != null) {
            this.currentConnection = connection;
        }
        if (!fetchingAccounts) {
            fetchAccounts();
        }
    }

    @Override
    public void onRefreshFailure(String errorMessage) {
        refreshMenuItem.setVisible(!accounts.isEmpty());
        UITools.showShortToast(this, "Refresh error: " + errorMessage);
    }

    @Override
    public void onInteractiveStepFailure(String errorMessage) {
        //we can ignore interactive step error
        UITools.showShortToast(this, errorMessage);
    }

    @Override
    public void onConnectionStateFetchError(String errorMessage) {
        //we can ignore intermediate connection's fetch error
    }

    @Override
    public void provideInteractiveData(SEStage lastStage) {
        if (lastStage.getInteractiveFieldsNames().length == 0) {
            refreshService.sendInteractiveData(new HashMap<>());
        } else {
            String inputFieldKey = lastStage.getInteractiveFieldsNames()[0];
            InputValueDialogHelper.showInputValueDialog(this, inputFieldKey, this);
        }
    }

    @Override
    public void inputValueResult(String inputFieldKey, String inputFieldValue) {
        if (TextUtils.isEmpty(inputFieldValue)) {
            UITools.showShortToast(this, R.string.empty_value_not_allowed);
        }
        HashMap<String, Object> credentials = new HashMap<>();
        credentials.put(inputFieldKey, inputFieldValue);
        refreshService.sendInteractiveData(credentials);
    }

    private void deleteConnection() {
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.removing_connection));
        SERequestManager.getInstance().deleteConnection(customerSecret, currentConnection.getSecret(),
                new DeleteEntryResult() {
                    @Override
                    public void onSuccess(Boolean entryIsRemoved, String entryId) {
                        if (entryIsRemoved) {
                            onDeleteConnectionSuccess(entryId);
                        }
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        onConnectionError(errorResponse);
                    }
        });
    }

    private void onDeleteConnectionSuccess(String entryId) {
        try {
            PreferencesTools.removeConnectionSecret(this, entryId);
            UITools.destroyAlertDialog(progressDialog);
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchAccounts() {
        if (TextUtils.isEmpty(currentConnection.getSecret()) || TextUtils.isEmpty(customerSecret)) {
            return;
        }
        accounts = new ArrayList<>();
        UITools.destroyProgressDialog(progressDialog);
        fetchingAccounts = true;
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.fetching_accounts));
        SERequestManager.getInstance().fetchAccounts(customerSecret, currentConnection.getSecret(),
                new FetchAccountsResult() {
                    @Override
                    public void onSuccess(ArrayList<SEAccount> accountsList) {
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

    private void onFetchAccountsSuccess(ArrayList<SEAccount> accountsList) {
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
                    currentConnection.getSecret(), showPendingTransactions);
            startActivity(transactionsIntent);
        }
    }

    private void showConnectActivity(Boolean tryToRefresh) {
        try {
            boolean overrideCredentials = !tryToRefresh && currentConnection.getLastSuccessAt() == null;
            startActivityForResult(ConnectActivity.newIntent(this, currentConnection.getProviderCode(),
                    currentConnection.getSecret(), tryToRefresh, overrideCredentials), Constants.CONNECT_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRefreshInBackground() {
        if (TextUtils.isEmpty(currentConnection.getSecret()) || TextUtils.isEmpty(customerSecret)) {
            UITools.showShortToast(this, R.string.error_invalid_refresh_secrets);
        } else if (currentConnection.getNextRefreshPossibleAt() == null) {
            UITools.showShortToast(this, R.string.refresh_not_allowed_reconnect);
        } else if (currentConnection.getNextRefreshPossibleAtDate().before(new Date())) {
            refreshMenuItem.setVisible(false);
            refreshService = SERequestManager.getInstance().refreshConnectionWithSecret(customerSecret,
                    currentConnection, Constants.CONSENT_SCOPES, this);
        } else {
            UITools.showShortToast(this, "Refresh is not allowed. Wait until " + currentConnection.getNextRefreshPossibleAtDate());
        }
    }

    private void setInitialData() {
        currentConnection = (SEConnection) this.getIntent().getSerializableExtra(Constants.KEY_CONNECTION);
        customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
    }
}
