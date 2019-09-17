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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.saltedge.sdk.interfaces.FetchTransactionsResult;
import com.saltedge.sdk.model.SETransaction;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.TransactionsAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferenceRepository;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;

import java.util.ArrayList;
import java.util.List;

import static com.saltedge.sdk.sample.utils.UITools.refreshProgressDialog;

public class TransactionsActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, FetchTransactionsResult {

    private enum ViewMode {
        POSTED_TRANSACTIONS, PENDING_TRANSACTIONS, DUPLICATED_TRANSACTIONS
    }

    private ProgressDialog progressDialog;
    private ArrayList<SETransaction> transactions;
    private String accountId;
    private String connectionSecret;
    private String customerSecret = "";
    private ViewMode viewMode = ViewMode.POSTED_TRANSACTIONS;

    public static Intent newIntent(Activity activity, String accountId, String connectionSecret) {
        Intent intent = new Intent(activity, TransactionsActivity.class);
        intent.putExtra(SEConstants.KEY_ACCOUNT_ID, accountId);
        intent.putExtra(Constants.KEY_CONNECTION_SECRET, connectionSecret);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        updateActivityTitle();
        setInitialData();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transactions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// Respond to the action bar's Up/Home button
                finish();
                return true;
            case R.id.show_posted_transactions:
                tryToUpdateViewMode(ViewMode.POSTED_TRANSACTIONS);
                return true;
            case R.id.show_pending_transactions:
                tryToUpdateViewMode(ViewMode.PENDING_TRANSACTIONS);
                return true;
            case R.id.show_duplicated_transactions:
                tryToUpdateViewMode(ViewMode.DUPLICATED_TRANSACTIONS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //TODO
    }

    @Override
    public void onSuccess(List<SETransaction> transactionsList) {
        UITools.destroyAlertDialog(progressDialog);
        transactions = new ArrayList<>(transactionsList);
        updateTransactionsList();
    }

    @Override
    public void onFailure(String errorResponse) {
        onConnectFail(errorResponse);
    }

    private void fetchTransactions() {
        progressDialog = refreshProgressDialog(this, progressDialog, R.string.fetching_transactions);

        transactions = new ArrayList<>();

        switch (viewMode) {
            case POSTED_TRANSACTIONS:
                SERequestManager.getInstance().fetchAllTransactions(customerSecret, connectionSecret, accountId, this);
                break;
            case PENDING_TRANSACTIONS:
                SERequestManager.getInstance().fetchPendingTransactionsOfAccount(customerSecret, connectionSecret, accountId, this);
                break;
            case DUPLICATED_TRANSACTIONS:
                SERequestManager.getInstance().fetchDuplicatedTransactionsOfAccount(customerSecret, connectionSecret, accountId, this);
                break;
        }
    }

    private void onConnectFail(String errorResponse) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.showAlertDialog(this, errorResponse);
    }

    private void updateTransactionsList() {
        try {
            ListView listView = findViewById(R.id.listView);
            View emptyView = findViewById(R.id.emptyView);
            emptyView.setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);
            TextView emptyLabelView = findViewById(R.id.emptyLabelView);
            if (transactions.isEmpty()) {
                emptyLabelView.setText(getEmptyViewTitleResId());
            } else {
                listView.setAdapter(new TransactionsAdapter(this, transactions));
                listView.setOnItemClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInitialData() {
        Intent intent = this.getIntent();
        accountId = intent.getStringExtra(SEConstants.KEY_ACCOUNT_ID);
        connectionSecret = intent.getStringExtra(Constants.KEY_CONNECTION_SECRET);
        customerSecret = PreferenceRepository.getStringFromPreferences(Constants.KEY_CUSTOMER_SECRET);
    }

    private void tryToUpdateViewMode(ViewMode newViewMode) {
        if (viewMode != newViewMode) {
            viewMode = newViewMode;
            updateActivityTitle();
            fetchTransactions();
        }
    }

    private void updateActivityTitle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            switch (viewMode) {
                case POSTED_TRANSACTIONS:
                    actionBar.setTitle(R.string.transactions);
                    break;
                case PENDING_TRANSACTIONS:
                    actionBar.setTitle(R.string.pending_transactions);
                    break;
                case DUPLICATED_TRANSACTIONS:
                    actionBar.setTitle(R.string.duplicated_transactions);
                    break;
            }
        }
    }

    private int getEmptyViewTitleResId() {
        switch (viewMode) {
            case POSTED_TRANSACTIONS:
                return R.string.no_transactions;
            case PENDING_TRANSACTIONS:
                return R.string.no_pending_transactions;
            case DUPLICATED_TRANSACTIONS:
                return R.string.no_duplicated_transactions;
        }
        return R.string.no_transactions;
    }
}
