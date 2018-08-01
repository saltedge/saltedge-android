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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.saltedge.sdk.interfaces.FetchLoginsResult;
import com.saltedge.sdk.interfaces.ProvidersResult;
import com.saltedge.sdk.model.LoginData;
import com.saltedge.sdk.model.ProviderData;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.BuildConfig;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.LoginsAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;

import java.util.ArrayList;
import java.util.List;

public class LoginsFragment extends Fragment implements ProvidersDialog.ProviderSelectListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    private ProgressDialog progressDialog;
    private ArrayList<ProviderData> providers;
    private ArrayList<LoginData> loginsList;
    private String applicationLanguage = "";
    private ListView listView;
    private TextView emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        applicationLanguage = getResources().getConfiguration().locale.getLanguage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = getView().findViewById(R.id.listView);
        emptyView = getView().findViewById(R.id.emptyView);
        listView.setOnItemClickListener(this);
        emptyView.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateViewData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UITools.destroyProgressDialog(progressDialog);
    }

    @Override
    public void onProviderSelected(ProviderData provider) {
        Toast.makeText(getActivity(), "Selected " + String.valueOf(provider.getName()), Toast.LENGTH_SHORT).show();
        showConnectActivity(provider.getCode());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_provider, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_provider_list:
                fetchAndShowProviders();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        String[] loginSecretArray = PreferencesTools.getLoginSecrets(getActivity());
        if (loginSecretArray.length == 0) {
            fetchAndShowProviders();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        LoginData login = loginsList.get(position);
        showLoginAccounts(login);
    }

    private void updateViewData() {
        String[] loginSecretArray = PreferencesTools.getLoginSecrets(getActivity());

        loginsList = new ArrayList<>();
        emptyView.setVisibility(View.VISIBLE);
        if (loginSecretArray.length == 0) {
            emptyView.setText(R.string.no_logins);
        } else {
            emptyView.setText(R.string.fetching_logins);
            fetchLogins(loginSecretArray);
        }
    }

    private void fetchLogins(String[] loginSecretsArray) {
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(getActivity(), getString(R.string.fetching_logins));
        String customerSecret = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().fetchLogins(loginSecretsArray, customerSecret, new FetchLoginsResult() {
                    @Override
                    public void onSuccess(List<LoginData> logins) {
                        UITools.destroyAlertDialog(progressDialog);
                        loginsList = new ArrayList<>(logins);
                        updateProvidersList();
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        UITools.showAlertDialog(getActivity(), errorResponse);
                    }
                });
    }

    private void updateProvidersList() {
        Context context = getActivity();
        if (context != null) {
            emptyView.setVisibility(View.GONE);
            listView.setAdapter(new LoginsAdapter(context, loginsList));
        }
    }

    private void showLoginAccounts(LoginData loginData) {
        Intent intent = AccountsActivity.newIntent(getActivity(), loginData);
        startActivity(intent);
    }

    private void fetchAndShowProviders() {
        if (providers == null || providers.isEmpty()) {
            UITools.destroyProgressDialog(progressDialog);
            progressDialog = UITools.showProgressDialog(getActivity(), getString(R.string.fetching_providers));
            String countryCode = (BuildConfig.DEBUG) ? "XF" : applicationLanguage;
            SERequestManager.getInstance().fetchProviders(countryCode, new ProvidersResult() {
                @Override
                public void onSuccess(ArrayList<ProviderData> providersList) {
                    onFetchProvidersSuccess(providersList);
                }

                @Override
                public void onFailure(String errorMessage) {
                    onFetchError(errorMessage);
                }});
        } else {
            showProvidersListDialog();
        }
    }

    private void onFetchError(String errorMessage) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.showAlertDialog(getActivity(), errorMessage);
    }

    private void onFetchProvidersSuccess(ArrayList<ProviderData> providersList) {
        UITools.destroyAlertDialog(progressDialog);
        providers = providersList;
        showProvidersListDialog();
    }

    private void showProvidersListDialog() {
        if (providers != null && !providers.isEmpty()) {
            Toast.makeText(getActivity(), "Fetched " + String.valueOf(providers.size()) + " providers", Toast.LENGTH_SHORT).show();
            if (isVisible()) {
                ProvidersDialog.newInstance(providers, this).show(getFragmentManager(), "");
            }
        } else {
            if (isVisible()) {
                UITools.showAlertDialog(getActivity(), getString(R.string.providers_empty));
            }
        }
    }

    private void showConnectActivity(String providerCode) {
        try {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.startActivityForResult(ConnectActivity.newIntent(getActivity(), providerCode), Constants.CONNECT_REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
