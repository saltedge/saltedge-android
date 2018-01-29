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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import com.saltedge.sdk.models.SEAccount;
import com.saltedge.sdk.models.comparators.SEAccountComparator;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.params.SETokenParams;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.AccountAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.Tools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;

import java.util.ArrayList;
import java.util.Collections;

public class AccountsFragment extends Fragment {

    private ProgressDialog progressDialog;
    private ArrayList<SEAccount> accounts;
    private String providerCode;
    private int loginId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        progressDialog = UITools.createProgressDialog(getActivity(), getActivity().getString(R.string.fetching_accounts));
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = this.getArguments();
        providerCode = bundle.getString(SEConstants.KEY_PROVIDER_CODE, "");
        loginId = bundle.getInt(SEConstants.KEY_LOGIN_ID, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_list_view, null);
        getAccounts();
        return mainView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UITools.destroyProgressDialog(progressDialog);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.token_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
                getFragmentManager().popBackStack();
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

    private void fetchRefreshToken() {
        String loginSecret = Tools.getStringFromPreferences(getActivity(), providerCode);
        String customerSecret = Tools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        UITools.showProgress(progressDialog);
        SETokenParams params = new SETokenParams(loginId, "", Constants.CALLBACK_URL, false, null);
        SERequestManager.getInstance().refreshToken(params, loginSecret, customerSecret,
                new SERequestManager.FetchListener() {

                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        UITools.failedParsing(getActivity(), errorResponse);
                    }

                    @Override
                    public void onSuccess(Object response) {
                        UITools.destroyAlertDialog(progressDialog);
                        urlObtained(response);
                    }
                });

    }

    private void fetchReconnectToken() {
        String loginSecret = Tools.getStringFromPreferences(getActivity(), providerCode);
        String customerSecret = Tools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        UITools.showProgress(progressDialog);
        SETokenParams params = new SETokenParams(loginId, "", Constants.CALLBACK_URL, false, null);
        SERequestManager.getInstance().reconnectToken(params, loginSecret, customerSecret,
                new SERequestManager.FetchListener() {
                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        UITools.failedParsing(getActivity(), errorResponse);
                    }

                    @Override
                    public void onSuccess(Object response) {
                        UITools.destroyAlertDialog(progressDialog);
                        urlObtained(response);
                    }
                });

    }

    private void deleteLogin() {
        String loginSecret = Tools.getStringFromPreferences(getActivity(), providerCode);
        String customerSecret = Tools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        UITools.showProgress(progressDialog);
        SERequestManager.getInstance().deleteLogin(loginSecret, customerSecret, new SERequestManager.FetchListener() {
            @Override
            public void onFailure(String errorResponse) {
                UITools.destroyAlertDialog(progressDialog);
                UITools.failedParsing(getActivity(), errorResponse);
            }

            @Override
            public void onSuccess(Object response) {
                UITools.destroyAlertDialog(progressDialog);
                getFragmentManager().popBackStack();
            }
        });
    }

    private void urlObtained(Object urlToGo) {
        getFragmentManager().popBackStack();
        Tools.addStringToPreferences(getActivity(), Constants.KEY_REFRESH_URL, (String) urlToGo);
        TabHost host = (TabHost) getActivity().findViewById(android.R.id.tabhost);
        host.setCurrentTab(0);
    }

    private void getAccounts() {
        String loginSecret = Tools.getStringFromPreferences(getActivity(), providerCode);
        String customerSecret = Tools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        if (TextUtils.isEmpty(loginSecret) || TextUtils.isEmpty(customerSecret)) {
            return;
        }
        accounts = new ArrayList<>();
        UITools.showProgress(progressDialog);
        SERequestManager.getInstance().listingAccounts(loginSecret, customerSecret, true, new SERequestManager.FetchListener() {
            @Override
            public void onFailure(String errorResponse) {
                UITools.destroyAlertDialog(progressDialog);
                UITools.failedParsing(getActivity(), errorResponse);
            }

            @Override
            public void onSuccess(Object response) {
                UITools.destroyAlertDialog(progressDialog);
                accounts = (ArrayList<SEAccount>) response;
                Collections.sort(accounts, new SEAccountComparator());
                populateAccounts();
            }
        });

    }

    private void failedParsing(String message) {
        UITools.showAlertDialog(getActivity(), message);
    }

    private void populateAccounts() {
        ListView listView = (ListView) getView().findViewById(R.id.listView);
        listView.setAdapter(new AccountAdapter(getActivity(), accounts));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SEAccount account= accounts.get(position);
                goToTransactions(account.getId());
            }
        });
    }

    private void goToTransactions(int accountId) {
        TransactionsFragment transactionsFragment = new TransactionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SEConstants.KEY_ACCOUNT_ID, accountId);
        bundle.putString(SEConstants.KEY_PROVIDER_CODE, providerCode);
        transactionsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, transactionsFragment).addToBackStack(null).commit();
    }
}
