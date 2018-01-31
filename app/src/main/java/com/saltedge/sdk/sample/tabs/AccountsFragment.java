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
import android.support.v7.app.AppCompatActivity;
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

import com.saltedge.sdk.connector.AccountsConnector;
import com.saltedge.sdk.connector.DeleteLoginConnector;
import com.saltedge.sdk.connector.TokenConnector;
import com.saltedge.sdk.model.AccountData;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.AccountAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;
import com.saltedge.sdk.utils.SEConstants;

import java.util.ArrayList;

public class AccountsFragment extends Fragment implements TokenConnector.Result {

    private ProgressDialog progressDialog;
    private ArrayList<AccountData> accounts;
    private String providerCode;
    private String loginId;

    public static AccountsFragment newInstance(String loginId, String providerCode) {
        Bundle args = new Bundle();
        args.putString(SEConstants.KEY_LOGIN_ID, loginId);
        args.putString(SEConstants.KEY_PROVIDER_CODE, providerCode);
        AccountsFragment fragment = new AccountsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setInitialValues();
    }

    private void setInitialValues() {
        Bundle bundle = this.getArguments();
        if (bundle == null) return;
        providerCode = bundle.getString(SEConstants.KEY_PROVIDER_CODE, "");
        loginId = bundle.getString(SEConstants.KEY_LOGIN_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        getAccounts();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UITools.destroyProgressDialog(progressDialog);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_accounts, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    public void onSuccess(String connectUrl) {
        UITools.destroyAlertDialog(progressDialog);
        urlObtained(connectUrl);
    }

    @Override
    public void onFailure(String errorMessage) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.failedParsing(getActivity(), errorMessage);
    }

    private void fetchRefreshToken() {
        String loginSecret = PreferencesTools.getStringFromPreferences(getActivity(), providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(getActivity(), getActivity().getString(R.string.fetching_accounts));
        String locale = "";
        String callbackUrl = Constants.CALLBACK_URL;
        SERequestManager.getInstance().refreshToken(locale, callbackUrl, loginSecret, customerSecret, this);
    }

    private void fetchReconnectToken() {
        String loginSecret = PreferencesTools.getStringFromPreferences(getActivity(), providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(getActivity(), getActivity().getString(R.string.fetching_accounts));
        String locale = "";
        String callbackUrl = Constants.CALLBACK_URL;
        SERequestManager.getInstance().reconnectToken(locale, callbackUrl, loginSecret, customerSecret, this);
    }

    private void deleteLogin() {
        String loginSecret = PreferencesTools.getStringFromPreferences(getActivity(), providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(getActivity(), getActivity().getString(R.string.removing_login));
        SERequestManager.getInstance().deleteLogin(loginSecret, customerSecret,
                new DeleteLoginConnector.Result() {
                    @Override
                    public void onSuccess(Boolean isRemoved) {
                        UITools.destroyAlertDialog(progressDialog);
                        getFragmentManager().popBackStack();
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        UITools.failedParsing(getActivity(), errorResponse);
                    }
        });
    }

    private void urlObtained(Object urlToGo) {
        getFragmentManager().popBackStack();
        PreferencesTools.putStringToPreferences(getActivity(), Constants.KEY_REFRESH_URL, (String) urlToGo);
        TabHost host = getActivity().findViewById(android.R.id.tabhost);
        host.setCurrentTab(0);
    }

    private void getAccounts() {
        String loginSecret = PreferencesTools.getStringFromPreferences(getActivity(), providerCode);
        String customerSecret = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        if (TextUtils.isEmpty(loginSecret) || TextUtils.isEmpty(customerSecret)) {
            return;
        }
        accounts = new ArrayList<>();
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(getActivity(), getActivity().getString(R.string.fetching_accounts));
        SERequestManager.getInstance().fetchAccounts(customerSecret, loginSecret,
                new AccountsConnector.Result() {
                    @Override
                    public void onSuccess(ArrayList<AccountData> accountsList) {
                        UITools.destroyAlertDialog(progressDialog);
                        accounts = accountsList;
                        populateAccounts();
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        UITools.failedParsing(getActivity(), errorResponse);
                    }
        });
    }

    private void populateAccounts() {
        View fragmentView = getView();
        if (fragmentView == null) return;
        ListView listView = fragmentView.findViewById(R.id.listView);
        if (listView == null) return;
        listView.setAdapter(new AccountAdapter(getActivity(), accounts));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AccountData account= accounts.get(position);
                goToTransactions(account.getId());
            }
        });
    }

    private void goToTransactions(String accountId) {
        TransactionsFragment fragment = TransactionsFragment.newInstance(accountId, providerCode);
        try {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragment).addToBackStack(null).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
