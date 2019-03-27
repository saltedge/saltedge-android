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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.saltedge.sdk.interfaces.FetchTransactionsResult;
import com.saltedge.sdk.model.SETransaction;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.TransactionsAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;

import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, FetchTransactionsResult {

    private ProgressDialog progressDialog;
    private ArrayList<SETransaction> transactions;
    private String accountId;
    private String connectionSecret;
    private boolean pendingTransactionsMode = false;

    public static Intent newIntent(Activity activity, String accountId, String connectionSecret,
                                   boolean showPendingTransactions) {
        Intent intent = new Intent(activity, TransactionsActivity.class);
        intent.putExtra(SEConstants.KEY_ACCOUNT_ID, accountId);
        intent.putExtra(Constants.KEY_CONNECTION_SECRET, connectionSecret);
        intent.putExtra(Constants.KEY_PENDING, showPendingTransactions);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.transactions);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setInitialData();
    }

    private void setInitialData() {
        Intent intent = this.getIntent();
        accountId = intent.getStringExtra(SEConstants.KEY_ACCOUNT_ID);
        connectionSecret = intent.getStringExtra(Constants.KEY_CONNECTION_SECRET);
        pendingTransactionsMode = intent.getBooleanExtra(Constants.KEY_PENDING, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchTransactions();
    }

    @Override
    public void onDestroy() {
        UITools.destroyProgressDialog(progressDialog);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String message = "Click ListItem Number " + String.valueOf(i);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(ArrayList<SETransaction> transactionsList) {
        UITools.destroyAlertDialog(progressDialog);
        transactions = transactionsList;
        updateTransactionsList();
    }

    @Override
    public void onFailure(String errorResponse) {
        onConnectFail(errorResponse);
    }

    private void fetchTransactions() {
        String customerSecret = PreferencesTools.getStringFromPreferences(this, Constants.KEY_CUSTOMER_SECRET);
        transactions = new ArrayList<>();
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(this, this.getString(R.string.fetching_transactions));
        if (pendingTransactionsMode) {
            SERequestManager.getInstance().fetchPendingTransactionsOfAccount(customerSecret, connectionSecret, accountId, this);
        } else {
            SERequestManager.getInstance().fetchAllTransactions(customerSecret, connectionSecret, accountId, this);
        }
    }

    private void onConnectFail(String errorResponse) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.showAlertDialog(this, errorResponse);
    }

    private void updateTransactionsList() {
        try {
            ListView listView = findViewById(R.id.listView);
            TextView emptyView = findViewById(R.id.emptyView);
            if (transactions.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(pendingTransactionsMode ? R.string.no_pending_transactions : R.string.no_transactions);
            } else {
                emptyView.setVisibility(View.GONE);
                listView.setAdapter(new TransactionsAdapter(this, transactions));
                listView.setOnItemClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
