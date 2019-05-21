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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.saltedge.sdk.interfaces.FetchConnectionsResult;
import com.saltedge.sdk.interfaces.ProvidersResult;
import com.saltedge.sdk.model.SEConnection;
import com.saltedge.sdk.model.SEProvider;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.ConnectionsAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;

import java.util.ArrayList;
import java.util.List;

public class ConnectionsFragment extends Fragment implements ProvidersDialog.ProviderSelectListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    private ProgressDialog progressDialog;
    private ArrayList<SEProvider> providers;
    private ArrayList<SEConnection> connectionsList;
    private String applicationCountryCode = "XF";
    private ListView listView;
    private TextView emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        applicationCountryCode = getResources().getConfiguration().locale.getCountry();
;    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View fragmentView = getView();
        if (fragmentView != null) {
            listView = fragmentView.findViewById(R.id.listView);
            emptyView = fragmentView.findViewById(R.id.emptyView);
            listView.setOnItemClickListener(this);
            emptyView.setOnClickListener(this);
        }
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
    public void onProviderSelected(SEProvider provider) {
        UITools.showShortToast(getActivity(), "Selected " + provider.getName());
        showConnectActivity(provider.getCode());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_provider, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_provider_list) {
            fetchAndShowProviders();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        String[] connectionsSecretArray = PreferencesTools.getConnectionSecrets(getActivity());
        if (connectionsSecretArray.length == 0) {
            fetchAndShowProviders();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        SEConnection connection = connectionsList.get(position);
        showConnectionAccounts(connection);
    }

    private void updateViewData() {
        String[] connectionsSecretArray = PreferencesTools.getConnectionSecrets(getActivity());

        connectionsList = new ArrayList<>();
        emptyView.setVisibility(View.VISIBLE);
        if (connectionsSecretArray.length == 0) {
            emptyView.setText(R.string.no_connections);
        } else {
            emptyView.setText(R.string.fetching_connections);
            fetchConnections(connectionsSecretArray);
        }
    }

    private void fetchConnections(String[] connectionsSecretsArray) {
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(getActivity(), getString(R.string.fetching_connections));
        String customerSecret = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().fetchConnections(customerSecret, connectionsSecretsArray,
                new FetchConnectionsResult() {
                    @Override
                    public void onSuccess(List<SEConnection> connections) {
                        UITools.destroyAlertDialog(progressDialog);
                        connectionsList = new ArrayList<>(connections);
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
            listView.setAdapter(new ConnectionsAdapter(context, connectionsList));
        }
    }

    private void showConnectionAccounts(SEConnection connection) {
        Intent intent = AccountsActivity.newIntent(getActivity(), connection);
        startActivity(intent);
    }

    private void fetchAndShowProviders() {
        if (providers == null || providers.isEmpty()) {
            UITools.destroyProgressDialog(progressDialog);
            progressDialog = UITools.showProgressDialog(getActivity(), getString(R.string.fetching_providers));
            String countryCode = applicationCountryCode;//"XF" for fake providers
            SERequestManager.getInstance().fetchProviders(countryCode, new ProvidersResult() {
                @Override
                public void onSuccess(ArrayList<SEProvider> providersList) {
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

    private void onFetchProvidersSuccess(ArrayList<SEProvider> providersList) {
        UITools.destroyAlertDialog(progressDialog);
        providers = providersList;
        showProvidersListDialog();
    }

    private void showProvidersListDialog() {
        if (providers != null && !providers.isEmpty()) {
            UITools.showShortToast(getActivity(), "Fetched " + providers.size());
            if (isVisible()) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    ProvidersDialog.newInstance(providers, this).show(fragmentManager, "");
                }
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
