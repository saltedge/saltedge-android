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
package com.saltedge.sdk.sample.tabs;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.saltedge.sdk.connector.TransactionsConnector;
import com.saltedge.sdk.model.TransactionData;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.TransactionAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;

import java.util.ArrayList;

public class TransactionsFragment extends Fragment {

    private ProgressDialog progressDialog;
    private ArrayList<TransactionData> transactions;
    private String accountId;
    private String providerCode;

    public static TransactionsFragment newInstance(String accountId, String providerCode) {
        Bundle args = new Bundle();
        args.putString(SEConstants.KEY_ACCOUNT_ID, accountId);
        args.putString(SEConstants.KEY_PROVIDER_CODE, providerCode);
        TransactionsFragment fragment = new TransactionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setInitialData();
    }

    private void setInitialData() {
        Bundle bundle = this.getArguments();
        if (bundle == null) return;
        accountId = bundle.getString(SEConstants.KEY_ACCOUNT_ID, "");
        providerCode = bundle.getString(SEConstants.KEY_PROVIDER_CODE, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_list_view, null);
        getTransactions();
        return mainView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UITools.destroyProgressDialog(progressDialog);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTransactions() {
        String loginSecret = PreferencesTools.getStringFromPreferences(getActivity(), providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        transactions = new ArrayList<>();
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(getActivity(), getActivity().getString(R.string.fetching_transactions));
        SERequestManager.getInstance().listingTransactionsOfAccount(customerSecret, loginSecret, accountId,
                new TransactionsConnector.Result() {
                    @Override
                    public void onSuccess(ArrayList<TransactionData> transactionsList) {
                        UITools.destroyAlertDialog(progressDialog);
                        transactions = transactionsList;
                        populateTransactions();
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        UITools.failedParsing(getActivity(), errorResponse);
                    }
        });
    }

    private void populateTransactions() {
        ListView listView = getView().findViewById(R.id.listView);
        listView.setAdapter(new TransactionAdapter(getActivity(), transactions));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

}
